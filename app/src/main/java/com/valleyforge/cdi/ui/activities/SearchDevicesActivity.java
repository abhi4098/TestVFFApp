package com.valleyforge.cdi.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.valleyforge.cdi.R;
import com.valleyforge.cdi.ui.adapters.ConnectionTypeAdapter;
import com.valleyforge.cdi.utils.PrefUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.ErrorDefinitions;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.PermissionException;
import ch.leica.sdk.LeicaSdk;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Logging.Logs;
import ch.leica.sdk.Types;
import ch.leica.sdk.Utilities.WifiHelper;

//import ch.leica.api.*;


/**
 * This is the main activity which handles finding available devices and holds the list of available devices.
 */
public class SearchDevicesActivity extends AppCompatActivity implements DeviceManager.FoundAvailableDeviceListener, Device.ConnectionListener, ErrorListener {

	/**
	 * ClassName
	 */
	private final String CLASSTAG = SearchDevicesActivity.class.getSimpleName();

	/**
	 * UI holding the available devices
	 */
	ListView deviceList;
	ConnectionTypeAdapter connectionTypeAdapter;
	/**
	 * List with all the devices available in BLE and wifi mode
	 */
	List<Device> availableDevices = new ArrayList<>();

	//ui-alterts to present errors to users
	AlertDialog activateWifiDialog = null;
	AlertDialog connectingDialog = null;
	AlertDialog activateBluetoothDialog = null;
	String measurementGridStatus;

	// for finding and connecting to a device
	DeviceManager deviceManager;
	boolean findDevicesRunning = false;
	@BindView(R.id.back_icon)
	ImageView ivBackIcon;
	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.tv_app_title)
	TextView tvAppTitle;
	@BindView(R.id.logout)
	ImageView ivLogout;



	@BindView(R.id.status)
	TextView tvGoToDashboard;

	/*@OnClick(R.id.skip_device_connection)
            public void skipConnection(){
		if (deviceManager != null){
			//Disconnect from all the connected devices
			for (Device connectedDevice: deviceManager.getConnectedDevices() ){
				connectedDevice.disconnect();


			}

		}

        Intent informationIntent = new Intent(SearchDevicesActivity.this, MeasurementGridActivity.class);
        startActivity(informationIntent);

    }*/
	/**
	 * Current selected device
	 */
	Device currentDevice;

	// needed for connection timeout
	Timer connectionTimeoutTimer;
	TimerTask connectionTimeoutTask;

	// to do infinite rounds of finding devices
	Timer findDevicesTimer;
	boolean activityStopped = true;

	// to handle info dialog at app start and only at app start
	// has to be static, otherwise the alert will be displayed more than one time
	static boolean searchInfoShown = false;

	// to handle user cancel connection attempt
	Map<Device,Boolean> connectionAttempts = new HashMap<>();
	Device currentConnectionAttemptToDevice = null;

	private TextView version;


	/**
	 * when closing main activity, disconnect from all devices
	 */
	@Override
	public void onBackPressed() {

		final String METHODTAG = ".onBackPressed";
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(" Go back to previous screen?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				if (deviceManager != null){
					//Disconnect from all the connected devices
					for (Device connectedDevice: deviceManager.getConnectedDevices() ){
						connectedDevice.disconnect();
						Log.d(CLASSTAG, METHODTAG + "Disconnected Device: "+connectedDevice.modelName);
					}

				}
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				// do nothing
			}
		});

		builder.create().show();
	}

	/**
	 * Inits device list
	 * */
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_devices);

		ButterKnife.bind(this);
		ivBackIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		ivLogout.setVisibility(View.GONE);
		tvGoToDashboard.setVisibility(View.VISIBLE);
		tvGoToDashboard.setText("Dashboard");


		tvGoToDashboard.setTextColor(Color.parseColor("#252525"));
		PrefUtils.storeMeasurementGrid("0",this);
		tvGoToDashboard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchDevicesActivity.this,NavigationActivity.class);
				startActivity(intent);
				finish();
			}
		});
		tvAppTitle.setText("Searching Devices . . .");
		deviceList = (ListView) findViewById(R.id.devices);

		//Shows the icon (connection Type) next to each of the available devices
		this.connectionTypeAdapter = new ConnectionTypeAdapter(getApplicationContext(), new ArrayList<Device>());
		this.deviceList.setAdapter(connectionTypeAdapter);

		updateList();

		availableDevices = new ArrayList<>();
		deviceList.setOnItemClickListener(new OnItemClickListener());

		connectionTimeoutTimer = new Timer();

		//Get the API version
		//version = (TextView) findViewById(R.id.sdkVersion);

		this.init();
	}

	/**
	 * read data from the Commands file and load it in the commands class
	 *
	 * sets the name pattern to filter the scan results.
	 *
	 * sets up DeviceManager
	 *
	 */
	void 	init(){

		String METHODTAG = ".init";

		LeicaSdk.InitObject initObject = new LeicaSdk.InitObject("commands.json"); // this "commands.json" file can be named differently. it only has to exist in the assets folder


		try {
			LeicaSdk.init(getApplicationContext(), initObject);
			//version.setText(String.format("Version: %s", LeicaSdk.getVersion()));

		} catch (JSONException e) {
			Toast.makeText(this, "Error in the structure of the JSON File, closing the application", Toast.LENGTH_LONG).show();
			Log.e(CLASSTAG, METHODTAG + ": Error in the structure of the JSON File, closing the application",e);
			finish();

		} catch (IllegalArgumentCheckedException e) {
			Toast.makeText(this, "Error in the data of the JSON File, closing the application", Toast.LENGTH_LONG).show();
			Log.e(CLASSTAG, METHODTAG +   ": Error in the data of the JSON File, closing the application",e);
			finish();

		} catch (IOException e) {
			Toast.makeText(this, "Error reading JSON File, closing the application", Toast.LENGTH_LONG).show();
			Log.e(CLASSTAG, METHODTAG +  ": Error reading JSON File, closing the application",e);
			finish();

		}

		deviceManager = DeviceManager.getInstance(this);
		deviceManager.setFoundAvailableDeviceListener(this);
		deviceManager.setErrorListener(this);
		Log.i(CLASSTAG, METHODTAG +  ": Activitys initialization was finished completely");
	}

	@Override
	protected void onResume() {

		super.onResume();

		activityStopped = false;

		// show only connected devices
		this.availableDevices = deviceManager.getConnectedDevices();
		measurementGridStatus = PrefUtils.getMeasurementGrid(this);

		/*if(deviceManager.getConnectedDevices().size() == 1 && measurementGridStatus.equals("finished"))
		{
			PrefUtils.storeMeasurementGrid("0",this);

			stopFindAvailableDevicesTimer();
			stopFindingAvailableDevices();
			// get device
			Device device = availableDevices.get(0);

			if(device == null) {

				return;
			}

			currentDevice = device;

			// already connected
			if(device.getConnectionState() == Device.ConnectionState.connected){

				goToInfoScreen(device);
				return;
			}

		}
		else if(deviceManager.getConnectedDevices().size() == 0 && measurementGridStatus.equals("finished"))
		{
			PrefUtils.storeMeasurementGrid("0",this);
			Intent informationIntent = new Intent(SearchDevicesActivity.this, MeasurementGridActivity.class);
			startActivity(informationIntent);
		}*/




		//Update the Devices List UI
		updateList();

		// register receivers for internally receiving wifi and bluetooth adapter changes
		deviceManager.registerReceivers(this);

		// only show info dialog once
		if (searchInfoShown == false) {
			searchInfoShown = true;
			showAlert("Finding Devices started. Please wait. Please turn on the bluetooth, wifi and gps adapter to find all possible devices in reach.");
		}

		// immediately start finding devices when activity resumes
		findAvailableDevices();

	}

	@Override
	protected void onStop() {
		super.onStop();

		stopFindAvailableDevicesTimer();
		activityStopped = true;
		stopFindingAvailableDevices();
	}

	private void stopFindAvailableDevicesTimer(){
		// stop finding devices when activity stops
		// stop the timer for finding devices
		if (findDevicesTimer != null) {
			findDevicesTimer.cancel();
			findDevicesTimer.purge();
			findDevicesTimer = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deviceManager.unregisterReceivers();
		final String METHODTAG = ".onDestroy";
		/*// stop finding devices when activity finishes
		// stop the timer for finding devices
		if (findDevicesTimer != null) {
			findDevicesTimer.cancel();
			findDevicesTimer.purge();
			findDevicesTimer = null;
		}
		activityStopped = true;
		stopFindingAvailableDevices();

		//dismiss all the dialogs that may be activated
		if(activateWifiDialog != null) {
			activateWifiDialog.dismiss();
		}
		if(activateBluetoothDialog != null) {
			activateBluetoothDialog.dismiss();
		}
		if(connectingDialog != null) {
			connectingDialog.dismiss();
		}

		// unregister internal broadcast receivers
		deviceManager.unregisterReceivers();

		if (deviceManager != null){
			//Disconnect from all the connected devices
			for (Device connectedDevice: deviceManager.getConnectedDevices() ){
				connectedDevice.disconnect();
				Log.d(CLASSTAG, METHODTAG + "Disconnected Device: "+connectedDevice.modelName);
			}

		}
*/
	}

	/**
	 * responsible for updating the device list UI
	 */
	void updateList(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				connectionTypeAdapter.setNewDeviceList(availableDevices);
				connectionTypeAdapter.notifyDataSetChanged();
				deviceList.setAdapter(connectionTypeAdapter);
			}
		});
	}

	/**
	 * start looking for available devices
	 * - first clears the UI and show only connected devices
	 * - setups deviceManager and start finding devices process
	 * - setups timer for restarting finding process (after 25 seconds start a new search iteration)
	 */
	void 	findAvailableDevices() {
		final String METHODTAG = ".findAvailableDevices";
		Log.d(CLASSTAG, METHODTAG +": called");

		findDevicesRunning = true;

		long findAvailableDevicesDelay =  15000;
		long findAvailableDevicesPeriod =  15000;

		// show only connected devices
		availableDevices = deviceManager.getConnectedDevices();
		updateList();

		// Verify and enable Wifi and Bluetooth, according to what the user allowed
		boolean[] permissions = verifyPermissions();

		deviceManager.setErrorListener(this);
		deviceManager.setFoundAvailableDeviceListener(this);

		try {
			deviceManager.findAvailableDevices(permissions, getApplicationContext());
		}catch (PermissionException e){
			Log.e(CLASSTAG, METHODTAG +": missing permission: " + e.getMessage());
		}

		//
		// restart for finding devices:
		// a) because already found devices may be out of reach by now,
		// b) the user may changed adapter settings meanwhile
		if (findDevicesTimer == null){
			findDevicesTimer = new Timer();
			findDevicesTimer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					findAvailableDevices();
				}
			}, findAvailableDevicesDelay, findAvailableDevicesPeriod);
		}
	}

	/**
	 * stop finding devices
	 */
	void stopFindingAvailableDevices(){
		findDevicesRunning = false;
		deviceManager.stopFindingDevices();
	}

	/**
	 * called when a valid Leica device is found
	 * @param device the device
	 */
	@Override
	public void onAvailableDeviceFound(final Device device) {



		final String METHODTAG = ".onAvailableDeviceFound";
		Log.i(CLASSTAG, METHODTAG+": deviceId: " + device.getDeviceID() + ", deviceName: " + device.getDeviceName());

		// synchronized, because internally onAvailableDeviceFound() can be called from different threads
		synchronized (availableDevices) {

			// in rare cases it can happen, that a device is found twice. so here is a double check.
			for (Device availableDevice : availableDevices){
				if (availableDevice.getDeviceID().equalsIgnoreCase(device.getDeviceID())){
					return;
				}
			}
			availableDevices.add(device);
		}

		updateList();
	}

	/**
	 * show wifi system settings
	 * - connection to hotspot devices needs to be done manually by the user since programatical connection does not work properly for most android devices and android os versions
	 */
	void launchWifiPanel(){

			final String METHODTAG = ".launchWifiPanel";
			this.runOnUiThread(new Runnable(){
				@Override
				public void run() {

					AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
					builder.setMessage("Please connect to the WIFI HOTSPOT from the device.");
					builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {

							connectingDialog.dismiss();

							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							Log.d(CLASSTAG, METHODTAG+": Wifi Panel launched");
						}
					});
					builder.setCancelable(false);
					builder.create().show();
				}
			});
	}

	/**
	 * To check which kind of devices/connection modes should be scanned for
	 * E.g. if wifi permission is not given the api only scans for bluetooth devices, vice versa
	 * The location permission is needed to scan for bluetooth devices
	 * @return an array of booleans which represents what can/should be scanned for
	 */
	boolean[] verifyPermissions() {
		final String METHODTAG = CLASSTAG+".verifyPermissions";

		ArrayList<String> manifestPermission = new ArrayList<>();

		boolean[] permissions = {false, false};
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


			if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.INTERNET);

			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_NETWORK_STATE);
			}

			/*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}*/
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.ACCESS_WIFI_STATE);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.CHANGE_WIFI_STATE);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.BLUETOOTH);
			}

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_DENIED) {
				manifestPermission.add(Manifest.permission.BLUETOOTH_ADMIN);
			}

			String[] manifestPermissionStrArray = new String[manifestPermission.size()];
			int i = 0;
			for(String permission: manifestPermission){
				manifestPermissionStrArray[i] = permission;
			}
			try {
				if (manifestPermissionStrArray.length > 0) {
					ActivityCompat.requestPermissions(this, manifestPermissionStrArray, 1);
				}
			}catch(Exception e){
				Logs.log(Types.LogTypes.exception, METHODTAG, "Permissions error: ",e);
			}

			if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
				permissions[0] = true;
			}

			if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
				permissions[1] = true;
			}

			LocationManager lm = (LocationManager)getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
			boolean gps_enabled = false;
			boolean network_enabled = false;


			try {
				network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			} catch(Exception ex) {}

			if(!network_enabled) {
				// notify user

				this.runOnUiThread(new Runnable(){
					@Override
					public void run() {


						AlertDialog.Builder locationServicesDialog = new AlertDialog.Builder(SearchDevicesActivity.this);
						locationServicesDialog.setMessage("Please activate Location Services.");
						locationServicesDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {

								Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								getApplicationContext().startActivity(myIntent);

							}
						});
						locationServicesDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {


							}
						});
						locationServicesDialog.create().show();
					}
				});
			}
		}
		else {
			permissions[0] = true;
			permissions[1] = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		}
		Log.i(CLASSTAG, METHODTAG + ": Permissions: WIFI: "+ permissions[0] + ", BLE: "+ permissions[1]);
		return permissions;
	}

	/**
	 * Defines what happen on a click on an item in the list
	 * If device is already connected directly jump to the coresponding activity
	 * - BLE devices - BLEInformationActivity
	 * - BLE YETI devices - YetiInformationActivity
	 * - Wifi devices - WifiInformationActivity
	 *
	 * For Hotspot devices check if smartphone is connected to the correct hotspot
	 * Otherwise connect to the device
	 */
	private class OnItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			final String METHODTAG = ".deviceList OnItemClick";

			stopFindAvailableDevicesTimer();
			stopFindingAvailableDevices();
			// get device
			Device device = availableDevices.get(position);

			if(device == null) {
				Log.i(METHODTAG, "device not found");
				return;
			}

			currentDevice = device;

			// already connected
			if(device.getConnectionState() == Device.ConnectionState.connected){
				goToInfoScreen(device);
				return;
			}

			// show connecting dialog
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
					builder.setMessage("Connecting... This may take up to 15 seconds... ").setTitle("Connecting");
					builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							// cancel connection attempt
							stopConnectionAttempt();
							findAvailableDevices();
						}
					});

					connectingDialog = builder.create();
					connectingDialog.setCancelable(false);
					connectingDialog.show();
				}
			});

			// if hotspot go to wifi settings first if the wifi is incorrect
			if (currentDevice.getConnectionType().equals(Types.ConnectionType.wifiHotspot)){

				String wifiName = WifiHelper.getWifiName(getApplicationContext());

				if(wifiName == null){
					launchWifiPanel();
					return;
				}
				else if (wifiName.equalsIgnoreCase(currentDevice.getDeviceName()) == false){
					launchWifiPanel();
					return;
				}
				else{
					// wifi is correct, connect!
					connectToDevice(currentDevice);
					return;
				}
			}

			//Start Timer here, if bt device. for wifi we do not need a timer, there we have a socket timeout
			if(currentDevice.getConnectionType() == Types.ConnectionType.ble){
				startConnectionTimeOutTimer();
			}

			// connect the device
			connectToDevice(currentDevice);
		}
	}

	/**
	 * Connects to device
	 * @param device device to connect to
	 */
	void connectToDevice(Device device){
		// remember to which device connection is attempting, for eventually cancelling the connection attempt
		currentConnectionAttemptToDevice = device;
		connectionAttempts.put(device, Boolean.FALSE);

		device.setConnectionListener(this);
		device.setErrorListener(this);
		device.connect();
	}

	/**
	 * Start a timer to stop connecting attempt and show a timeout dialog (only for BTLE devices)
	 */
	void startConnectionTimeOutTimer(){
		final String METHODTAG = "startConnectionTimeOutTimer";
		Log.d(CLASSTAG, METHODTAG + ": Connection timeout timer started at: "+ System.currentTimeMillis());

		final long connectionTimeout = 30000;

		//Start Timer
		this.connectionTimeoutTask = new TimerTask() {
			@Override
			public void run() {
				// stop connecting attempt
				stopConnectionAttempt();

				// show timeout dialog
				showConnectionTimedOutDialog();
				if (currentDevice != null) {
					currentDevice.disconnect();
				}
				findAvailableDevices();

			}
		};
		this.connectionTimeoutTimer.schedule(connectionTimeoutTask, connectionTimeout);
	}

	/**
	 * stop connection timeout timer
	 */
	void stopConnectionTimeOutTimer(){
		if(connectionTimeoutTask == null){
			return;
		}
		this.connectionTimeoutTask.cancel();
		this.connectionTimeoutTimer.purge();
	}

	/**
	 * stop connection attempt
	 */
	synchronized void stopConnectionAttempt() {

		// remember for which device connection attempt is canceled
		if (currentConnectionAttemptToDevice != null) {
			connectionAttempts.put(currentConnectionAttemptToDevice, Boolean.TRUE);
		}

		stopConnectionTimeOutTimer();

		if (connectingDialog != null) {
			connectingDialog.dismiss();
		}

		if (currentDevice != null) {
			currentDevice.disconnect();
		}

	}

	/**
	 * Called when the connection state of a device changed
	 * @param device currently connected device
	 * @param state current device connection state
	 */
	@Override
	public void onConnectionStateChanged(Device device, Device.ConnectionState state) {
		final String METHODTAG = ".onConnectionStateChanged";
		Log.d(CLASSTAG, METHODTAG +": onConnectionStateChanged: " + device.getDeviceID() + ", state: " + state);

		switch (state){
			case connected:

				// update UI
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						connectingDialog.dismiss();
					}
				});

				// if connection attempt was canceled
				Boolean canceled = connectionAttempts.get(device);
				if (canceled != null){

					// connection attempt was canceled
					if (canceled == Boolean.TRUE){
						// disconnect device
						device.disconnect();
						// clean map
						connectionAttempts.remove(device);
						// update UI
						updateList();
						return;
					}
				}

				goToInfoScreen(device);

				break;
			case disconnected:
				break;
		}
	}

	/**
	 * Switch activity
	 * BTLE devices go to BLEInformationActivty
	 * BTLE Yeti go to YetiInformationActivty
	 * Wifi devices go to WifiInformationActivity
	 *
	*/
	void goToInfoScreen(Device device){

		final String METHODTAG = ".goToInfoScreen";
		// if bluetooth
		if (device.getConnectionType() == Types.ConnectionType.ble) {
			// Stop Timer here
			stopConnectionTimeOutTimer();

			// if device is Yeti
			if(device.getDeviceType().equals(Types.DeviceType.Yeti)){

				// set the current device object for the next acitivity
				//YetiInformationActivity.setCurrentDevice(device, getApplicationContext());

				//Launch the YetiInformationActivity
				Intent informationIntent = new Intent(SearchDevicesActivity.this, YetiInformationActivity.class);
				startActivity(informationIntent);
			}
			else {

				// set the current device object for the next acitivity
				BLEInformationActivity.setCurrentDevice(device, getApplicationContext());


				finish();

				//Launch the BLEInformationActivity
				/*Intent informationIntent = new Intent(SearchDevicesActivity.this, MeasurementGridActivity.class);
				startActivity(informationIntent);*/
			}
		}

		/*else if (device.getConnectionType() == Types.ConnectionType.wifiHotspot || device.getConnectionType() == Types.ConnectionType.wifiAP){

			// set the current device object for the next acitivity
			WifiInformationActivity.setCurrentDevice(device, getApplicationContext());

			//Launch the WifiInformationActivity
			Intent informationIntent = new Intent(SearchDevicesActivity.this, WifiInformationActivity.class);
			startActivity(informationIntent);
		}*/

		else {
			Log.e(CLASSTAG, METHODTAG +": unknown connection type. this should never happen.");
		}

		// forget current device
		this.currentDevice = null;
	}

	/**
	 * Defines the default behavior when an error is notified.
	 * Presents alert to user showing a error message
	 * @param errorObject error object comes from different sources API or APP.
	 */
	@Override
	public void onError(final ErrorObject errorObject) {
		final String METHODTAG = ".onError";
		Log.i(CLASSTAG, METHODTAG +": "+errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(connectingDialog != null) {
					connectingDialog.dismiss();
				}

				if (errorObject.getErrorCode() == ErrorDefinitions.HOTSPOT_DEVICE_IP_NOT_REACHABLE_CODE){
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() + "\nPlease try again.");
					return;
				}
				if (errorObject.getErrorCode() == ErrorDefinitions.AP_DEVICE_IP_NOT_REACHABLE_CODE){
					showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode() + "\nPlease try again.");
					return;
				}

				showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
			}
		});
	}

	/**
	 * 	Show alert message
	 */
	public void showAlert(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(SearchDevicesActivity.this);
				alertBuilder.setMessage(message);
				alertBuilder.setPositiveButton("Ok", null);
				alertBuilder.create().show();
			}
		});

	}

	/**
	 * Displays the timeout dialog, when the app is not able to connect to the DISTO device after 20 seconds.
	 * @see #startConnectionTimeOutTimer
	 *
	 */
	void showConnectionTimedOutDialog(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String message = "Could not connect to \n"+ currentDevice.getDeviceID()+"\nPlease check your device and adapters and try again.";
				AlertDialog.Builder builder = new AlertDialog.Builder(SearchDevicesActivity.this);
				builder.setMessage(message).setTitle("Connection Timeout");
				builder.setNegativeButton("Ok", null);
				connectingDialog = builder.create();
				connectingDialog.setCancelable(true);
				connectingDialog.show();

				// show only connected devices
				availableDevices = deviceManager.getConnectedDevices();
				updateList();
			}
		});
	}
}