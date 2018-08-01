package com.valleyforge.cdi.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.utils.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.leica.sdk.Defines;
import ch.leica.sdk.Devices.Device;
import ch.leica.sdk.Devices.DeviceManager;
import ch.leica.sdk.ErrorHandling.DeviceException;
import ch.leica.sdk.ErrorHandling.ErrorDefinitions;
import ch.leica.sdk.ErrorHandling.ErrorObject;
import ch.leica.sdk.ErrorHandling.IllegalArgumentCheckedException;
import ch.leica.sdk.ErrorHandling.WrongDataException;
import ch.leica.sdk.Listeners.ErrorListener;
import ch.leica.sdk.Listeners.ReceivedDataListener;
import ch.leica.sdk.Reconnection.ReconnectionHelper;
import ch.leica.sdk.Types;
import ch.leica.sdk.Utilities.WaitAmoment;
import ch.leica.sdk.commands.MeasuredValue;
import ch.leica.sdk.commands.MeasurementConverter;
import ch.leica.sdk.commands.ReceivedData;
import ch.leica.sdk.commands.ReceivedDataPacket;
import ch.leica.sdk.commands.ReceivedYetiDataPacket;
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseUpdate;


public class YetiInformationActivity extends AppCompatActivity implements ReceivedDataListener, Device.ConnectionListener, ErrorListener, ReconnectionHelper.ReconnectListener, Device.UpdateDeviceListener {

	/**
	 * Classname
	 */
	private final String CLASSTAG = YetiInformationActivity.class.getSimpleName();


	static Device currentDevice;
	static ReconnectionHelper reconnectionHelper;

	AlertDialog updateProgressDialog;
	private byte [] updateFileBytes;

	private final static int APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE = 9001;

	private boolean storagePermission = false;
	private Types.Commands updateType = null;

	private TextView status;
	private TextView distance;
	private TextView distanceUnit;
	private TextView inclination;
	private TextView inclinationUnit;
	private TextView direction;
	private TextView directionUnit;
	private TextView timestamp_Basic_Measurements;

	//Start New Measurements for Yeti
	private TextView HZAngle;
	private TextView VeAngle;
	private TextView InclinationStatus;
	private TextView timestamp_P2P_Measurements;

	private TextView Quaternion_X;
	private TextView Quaternion_Y;
	private TextView Quaternion_Z;
	private TextView Quaternion_W;
	private TextView timestamp_Quaternion_Measurements;

	private TextView Acceleration_X;
	private TextView Acceleration_Y;
	private TextView Acceleration_Z;
	private TextView AccSensitivity;
	private TextView Rotation_X;
	private TextView Rotation_Y;
	private TextView Rotation_Z;
	private TextView timestamp_ACCRotation_Measurements;
	private TextView RotationSensitivity;


	private TextView Magnetometer_X;
	private TextView Magnetometer_Y;
	private TextView Magnetometer_Z;
	private TextView timestamp_Magnetometer_Measurements;
	//End - New Measurements for Yeti

	private TextView distoCOMResponse;
	private TextView distoCOMEvent;

	private TextView modelName;

	private Button read;
	private Button clear;

	private ButtonListener bl = new ButtonListener();


	private Button sendCommand;
	private Button updateButton;

	private ScrollView measurementsScrollView;


	boolean turnOnBluetoothDialogIsShown = false;
	boolean reconnectionIsRunning = false;
	private Boolean receiverRegistered = false;


	static int defaultDirectionAngleUnit = MeasurementConverter.getDefaultDirectionAngleUnit();

	private AlertDialog alertDialogConnect;
	private AlertDialog alertDialogDisconnect;

	private String testCommand = "srvBt This is a test command, bigger than 20 bytes.";

	boolean isDestroyed = false;

	int defaultDistanceUnit = 0;
	int defaultInclinationUnit = 0;
	int defaultDirectionUnit = 0;

	HandlerThread sendCustomCommandThread;
	Handler sendCustomCommandHandler;

	HandlerThread getDeviceStateThread;
	Handler getDeviceStateHandler;

	private File updateFile = null;

	private Timer getModelTimeoutTimer = new Timer();
	private TimerTask getModelTimeoutTask;
	private int getModelTimeout = 2500;

	AlertDialog customCommandDialog;

	Runnable checkDeviceState =	new Runnable() {
		@Override
		public void run() {
			final String METHODTAG = ".checkDeviceState";
			Log.d(CLASSTAG, METHODTAG + ": calling is in sw mode");
			try {
				if(currentDevice.isInUpdateMode() == true){
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							sendCommand.setEnabled(false);
							clear.setEnabled(false);
							measurementsScrollView.setVisibility(View.GONE);
							measurementsScrollView.setEnabled(false);
							updateButton.setEnabled(false);

						}
					});
					launchUpdateProcess();

				}else{

					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							sendCommand.setEnabled(true);
							clear.setEnabled(true);
							measurementsScrollView.setEnabled(true);
							measurementsScrollView.setVisibility(View.VISIBLE);
							updateButton.setEnabled(true);
							//

						}
					});
				}
			} catch (DeviceException e) {
				showUpdateMessages("Not Able to get the Device State");

				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						sendCommand.setEnabled(true);
						clear.setEnabled(true);
						measurementsScrollView.setEnabled(true);
						measurementsScrollView.setVisibility(View.VISIBLE);
						updateButton.setEnabled(true);
						dismissUpdateProgressDialog();
					}
				});

				Log.e(CLASSTAG, METHODTAG + ": Not Able to get the Device State",e);
			}
		}
	};

	BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String METHODTAG = "bluetoothAdapterReceiver.receive";
			Log.d(CLASSTAG, METHODTAG );
			checkForReconnection();

		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Initialize all UI Fields
		setContentView(R.layout.activity_yeti_information);

		status = (TextView) findViewById(R.id.status);
		distance = (TextView) findViewById(R.id.distance);
		distanceUnit = (TextView) findViewById(R.id.distanceUnit);
		inclination = (TextView) findViewById(R.id.inclination);
		inclinationUnit = (TextView) findViewById(R.id.inclinationUnit);
		direction = (TextView) findViewById(R.id.direction);
		directionUnit = (TextView) findViewById(R.id.directionUnit);
		timestamp_Basic_Measurements = (TextView) findViewById(R.id.timestamp_Basic_Measurements);

		HZAngle = (TextView) findViewById(R.id.fHZAngle);
		VeAngle = (TextView) findViewById(R.id.fVeAngle);
		InclinationStatus = (TextView) findViewById(R.id.uInclinationStatus);
		timestamp_P2P_Measurements = (TextView) findViewById(R.id.timestamp_P2P_Measurements);

		Quaternion_X = (TextView) findViewById(R.id.fQuaternion_X);
		Quaternion_Y = (TextView) findViewById(R.id.fQuaternion_Y);
		Quaternion_Z = (TextView) findViewById(R.id.fQuaternion_Z);
		Quaternion_W = (TextView) findViewById(R.id.fQuaternion_W);
		timestamp_Quaternion_Measurements = (TextView) findViewById(R.id.timestamp_Quaternion_Measurements);

		Acceleration_X = (TextView) findViewById(R.id.sAcceleration_X);
		Acceleration_Y = (TextView) findViewById(R.id.sAcceleration_Y);
		Acceleration_Z = (TextView) findViewById(R.id.sAcceleration_Z);
		AccSensitivity = (TextView) findViewById(R.id.sAccSensitivity);
		Rotation_X = (TextView) findViewById(R.id.sRotation_X);
		Rotation_Y = (TextView) findViewById(R.id.sRotation_Y);
		Rotation_Z = (TextView) findViewById(R.id.sRotation_Z);
		timestamp_ACCRotation_Measurements = (TextView) findViewById(R.id.timestamp_ACCRotation_Measurements);
		RotationSensitivity = (TextView) findViewById(R.id.fRotationSensitivity);


		Magnetometer_X = (TextView) findViewById(R.id.fMagnetometer_X);
		Magnetometer_Y = (TextView) findViewById(R.id.fMagnetometer_Y);
		Magnetometer_Z = (TextView) findViewById(R.id.fMagnetometer_Z);
		timestamp_Magnetometer_Measurements = (TextView) findViewById(R.id.timestamp_Magnetometer_Measurements);

		distoCOMResponse = (TextView) findViewById(R.id.distoCOMResponse);
		distoCOMEvent = (TextView) findViewById(R.id.distoCOMEvent);

		modelName = (TextView) findViewById(R.id.ModelName);

		clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(bl);
		read = (Button) findViewById(R.id.read);
		read.setVisibility(View.INVISIBLE);
		read.setOnClickListener(bl);

		sendCommand = (Button) findViewById(R.id.sendCustomCommand);
		sendCommand.setOnClickListener(bl);

		measurementsScrollView = (ScrollView) findViewById(R.id.commands);

		updateButton = (Button) findViewById(R.id.update);
		updateButton.setOnClickListener(bl);



		//Connect /Disconnect dialog
		AlertDialog.Builder alertConnectedBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
		alertConnectedBuilder.setMessage("connection established");
		alertConnectedBuilder.setPositiveButton("Ok", null);

		AlertDialog.Builder alertDisconnectedBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
		alertDisconnectedBuilder.setMessage("lost connection to device");
		alertDisconnectedBuilder.setPositiveButton("Ok", null);

		alertDialogConnect 		= alertConnectedBuilder.create();
		alertDialogDisconnect 	= alertDisconnectedBuilder.create();


	}

	@Override
	protected void onResume() {
		super.onResume();


		//Put allUI disabled until getting the deviceState

		updateButton.setEnabled(false);
		sendCommand.setEnabled(false);
		clear.setEnabled(false);
		measurementsScrollView.setVisibility(View.GONE);



		// Register activity for bluetooth adapter changes
		if (!receiverRegistered) {
			receiverRegistered = true;
			IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			registerReceiver(bluetoothAdapterReceiver, filter);
		}

		// setup according to device
		if (currentDevice != null) {
			currentDevice.setConnectionListener(this);
			currentDevice.setReceiveDataListener(this);
			currentDevice.setErrorListener(this);

			status.setText(currentDevice.getConnectionState().toString());

			if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {

				getModelTimeoutTimer = new Timer();

				// Ask for model number and serial number, this will result async

				String model = currentDevice.getModel();
				if(model.isEmpty() == false) {
					setUI(model);
				}else{
					//Start Timer
					getModelTimeoutTask = new TimerTask() {
						@Override
						public void run() {
							final String METHODTAG = ".getModelTimeoutTask";
							Log.d(CLASSTAG, METHODTAG+ ": called");
							String modelName = currentDevice.getModel();
							if(modelName.isEmpty() == false) {
								Log.d(CLASSTAG, METHODTAG+ ": ModelName: "+modelName);
								setUI(modelName);
							}else{
								currentDevice.getModelValue();
							}
						}
					};

					getModelTimeoutTimer.schedule(getModelTimeoutTask, getModelTimeout);
				}

				if(getDeviceStateThread == null) {
					getDeviceStateThread = new HandlerThread("getDeviceStateThread" + System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
					getDeviceStateThread.start();
					getDeviceStateHandler = new Handler(getDeviceStateThread.getLooper());
				}
				//This time is needed to wait for all the BLE characteristics to be set-up.
				getDeviceStateHandler.postDelayed( checkDeviceState,2000);


			}
		}
		if (reconnectionHelper != null) {
			reconnectionHelper.setErrorListener(this);
			reconnectionHelper.setReconnectListener(this);
		}

		if (reconnectionIsRunning){
			status.setText(R.string.reconnecting);
		}

		// start bt connection
		if(currentDevice != null){
			currentDevice.startBTConnection();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();

		//Unregister activity for bluetooth adapter changes
		if (receiverRegistered) {
			unregisterReceiver(bluetoothAdapterReceiver);
			receiverRegistered = false;
		}

		if(customCommandDialog != null) {
			customCommandDialog.dismiss();
		}


		// Pause the bluetooth connection
		if(currentDevice != null){
			currentDevice.pauseBTConnection();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		final String METHODTAG = ".onDestroy";

		isDestroyed = true;

		//Unregister the activity for changes in the connection
		if (currentDevice != null) {
			currentDevice.setReceiveDataListener(null);
			currentDevice.setConnectionListener(null);
			currentDevice.setErrorListener(null);
			currentDevice = null;
		}
		//Unregister the activity for reconnection events
		if (reconnectionHelper != null) {
			reconnectionHelper.setErrorListener(null);
			reconnectionHelper.setReconnectListener(null);
			reconnectionHelper.stopReconnecting();
			reconnectionHelper = null;
			reconnectionIsRunning = false;
		}

		if (sendCustomCommandThread != null){
			sendCustomCommandThread.interrupt();
			sendCustomCommandThread = null;
			sendCustomCommandHandler = null;
		}
		if (getDeviceStateThread != null){
			getDeviceStateThread.interrupt();
			getDeviceStateThread = null;
			getDeviceStateHandler = null;
		}

		if (currentDevice != null){
			//Disconnect the device
			currentDevice.disconnect();
			Log.d(CLASSTAG, METHODTAG + "Disconnected Device: "+currentDevice.modelName);

		}

	}


	/**
	 * Verify if the current device need to be reconnected
	 * If the device disconnected and the reconnection function has not been called, then it will start
	 */
	synchronized void checkForReconnection() {

		final String METHODTAG = ".checkForReconnection";

		if (currentDevice == null) {
			return;
		}
		if (currentDevice.getConnectionState() == Device.ConnectionState.connected) {
			return;
		}
		if (!currentDevice.getConnectionType().equals(Types.ConnectionType.ble)) {
			return;
		}
		if (turnOnBluetoothDialogIsShown) {
			Log.d(CLASSTAG, METHODTAG +":  turnOnBluetoothDialogIsShown is true");
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// wait a moment for bluetooth adapter to be setup
				WaitAmoment wait = new WaitAmoment();
				wait.waitAmoment(2000);

				// check if bluetooth is available
				boolean bluetoothIsAvailable = DeviceManager.getInstance(getApplicationContext()).checkBluetoothAvailibilty();
				if (!bluetoothIsAvailable) {
					Log.d(CLASSTAG, METHODTAG +": bluetooth is not available");

					// show alert to turn on bluetooth
					showBluetoothTurnOn();

					return;
				}

				if (!reconnectionIsRunning && reconnectionHelper!=null) {
					reconnectionIsRunning = true;
					reconnectionHelper.startReconnecting();

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							status.setText(R.string.reconnecting);
						}
					});
				}

			}
		}).start();
	}

	/**
	 * Clear all textfields
	 */
	private void clear() {
		distance.setText(R.string.default_value);
		distanceUnit.setText(R.string.default_value);
		inclination.setText(R.string.default_value);
		inclinationUnit.setText(R.string.default_value);
		direction.setText(R.string.default_value);
		directionUnit.setText(R.string.default_value);
		timestamp_Basic_Measurements.setText(R.string.default_value);

		HZAngle.setText(R.string.default_value);
		VeAngle.setText(R.string.default_value);
		InclinationStatus.setText(R.string.default_value);
		timestamp_P2P_Measurements.setText(R.string.default_value);

		Quaternion_X.setText(R.string.default_value);
		Quaternion_Y.setText(R.string.default_value);
		Quaternion_Z.setText(R.string.default_value);
		Quaternion_W.setText(R.string.default_value);
		timestamp_Quaternion_Measurements.setText(R.string.default_value);

		Acceleration_X.setText(R.string.default_value);
		Acceleration_Y.setText(R.string.default_value);
		Acceleration_Z.setText(R.string.default_value);
		AccSensitivity.setText(R.string.default_value);
		Rotation_X.setText(R.string.default_value);
		Rotation_Y.setText(R.string.default_value);
		Rotation_Z.setText(R.string.default_value);
		RotationSensitivity.setText(R.string.default_value);
		timestamp_ACCRotation_Measurements.setText(R.string.default_value);

		Magnetometer_X.setText(R.string.default_value);
		Magnetometer_Y.setText(R.string.default_value);
		Magnetometer_Z.setText(R.string.default_value);
		timestamp_Magnetometer_Measurements.setText(R.string.default_value);

		distoCOMEvent.setText(R.string.default_value);
		distoCOMResponse.setText(R.string.default_value);

	}

	/**
	 * Check if the device is disconnected, if it is disconnected launch the reconnection function
	 * @param device the device on which the connection state changed
	 * @param state the current connection state. If state is disconnected, the device object is not valid anymore. No connection can be established with this object any more.
	 */
	@Override
	public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {
		final String METHODTAG = ".onConnectionStateChanged";
		Log.d(CLASSTAG, METHODTAG +": " + device.getDeviceID() + ", state: " + state);

		try {
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					status.setText(state.toString());
				}
			});

			if (state == Device.ConnectionState.disconnected) {

				showConnectedDisconnectedDialog(false);
				checkForReconnection();
				return;
			}

			showConnectedDisconnectedDialog(true);

		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onAsyncDataReceived(final ReceivedData receivedData) {

		final String METHODTAG = CLASSTAG + ".onAsyncDataReceived";

		if (receivedData != null) {

			//Set data in the corresponding UI Element
			this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					//Default units = 0, for Basic Measurements, Distance, Orientation, Inclination
					for (ReceivedDataPacket receivedPacket : receivedData.dataPackets) {
						try {
							int id = receivedPacket.dataId;

							switch (id) {

								//show the Model number from the ModelName characteristic in DISTO Service
								case Defines.ID_DS_MODEL_NAME: {
									String data = receivedPacket.getStringValue();
									modelName.setText(data);
									setUI(data);
									Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
								}
								break;

								//Distance Measurement
								case Defines.ID_IMU_BASIC_MEASUREMENTS: {

									MeasuredValue distanceValue;
									MeasuredValue inclinationValue;
									MeasuredValue directionValue;

									ReceivedYetiDataPacket.YetiBasicMeasurements data = ((ReceivedYetiDataPacket)receivedPacket).getBasicMeasurements();

									distanceValue = new MeasuredValue(data.getDistance());
									distanceValue.setUnit(data.getDistanceUnit());
									distanceValue = MeasurementConverter.convertDistance(distanceValue);
									distance.setText(distanceValue.getConvertedValueStr());
									distanceUnit.setText(distanceValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getDistance());

									//Creates a measured value for the inclination angle

									inclinationValue = new MeasuredValue(data.getInclination());
									inclinationValue.setUnit(data.getInclinationUnit());
									inclinationValue = MeasurementConverter.convertAngle(inclinationValue);
									inclination.setText(inclinationValue.getConvertedValueStr());
									inclinationUnit.setText(inclinationValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getInclination());

									//Creates a measured value for the direction angle
									directionValue = new MeasuredValue(data.getDirection());
									directionValue.setUnit(data.getDirectionUnit());
									directionValue = MeasurementConverter.convertAngle(directionValue);
									direction.setText(directionValue.getConvertedValueStr());
									directionUnit.setText(directionValue.getUnitStr());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getDirection());


									timestamp_Basic_Measurements.setText(String.valueOf(data.getTimestamp()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getTimestamp());



								}
								break;

								case Defines.ID_IMU_P2P: {


									MeasuredValue VeP2PValue;
									MeasuredValue HzP2PValue;
									ReceivedYetiDataPacket.YetiP2P data = ((ReceivedYetiDataPacket)receivedPacket).getP2P();

									HzP2PValue = new MeasuredValue(data.getHzAngle());
									HzP2PValue.setUnit(defaultDirectionAngleUnit);
									HzP2PValue = MeasurementConverter.convertAngle(HzP2PValue);
									HZAngle.setText(HzP2PValue.getConvertedValueStr());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getHzAngle());

									VeP2PValue = new MeasuredValue(data.getVeAngle());
									VeP2PValue.setUnit(defaultDirectionAngleUnit);
									VeP2PValue = MeasurementConverter.convertAngle(VeP2PValue);
									VeAngle.setText(VeP2PValue.getConvertedValueStr());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getVeAngle());

									InclinationStatus.setText(String.valueOf(data.getInclinationStatus()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getInclinationStatus());

									timestamp_P2P_Measurements.setText(String.valueOf(data.getTimeStampAndFlags()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getTimeStampAndFlags());

								}
								break;

								case Defines.ID_IMU_QUATERNION: {
									ReceivedYetiDataPacket.YetiQuaternion data = ((ReceivedYetiDataPacket)receivedPacket).getQuaternion();

									Quaternion_X.setText(String.valueOf(data.getQuaternion_X()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getQuaternion_X());

									Quaternion_Y.setText(String.valueOf(data.getQuaternion_Y()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getQuaternion_Y());

									Quaternion_Z.setText(String.valueOf(data.getQuaternion_Z()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getQuaternion_Z());

									Quaternion_W.setText(String.valueOf(data.getQuaternion_W()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getQuaternion_W());

									timestamp_Quaternion_Measurements.setText(String.valueOf(data.getTimeStampAndFlags()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getTimeStampAndFlags());

								}
								break;
								case Defines.ID_IMU_ACELERATION_AND_ROTATION: {
									ReceivedYetiDataPacket.YetiAccelerationAndRotation data = ((ReceivedYetiDataPacket)receivedPacket).getAccelerationAndRotation();
									Acceleration_X.setText(String.valueOf(data.getAcceleration_X()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getAcceleration_X());

									Acceleration_Y.setText(String.valueOf(data.getAcceleration_Y()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getAcceleration_Y());

									Acceleration_Z.setText(String.valueOf(data.getAcceleration_Z()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getAcceleration_Z());

									AccSensitivity.setText(String.valueOf(data.getAccSensitivity()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getAccSensitivity());

									Rotation_X.setText(String.valueOf(data.getRotation_X()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getRotation_X());

									Rotation_Y.setText(String.valueOf(data.getRotation_Y()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getRotation_Y());

									Rotation_Z.setText(String.valueOf(data.getRotation_Z()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getRotation_Z());

									RotationSensitivity.setText(String.valueOf(data.getRotationSensitivity()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getRotationSensitivity());

									timestamp_ACCRotation_Measurements.setText(String.valueOf(data.getTimeStampAndFlags()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getTimeStampAndFlags());
								}
								break;


								case Defines.ID_IMU_MAGNETOMETER: {
									ReceivedYetiDataPacket.YetiMagnetometer data = ((ReceivedYetiDataPacket)receivedPacket).getMagnetometer();
									Magnetometer_X.setText(String.valueOf(data.getMagnetometer_X()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getMagnetometer_X());

									Magnetometer_Y.setText(String.valueOf(data.getMagnetometer_Y()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getMagnetometer_Y());

									Magnetometer_Z.setText(String.valueOf(data.getMagnetometer_Z()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getMagnetometer_Z());

									timestamp_Magnetometer_Measurements.setText(String.valueOf(data.getTimeStampAndFlags()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getTimeStampAndFlags());

								}
								break;
								case Defines.ID_IMU_DISTOCOM_TRANSMIT: {
									ReceivedYetiDataPacket.YetiDistocom data = ((ReceivedYetiDataPacket)receivedPacket).getDistocom();
									distoCOMResponse.setText(String.valueOf(data.getDistocomMessage()));
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getDistocomMessage());
								}
								break;
								case Defines.ID_IMU_DISTOCOM_EVENT: {
									ReceivedYetiDataPacket.YetiDistocom data = ((ReceivedYetiDataPacket)receivedPacket).getDistocom();
									distoCOMEvent.setText(data.getDistocomMessage());
									Log.d(CLASSTAG, METHODTAG+": called with id: " + id + ", value: " + data.getDistocomMessage());
								}
								break;
								default:{
									Log.d(CLASSTAG, METHODTAG+":  Error setting data in the UI");
								}
								break;
							}
						} catch (IllegalArgumentCheckedException e) {
							Log.e(CLASSTAG, METHODTAG, e);
						} catch (WrongDataException e) {
							Log.d(CLASSTAG, METHODTAG + " A wrong value has been set into the UI");
							showAlert("Wrong Value Received.");
						}
					}
				}
			});
		} else {
			Log.d(CLASSTAG, METHODTAG +": Error onAsyncDataReceived: receivedData object is null  ");
		}
	}


	/**
	 * Button Listener
	 */
	private class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			final String METHODTAG = ".ButtonListener.onClick";

			switch (v.getId()) {
				case R.id.clear: {
					clear();
				}
				break;
				case R.id.sendCustomCommand:{

					if (sendCustomCommandThread == null) {
						sendCustomCommandThread = new HandlerThread("getDeviceStateThread" + System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
						sendCustomCommandThread.start();
						sendCustomCommandHandler = new Handler(sendCustomCommandThread.getLooper());
					}

					showCustomCommandDialog();


				}
				break;
				case R.id.update: {
					launchUpdateProcess();
				}
				break;
				default:{

					Log.d(CLASSTAG, METHODTAG+": Error setting data in the UI");

				}
				break;
			}
		}
	}


	/**
	 * Shows dialog after connection status changed.
	 * @param connected true -> Connected, false -> Disconnected
	 */
	synchronized void showConnectedDisconnectedDialog(final boolean connected) {
		try {
			if(currentDevice.isInUpdateMode() == false) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connected) {
                            alertDialogConnect.show();
                            if (alertDialogDisconnect.isShowing()) {
                                alertDialogDisconnect.dismiss();
                            }
                        } else {
                            alertDialogDisconnect.show();
                            if (alertDialogDisconnect.isShowing()) {
                                alertDialogConnect.dismiss();
                            }
                        }
                    }
                });
            }
		} catch (DeviceException e) {
			e.printStackTrace();
		}
	}

	public void showAlert(final String message) {
		if (isDestroyed){
			return;
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
				alertBuilder.setMessage(message);
				alertBuilder.setPositiveButton("Ok", null);
				alertBuilder.create().show();
			}
		});

	}

	synchronized void showBluetoothTurnOn() {

		final String METHODTAG = ".showBluetoothTurnOn";
		if (turnOnBluetoothDialogIsShown) {
			Log.d(CLASSTAG, 		METHODTAG +":  dialog is already shown");
			return;
		}

		turnOnBluetoothDialogIsShown = true;
		Log.d(CLASSTAG, METHODTAG +": turnOnBluetoothDialogIsShown is true");

		runOnUiThread(new Runnable() {
			@Override
			public void run() {

				AlertDialog.Builder builder = new AlertDialog.Builder(YetiInformationActivity.this);
				builder.setMessage("Bluetooth has to be turned on.");
				builder.setPositiveButton("Turn it on", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						turnOnBluetoothDialogIsShown = false;
						DeviceManager.getInstance(getApplicationContext()).enableBLE();
					}
				});
				builder.create().show();
				Log.d(CLASSTAG, METHODTAG +": SHOW");

			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onError(ErrorObject errorObject) {
		final String METHODTAG = ".onError";

		Log.e(CLASSTAG, METHODTAG +": "+errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

		showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onReconnect(Device device) {
		final String METHODTAG = ".onReconnect";
		Log.d(CLASSTAG, METHODTAG);

		reconnectionIsRunning = false;

		currentDevice = device;
		currentDevice.setErrorListener(this);
		currentDevice.setConnectionListener(this);
		currentDevice.setReceiveDataListener(this);

		reconnectionHelper = new ReconnectionHelper(currentDevice, getApplicationContext());
		reconnectionHelper.setReconnectListener(this);
		reconnectionHelper.setErrorListener(this);

		onConnectionStateChanged(currentDevice, currentDevice.getConnectionState());

		if(getDeviceStateThread == null) {
			getDeviceStateThread = new HandlerThread("getDeviceStateThread_"+ System.currentTimeMillis(), HandlerThread.MAX_PRIORITY);
			getDeviceStateThread.start();
			getDeviceStateHandler = new Handler(getDeviceStateThread.getLooper());
		}

		getDeviceStateHandler.post( checkDeviceState);

	}

	public static void setCurrentDevice(Device currentDevice, Context context) {
		YetiInformationActivity.currentDevice = currentDevice;
		reconnectionHelper = new ReconnectionHelper(currentDevice, context);
	}

	void showUpdateProgessDialog(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (updateProgressDialog != null && updateProgressDialog.isShowing()){
					updateProgressDialog.dismiss();
				}
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(YetiInformationActivity.this);
				dialogBuilder.setTitle("Updating device");
				dialogBuilder.setMessage("Please wait...");
				dialogBuilder.setCancelable(false);
				updateProgressDialog = dialogBuilder.create();
				updateProgressDialog.show();
			}
		});
	}

	void setUpdateProgressDialog(final String title, final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (updateProgressDialog == null){
					return;
				}
				updateProgressDialog.setTitle(title);
				updateProgressDialog.setMessage(message);
			}
		});
	}

	void dismissUpdateProgressDialog(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (updateProgressDialog == null){
					return;
				}
				updateProgressDialog.dismiss();
			}
		});
	}

	/**
	 * Implemented method from Device.UpdateDeviceListener
	 * @param bytesSent the number of bytes that are already sent
	 * @param bytesTotalNumber the total number of bytes that should be sent
	 */
	@Override
	public void onProgress(long bytesSent, long bytesTotalNumber) {

		final String METHODTAG = ".onProgress";

		Log.d(CLASSTAG, METHODTAG +": number of bytes sent: " + bytesSent + " of total bytes: " + bytesTotalNumber);

		setUpdateProgressDialog("Updating device", "Progress: " + bytesSent + "/" + bytesTotalNumber);

	}

	public void showUpdateTypeDialog(){


		if(updateType == null || updateFile == null) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Select update type");

			CharSequence[] updateTypes = new CharSequence[]{"App", "EDM", "ExtFlash"};
			dialogBuilder.setItems(updateTypes, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					List<File> updateFirmwareFiles = null;


					switch (which) {

						/*
						* find files with specific prefix and file extension
						* */
						// app
						case 0:
							updateType = Types.Commands.UpdateAPP;
							//updateFirmwareFiles = FileHelper.findFiles("update_app_", "bin");
							updateFirmwareFiles = FileHelper.findFiles("", "bin");
							break;

						// edm
						case 1:
							updateType = Types.Commands.UpdateEDM;
							//updateFirmwareFiles = FileHelper.findFiles("update_edm_", "bin");
							updateFirmwareFiles = FileHelper.findFiles("", "bin");
							break;

						// flash
						case 3:
							updateType = Types.Commands.UpdateExtFlash;
							//updateFirmwareFiles = FileHelper.findFiles("update_extflash_", "bin");
							updateFirmwareFiles = FileHelper.findFiles("", "bin");
							break;
					}


					// show dialog
					showUpdateFiles(updateFirmwareFiles);

				}
			});
			dialogBuilder.create().show();

		}else{

			startUpdateProcess(updateFile);
		}
	}

	void showUpdateFiles(final List<File> files){

		final String METHODTAG = ".showUpdateFiles";

		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

		if(files!=null) {
			if (files.size() < 1) {
				dialogBuilder.setTitle("No files found.");
				dialogBuilder.setMessage("Please put the update firmware files into download, documents or root storage folder.");  // that the dialog looks nicer
				dialogBuilder.show();
				return;
			}

			dialogBuilder.setTitle("Found update files (downloads, documents, root storage folder):");

			CharSequence[] items = new CharSequence[files.size()];
			for (int i = 0; i < files.size(); i++) {
				String fileName = files.get(i).getAbsolutePath().substring(files.get(i).getAbsolutePath().lastIndexOf("/") + 1);
				items[i] = fileName;
			}
			dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {

					updateFile = files.get(i);
					File chosenFile = updateFile;
					startUpdateProcess(chosenFile);
				}
			});
			dialogBuilder.create().show();
		}else{
			Log.d(CLASSTAG, METHODTAG+": An error ocurreed showing the update files. ");
		}
	}

	/**
	 * Show bluetooth turnOn dialog
	 */
	synchronized void showUpdateMessages(final String message){

			final String METHODTAG = ".showUpdateMessages";
			if (turnOnBluetoothDialogIsShown) {
				Log.d(CLASSTAG, METHODTAG + ": dialog is already shown");
				return;
			}

			turnOnBluetoothDialogIsShown = true;
			Log.d(CLASSTAG, METHODTAG + ":  turnOnBluetoothDialogIsShown is true");

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						AlertDialog.Builder builder = new android.app.AlertDialog.Builder(YetiInformationActivity.this);
						builder.setMessage(message);
						builder.setCancelable(false);
						builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								turnOnBluetoothDialogIsShown = false;
								DeviceManager.getInstance(getApplicationContext()).enableBLE();
							}
						});
						builder.create().show();
						Log.d(CLASSTAG, METHODTAG + ": (String message)");
					} catch (Exception e) {
						Log.e(CLASSTAG, "Error showing update Message, UI Error", e);
					}
				}
			});

	}

	void startUpdateProcess(final File firmwareFile){
		final String METHODTAG = ".startUpdateProcess()";

		if(currentDevice != null && currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {
			if (storagePermission == true) {

				if (updateType == null) {

					showUpdateMessages("Please set the update type before proceeding.");
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						ResponseUpdate responseUpdate = null;
						try {
							updateFileBytes = FileHelper.loadFile(firmwareFile);

							Log.d(CLASSTAG, METHODTAG +": FILE byte: " + updateFileBytes.length);

							showUpdateProgessDialog();

							if (updateType.equals(Types.Commands.UpdateAPP)) {
								responseUpdate = currentDevice.updateDeviceFirmwareApp(updateFileBytes, YetiInformationActivity.this);
							}
							else if (updateType.equals(Types.Commands.UpdateEDM)) {
								responseUpdate = currentDevice.updateDeviceFirmwareEdm(updateFileBytes, YetiInformationActivity.this);
							}
							else if (updateType.equals(Types.Commands.UpdateExtFlash)) {
								responseUpdate = currentDevice.updateDeviceFirmwareExtFlash(updateFileBytes, YetiInformationActivity.this);
							}
							else {
								Log.e(CLASSTAG, METHODTAG +": unknown updatetype. this should not happen");
							}


							if(responseUpdate == null){
								updateType = null;
								updateFile = null;
								onError(new ErrorObject(ErrorDefinitions.UPDATE_FIRMWARE_FAIL_CODE, "Failed to get the response update."));
								dismissUpdateProgressDialog();

							}else if (responseUpdate.getError() != null){
								updateType = null;
								updateFile = null;
								onError(responseUpdate.getError());
								dismissUpdateProgressDialog();
								//return;


							}else{
								// if device only restarts, update process will still go on to phase 2
								if (responseUpdate.getIsUpdateFinished() == false){
									// do nothing
									return;
								}
								else {
									updateType = null;
									updateFile = null;
									dismissUpdateProgressDialog();
									showAlert("Update Successful");

								}
							}

							//getDeviceStateHandler.postDelayed( checkDeviceState,100);


						} catch (IOException e) {
							showUpdateMessages("Unable to load the File.");
							Log.e(CLASSTAG, METHODTAG + ": Unable to load the File.",e);
							updateType = null;
							updateFile = null;
						} catch (DeviceException e) {

							showUpdateMessages("Not Able to get the Device State.");
							Log.e(CLASSTAG, METHODTAG + ": Not Able to get the Device State.",e);
							updateType = null;
							updateFile = null;
						}
						finally{
							dismissUpdateProgressDialog();
						}

					}
				}).start();

			} else {
				Log.e("Error", METHODTAG + ": No Storage permissions, unable to run the update.");
				updateType = null;
				updateFile = null;
			}
		}
	}

	/**
	 *
	 */
	private void launchUpdateProcess(){

		final String METHODTAG = ".launchUpdateProcess";

		// /Send command
		//update start_app 2004<trm>
		//update start_app "FileSize"<trm>
		//Obtain packagesize -->this can be the buffersize for reading the file

		try {
			// Here, this Activity is the current activity
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				if (ContextCompat.checkSelfPermission(YetiInformationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(YetiInformationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

					// Should we show an explanation?
					if (ActivityCompat.shouldShowRequestPermissionRationale(YetiInformationActivity.this,
							Manifest.permission.READ_EXTERNAL_STORAGE)) {

						showAlert("Read and write permissions are needed to update the device");

						// Show an explanation to the user *asynchronously* -- don't block
						// this thread waiting for the user's response! After the user
						// sees the explanation, try again to request the permission.

					}

						// No explanation needed, we can request the permission.

							ActivityCompat.requestPermissions(YetiInformationActivity.this,
							new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
							APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE);

						// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
						// app-defined int constant. The callback method gets the
						// result of the request.

				}else{
					storagePermission = true;
					Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
					showUpdateTypeDialog();
				}
			}else{
				storagePermission = true;
				Log.d(CLASSTAG, "StoragePermission, Storage permission granted");
				showUpdateTypeDialog();
			}




		} catch (Exception e) {
			Log.e(CLASSTAG, METHODTAG, e);
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case APP_PERMISSIONS_REQUEST_READWRITE_EXTERNAL_STORAGE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showUpdateTypeDialog();

				} else {
					this.updateButton.setEnabled(false);
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	/**
	 * Show the corresponding UI elements for each of the models
	 * Different Leica models support different BTLE functionality.
	 * @param deviceModel Device Model
	 */
	private void setUI( String deviceModel ){
		modelName.setText(deviceModel);
	}

	/**
	 * show a dialog in which a user can type in a text and send it as a command
	 */
	void showCustomCommandDialog() {

		final String METHODTAG = ".showCustomCommandDialog";
		final EditText input = new EditText(this);
		AlertDialog.Builder customCommandDialogBuilder = new AlertDialog.Builder(this);
		customCommandDialogBuilder.setTitle(R.string.custom_command);
		customCommandDialogBuilder.setView(input);
		customCommandDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				// send any string to device
				try {
					sendCustomCommandHandler.post( new Runnable(){
						@Override
						public void run(){
							try {
								Response response = currentDevice.sendDistoComCommandSync(input.getText().toString(), false,currentDevice.getTIMEOUT_NORMAL());
								response.waitForData();

								if (response.getError() != null){
									Log.e(CLASSTAG,METHODTAG + ": error: " + response.getError().getErrorMessage());
								}

								for (ReceivedData receivedData : response.getReceivedData()){
									onAsyncDataReceived(receivedData);
								}
							} catch (DeviceException e) {

								Log.e(CLASSTAG, METHODTAG+ ": Error sending the command.", e);
							}
						}
					});
					//currentDevice.sendCustomCommand(input.getText().toString());
				} catch (Exception e) {
					Log.e(CLASSTAG, METHODTAG +": Error showCustomCommandDialog ", e);
				}
			}
		});
		customCommandDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		customCommandDialog = customCommandDialogBuilder.create();
		customCommandDialog.show();
	}
}