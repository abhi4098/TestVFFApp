package com.valleyforge.cdi.ui.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valleyforge.cdi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
import ch.leica.sdk.commands.response.Response;
import ch.leica.sdk.commands.response.ResponseBLEMeasurements;
import ch.leica.sdk.connection.BleConnectionManager;

/**
 * UI to diplay bluetooth device information.
 * Excluding Yeti.
 */
public class BLEInformationActivity extends AppCompatActivity implements ReceivedDataListener, Device.ConnectionListener, ErrorListener, ReconnectionHelper.ReconnectListener{

    /**
     * ClassName
     */
    private final String CLASSTAG = BLEInformationActivity.class.getSimpleName();

    static Device currentDevice;
    static ReconnectionHelper reconnectionHelper;

    static int defaultDirectionAngleUnit = MeasurementConverter.getDefaultDirectionAngleUnit();

    private boolean modelIsSet = false;
    //UI - Status of the connection


    //Textfields present information to user



    AlertDialog commandDialog;
    AlertDialog customCommandDialog;
    AlertDialog lostConnectionAlert;
    boolean lostConnectionAlertIsShown = false;

    boolean isWallWidthSelected = true;
    boolean isWidthLeftOfwindowSelected = false;
    boolean isIBWidthOfWindow = false;
    boolean isIBLenghtOfWindow = false;
    boolean isWidthRightOfwindowSelected = false;
    boolean isLengthCeilFlr = false;
    boolean PocketDepth = false;
    //boolean isWidthLeftOfwindowSelected = false;
    // boolean isWidthLeftOfwindowSelected = false;

    //  ButtonListener bl = new ButtonListener();

    boolean isDestroyed = false;
    boolean deviceIsInTrackingMode = false;
    boolean turnOnBluetoothDialogIsShown = false;

    //Measurement Values received from the device
    MeasuredValue distanceValue;
    MeasuredValue inclinationValue;
    MeasuredValue directionValue;

    Boolean receiverRegistered = false;
    boolean reconnectionIsRunning = false;

    private AlertDialog alertDialogConnect;
    private AlertDialog alertDialogDisconnect;

    private boolean hasDistanceMeasurement = false;

    String wallWidth, widthLeftOfWindow ,ibWidthOfWindow,ibLengthOfWindow,widthRightOfWindow,lengthCielFlr,pocketDepth,carpetInst,additionalData;

    @BindView(R.id.wall_width)
    EditText etWallWidth;

    @BindView(R.id.width_left_of_window)
    EditText etWidthLeftOfWindow;

    @BindView(R.id.ib_width_of_window)
    EditText etIbWidthOfWindow;
    @BindView(R.id.ib_length_of_window)
    EditText etIbLengthOfWindow;
    @BindView(R.id.width_right_of_window)
    EditText etWidthRightOfWindow;
    @BindView(R.id.length_of_ceil_floor)
    EditText etLengthCielFlr;
    @BindView(R.id.pocket_depth)
    EditText etPocketDepth;
    /*@BindView(R.id.carpet_inst)
    EditText tvCarpetInst;*/
  /*  @BindView(R.id.additional_data)
    EditText tvAdditionalData;*/


    @BindView(R.id.details)
    LinearLayout llDetails;

    @BindView(R.id.measurements)
    LinearLayout llMeasurement;

    @BindView(R.id.pictures)
    LinearLayout llPictures;

    @BindView(R.id.details_header)
    TextView tvDetails;

    @BindView(R.id.measurements_header)
    TextView tvMeasurement;

    @BindView(R.id.pictures_header)
    TextView tvPictures;

    @BindView(R.id.ll_on_details_click)
    LinearLayout llOnDetailsClick;

    @BindView(R.id.ll_on_pictures_click)
    LinearLayout llOnPicturesClick;


    @BindView(R.id.ll_on_measurement_click)
    LinearLayout llOnMeasurementClick;





    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;

    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.additional_data)
    EditText etAdditionalData;




    // listen to changes to the bluetooth adapter
    BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String METHODTAG =".bluetoothAdapterReceiver.receive()";
            Log.d(CLASSTAG, METHODTAG);
            checkForReconnection();
        }
    };

    @OnClick(R.id.details)
    public void detailsScreen(View view) {
        tvAppTitle.setText("Room Details");
        tvDetails.setTextColor(Color.parseColor("#ffffff")); // custom color
        llDetails.setBackgroundColor(Color.parseColor("#048700"));
        tvMeasurement.setTextColor(Color.parseColor("#252525")); // custom color
        llMeasurement.setBackgroundColor(Color.parseColor("#ffffff"));
        tvPictures.setTextColor(Color.parseColor("#252525")); // custom color
        llPictures.setBackgroundColor(Color.parseColor("#ffffff"));

        llOnDetailsClick.setVisibility(View.VISIBLE);
        llOnMeasurementClick.setVisibility(View.GONE);
        llOnPicturesClick.setVisibility(View.GONE);



    }


    @OnClick(R.id.measurements)
    public void measurementScreen(View view) {
        tvAppTitle.setText("Room Measurement");
        tvMeasurement.setTextColor(Color.parseColor("#ffffff")); // custom color
        llMeasurement.setBackgroundColor(Color.parseColor("#048700"));
        tvDetails.setTextColor(Color.parseColor("#252525")); // custom color
        llDetails.setBackgroundColor(Color.parseColor("#ffffff"));
        tvPictures.setTextColor(Color.parseColor("#252525")); // custom color
        llPictures.setBackgroundColor(Color.parseColor("#ffffff"));

        llOnDetailsClick.setVisibility(View.GONE);
        llOnMeasurementClick.setVisibility(View.VISIBLE);
        llOnPicturesClick.setVisibility(View.GONE);



    }

    @OnClick(R.id.pictures)
    public void picturesScreen(View view) {
        tvAppTitle.setText("Room Pictures");
        tvPictures.setTextColor(Color.parseColor("#ffffff")); // custom color
        llPictures.setBackgroundColor(Color.parseColor("#048700"));
        tvDetails.setTextColor(Color.parseColor("#252525")); // custom color
        llDetails.setBackgroundColor(Color.parseColor("#ffffff"));
        tvMeasurement.setTextColor(Color.parseColor("#252525")); // custom color
        llMeasurement.setBackgroundColor(Color.parseColor("#ffffff"));

        llOnDetailsClick.setVisibility(View.GONE);
        llOnMeasurementClick.setVisibility(View.GONE);
        llOnPicturesClick.setVisibility(View.VISIBLE);

    }



    @OnClick(R.id.width_left_of_window)
    public void setWidthLeftOfWindow(View view) {
         etWallWidth.setFocusableInTouchMode(false);
         etWidthLeftOfWindow.setFocusableInTouchMode(true);
         etIbWidthOfWindow.setFocusableInTouchMode(false);
         etIbLengthOfWindow.setFocusableInTouchMode(false);
         etWidthRightOfWindow.setFocusableInTouchMode(false);
         etLengthCielFlr.setFocusableInTouchMode(false);
         etPocketDepth.setFocusableInTouchMode(false);


         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = true;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = false;
         PocketDepth = false;
    }


    @OnClick(R.id.wall_width)
    public void wallwidth(View view) {
        etWallWidth.setFocusableInTouchMode(true);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(false);
        etIbLengthOfWindow.setFocusableInTouchMode(false);
        etWidthRightOfWindow.setFocusableInTouchMode(false);
        etLengthCielFlr.setFocusableInTouchMode(false);
        etPocketDepth.setFocusableInTouchMode(false);

         isWallWidthSelected = true;
        isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = false;
         PocketDepth = false;

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);



    }

    @OnClick(R.id.ib_width_of_window)
    public void setIBWidthOfWindow(View view) {
        etWallWidth.setFocusableInTouchMode(false);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(true);
        etIbLengthOfWindow.setFocusableInTouchMode(false);
        etWidthRightOfWindow.setFocusableInTouchMode(false);
        etLengthCielFlr.setFocusableInTouchMode(false);
        etPocketDepth.setFocusableInTouchMode(false);


         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = true;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = false;
         PocketDepth = false;


    }

    @OnClick(R.id.ib_length_of_window)
    public void setIBLenghtOfWindow(View view) {

        etWallWidth.setFocusableInTouchMode(false);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(false);
        etIbLengthOfWindow.setFocusableInTouchMode(true);
        etWidthRightOfWindow.setFocusableInTouchMode(false);
        etLengthCielFlr.setFocusableInTouchMode(false);
        etPocketDepth.setFocusableInTouchMode(false);

         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = true;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = false;
         PocketDepth = false;


    }

    @OnClick(R.id.width_right_of_window)
    public void setWidthRightOfWindow(View view) {


        etWallWidth.setFocusableInTouchMode(false);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(false);
        etIbLengthOfWindow.setFocusableInTouchMode(false);
        etWidthRightOfWindow.setFocusableInTouchMode(true);
        etLengthCielFlr.setFocusableInTouchMode(false);
        etPocketDepth.setFocusableInTouchMode(false);


         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = true;
         isLengthCeilFlr = false;
         PocketDepth = false;


    }

    @OnClick(R.id.length_of_ceil_floor)
    public void setLengthCielFlr(View view) {

        etWallWidth.setFocusableInTouchMode(false);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(false);
        etIbLengthOfWindow.setFocusableInTouchMode(false);
        etWidthRightOfWindow.setFocusableInTouchMode(false);
        etLengthCielFlr.setFocusableInTouchMode(true);
        etPocketDepth.setFocusableInTouchMode(false);

         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = true;
         PocketDepth = false;


    }

    @OnClick(R.id.pocket_depth)
    public void setPocketDepth(View view) {

        etWallWidth.setFocusableInTouchMode(false);
        etWidthLeftOfWindow.setFocusableInTouchMode(false);
        etIbWidthOfWindow.setFocusableInTouchMode(false);
        etIbLengthOfWindow.setFocusableInTouchMode(false);
        etWidthRightOfWindow.setFocusableInTouchMode(false);
        etLengthCielFlr.setFocusableInTouchMode(false);
        etPocketDepth.setFocusableInTouchMode(true);

         isWallWidthSelected = false;
         isWidthLeftOfwindowSelected = false;
         isIBWidthOfWindow = false;
         isIBLenghtOfWindow = false;
         isWidthRightOfwindowSelected = false;
         isLengthCeilFlr = false;
         PocketDepth = true;


    }


    @OnClick(R.id.save_btn)
    public void saveMeasurement(View view) {
        wallWidth = etWallWidth.getText().toString();
        widthLeftOfWindow = etWidthLeftOfWindow.getText().toString();
        Log.e("abhi", "widthLeftOfWindow:  ............" +wallWidth  );
        Log.e("abhi", "widthLeftOfWindow:  ............" +widthLeftOfWindow  );
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_information);
        ButterKnife.bind(this);
        ivBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivLogout.setVisibility(View.GONE);
        status.setVisibility(View.VISIBLE);
        tvAppTitle.setText("Room Details");
        //Initialize all UI Fields

        //inclinationLabel = (TextView) findViewById(R.id.inclinationLabel);
        // inclination = (TextView) findViewById(R.id.inclination);


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BLEInformationActivity.this);
        alertBuilder.setMessage(R.string.lostConnection);
        alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                lostConnectionAlertIsShown = false;
            }
        });
        lostConnectionAlert = alertBuilder.create();
        lostConnectionAlert.setCancelable(false);


        AlertDialog.Builder alertConnectedBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
        alertConnectedBuilder.setMessage("connection established");
        alertConnectedBuilder.setPositiveButton("Ok", null);

        AlertDialog.Builder alertDisconnectedBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
        alertDisconnectedBuilder.setMessage("lost connection to device");
        alertDisconnectedBuilder.setPositiveButton("Ok", null);

        alertDialogConnect 		= alertConnectedBuilder.create();
        alertDialogDisconnect 	= alertDisconnectedBuilder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register activity for bluetooth adapter changes
        if (!receiverRegistered) {
            receiverRegistered = true;
            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(bluetoothAdapterReceiver, filter);
        }

        // setup according to device
        if(currentDevice != null){
            currentDevice.setConnectionListener(this);
            currentDevice.setReceiveDataListener(this);
            currentDevice.setErrorListener(this);

            status.setText(currentDevice.getConnectionState().toString());
            status.setTextColor(Color.parseColor("#048700"));

            if(currentDevice.getConnectionState().equals(Device.ConnectionState.connected)){

                String model = currentDevice.getModel();
                if(model.isEmpty() == false) {
                    // setUI(model);
                }


            }
        }

        // Register activity for reconnection
        if (reconnectionHelper != null){
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

        // Unregister activity for adapter changes
        if (receiverRegistered) {
            unregisterReceiver(bluetoothAdapterReceiver);
            receiverRegistered = false;
        }

        // Dismiss opened dialogs
        if(commandDialog != null) {
            commandDialog.dismiss();
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

        //unregister activity for connection changes
        if (currentDevice != null){
            currentDevice.setReceiveDataListener(null);
            currentDevice.setConnectionListener(null);
            currentDevice.setErrorListener(null);
            currentDevice = null;
        }
        //unregister activity for reconnection
        if (reconnectionHelper != null){
            reconnectionHelper.setErrorListener(null);
            reconnectionHelper.setReconnectListener(null);
            reconnectionHelper.stopReconnecting();
            reconnectionHelper = null;
            reconnectionIsRunning = false;
        }

        if (currentDevice != null){
            //Disconnect the device
            currentDevice.disconnect();
            Log.d(CLASSTAG, METHODTAG + "Disconnected Device: "+currentDevice.modelName);

        }

    }

    /**
     * Show the corresponding UI elements for each of the models
     * Different Leica models support different BTLE functionality.
     * @param deviceModel Device Model
     */


    /**
     * Verify if the current device need to be reconnected.
     * If the device disconnected and the reconnection function has not been called, then it will start.
     */
    synchronized void checkForReconnection(){
        final String METHODTAG = ".checkForReconnection";
        Log.d(CLASSTAG,METHODTAG+": called");
        if (currentDevice == null){
            Log.d(CLASSTAG,METHODTAG+": device is null");
            return;
        }
        if (currentDevice.getConnectionState() == Device.ConnectionState.connected){
            Log.d(CLASSTAG,METHODTAG+": device connectionstate is connected ?!?!");
            return;
        }
        if (!currentDevice.getConnectionType().equals(Types.ConnectionType.ble)){
            Log.d(CLASSTAG,METHODTAG+": device is not ble ???");
            return;
        }
        if (turnOnBluetoothDialogIsShown){
            Log.d(CLASSTAG, METHODTAG+": turnOnBluetoothDialogIsShown is true");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {

                // wait a moment for bluetooth adapter to be setup, this can take a few seconds. if the app is not waiting here, there are bluetooth errors occuring
                WaitAmoment wait = new WaitAmoment();
                wait.waitAmoment(2000);

                // check if bluetooth is available
                boolean bluetoothIsAvailable = DeviceManager.getInstance(getApplicationContext()).checkBluetoothAvailibilty();

                if (!bluetoothIsAvailable){
                    Log.d(CLASSTAG,METHODTAG+": bluetooth is not available");

                    // show alert to turn on bluetooth
                    showBluetoothTurnOn();

                    return;
                }

                // if the reconnection is not running already, it will be called
                if (!reconnectionIsRunning){
                    reconnectionIsRunning = true;

                    Log.d(CLASSTAG,METHODTAG+": start reconnecting!!");
                    reconnectionHelper.startReconnecting();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText(R.string.reconnecting);

                            showAlert("Try to automatically reconnect now. (This may take a minute.)");
                        }
                    });
                }
            }
        }).start();
    }





    /**
     * set all textviews to zeros
     */
    void clear() {
        // set the current state as text
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etWallWidth.setText(R.string.default_value);
                etWidthLeftOfWindow.setText(R.string.default_value);
                //  distanceUnit.setText(R.string.default_value);

            }
        });
    }

    /**
     * Check if the device is disconnected, if it is disconnected launch the reconnection functiono
     *
     * @param device the device on which the connection state changed
     * @param state the current connection state. If state is disconnected, the device object is not valid anymore. No connection can be established with this object any more.
     */
    @Override
    public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {

        final String METHODTAG = ".onConnectionStateChanged";
        Log.d(CLASSTAG, METHODTAG +": " + device.getDeviceID() + ", state: " + state);

        try {
            // set the current state as text
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText(state.toString());
                }
            });

            // if disconnected, try reconnecting
            if (state == Device.ConnectionState.disconnected){
                showConnectedDisconnectedDialog(false);
                checkForReconnection();
                return;
            }
            else{
                // if connected ask for model. this will result in onDataReceived()

                //setUI(currentDevice.getModel());

            }

            showConnectedDisconnectedDialog(true);
        }catch(Exception e){
            Log.e(CLASSTAG, METHODTAG, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAsyncDataReceived(final ReceivedData receivedData) {

        final String METHODTAG = ".onDataReceived";
        Log.d(CLASSTAG, METHODTAG+ ": called.");



        if (receivedData != null) {
            try {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //loop over all the elements in the received data packet
                        // Set each element in the corresponding UI Element
                        for (ReceivedDataPacket receivedPacket : receivedData.dataPackets) {
                            try {
                                int id = receivedPacket.dataId;

                                switch (id) {
                                    //show the Model number from the ModelName characteristic in DISTO Service
                                    case Defines.ID_DS_MODEL_NAME: {
                                        String data = receivedPacket.getStringValue();
                                        // setUI(data);
                                        modelIsSet = true;
                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                    }
                                    break;
                                    //show the Model number from the ModelName characteristic in Device Information Service
                                    //fallback call if the Model Number from the DISTO Service is not available
                                    case Defines.ID_DI_MODEL_NUMBER: {

                                        if (modelIsSet == false) {
                                            String data = receivedPacket.getStringValue();
                                            // setUI(data);

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                        }

                                        Log.d(CLASSTAG, METHODTAG + ": Model has already been set");
                                    }
                                    break;

                                    //show the Firmware revision in Device Interface
                                    case Defines.ID_DI_FIRMWARE_REVISION: {
                                        String data = receivedPacket.getStringValue();

                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                    }
                                    break;
                                    //show the Hardware revision in Device Interface
                                    case Defines.ID_DI_HARDWARE_REVISION: {
                                        String data = receivedPacket.getStringValue();

                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                    }
                                    break;
                                    //Get the Firmware revision from Device Interface
                                    case Defines.ID_DI_MANUFACTURER_NAME_STRING: {
                                        String data = receivedPacket.getStringValue();

                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                    }
                                    break;
                                    //show the Firmware revision from Device Interface
                                    case Defines.ID_DI_SERIAL_NUMBER: {
                                        String data = receivedPacket.getStringValue();

                                        // only for below android 5 or if devicename is "too short" to have a serial number
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP || currentDevice.getDeviceName().length() <= 10) {

                                        }
                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                    }
                                    break;
                                    //Distance Measurement
                                    case Defines.ID_DS_DISTANCE: {
                                        if (deviceIsInTrackingMode == false) {
                                            //clears only UI
                                            // etWallWidth.setText(R.string.blank_value);
                                            // etWidthLeftOfWindow.setText(R.string.default_value);
                                            // distanceUnit.setText(R.string.blank_value);

                                        }
                                        float data = receivedPacket.getFloatValue();    // distance is always float
                                        distanceValue = new MeasuredValue(data);    // save the measured value, a unit may come in the next data packet
                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                        hasDistanceMeasurement = true;
                                    }
                                    break;
                                    //Assign units to the distance measured Value object and do the conversion
                                    case Defines.ID_DS_DISTANCE_UNIT: {

                                        if (distanceValue != null) {
                                            short data = receivedPacket.getShortValue();    // unit is always short
                                            distanceValue.setUnit(data);
                                            distanceValue.convertDistance();
                                            if (isWallWidthSelected) {
                                                etWallWidth.setText(distanceValue.getConvertedValueStrNoUnit());

                                                etWallWidth.setFocusableInTouchMode(true);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = true;
                                                isIBWidthOfWindow = false;
                                                isIBLenghtOfWindow = false;
                                                isWidthRightOfwindowSelected = false;
                                                isLengthCeilFlr = false;
                                                PocketDepth = false;
                                            } else if(isWidthLeftOfwindowSelected) {
                                                etWidthLeftOfWindow.setText(distanceValue.getConvertedValueStrNoUnit());

                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(true);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = false;
                                                isIBWidthOfWindow = true;
                                                isIBLenghtOfWindow = false;
                                                isWidthRightOfwindowSelected = false;
                                                isLengthCeilFlr = false;
                                                PocketDepth = false;
                                            }
                                            else if(isIBWidthOfWindow) {
                                                etIbWidthOfWindow.setText(distanceValue.getConvertedValueStrNoUnit());

                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(true);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = false;
                                                isIBWidthOfWindow = false;
                                                isIBLenghtOfWindow = true;
                                                isWidthRightOfwindowSelected = false;
                                                isLengthCeilFlr = false;
                                                PocketDepth = false;
                                            }
                                            else if(isIBLenghtOfWindow) {

                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(true);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                etIbLengthOfWindow.setText(distanceValue.getConvertedValueStrNoUnit());
                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = false;
                                                isIBWidthOfWindow = false;
                                                isIBLenghtOfWindow = false;
                                                isWidthRightOfwindowSelected = true;
                                                isLengthCeilFlr = false;
                                                PocketDepth = false;
                                            }
                                            else if(isWidthRightOfwindowSelected) {

                                                etWidthRightOfWindow.setText(distanceValue.getConvertedValueStrNoUnit());


                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(true);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = false;
                                                isIBWidthOfWindow = false;
                                                isIBLenghtOfWindow = false;
                                                isWidthRightOfwindowSelected = false;
                                                isLengthCeilFlr = true;
                                                PocketDepth = false;
                                            }
                                            else if(isLengthCeilFlr) {
                                                etLengthCielFlr.setText(distanceValue.getConvertedValueStrNoUnit());

                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(true);
                                                etPocketDepth.setFocusableInTouchMode(false);


                                                isWallWidthSelected = false;
                                                isWidthLeftOfwindowSelected = false;
                                                isIBWidthOfWindow = false;
                                                isIBLenghtOfWindow = false;
                                                isWidthRightOfwindowSelected = false;
                                                isLengthCeilFlr = false;
                                                PocketDepth = true;
                                            }
                                            else {
                                                etPocketDepth.setText(distanceValue.getConvertedValueStrNoUnit());

                                                etWallWidth.setFocusableInTouchMode(false);
                                                etWidthLeftOfWindow.setFocusableInTouchMode(false);
                                                etIbWidthOfWindow.setFocusableInTouchMode(false);
                                                etIbLengthOfWindow.setFocusableInTouchMode(false);
                                                etWidthRightOfWindow.setFocusableInTouchMode(false);
                                                etLengthCielFlr.setFocusableInTouchMode(false);
                                                etPocketDepth.setFocusableInTouchMode(true);

                                            }

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                            hasDistanceMeasurement = true;
                                        }
                                    }
                                    break;
                                    //Inclination Angle Measurement
                                    case Defines.ID_DS_INCLINATION: {
                                        if (deviceIsInTrackingMode == false) {
                                            //Clears only UI

                                        }
                                        float data = receivedPacket.getFloatValue();
                                        inclinationValue = new MeasuredValue(data);
                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                    }
                                    break;
                                    //Assign units to the Inclination Angle  measured Value object and do the conversion
                                    case Defines.ID_DS_INCLINATION_UNIT: {
                                        if (deviceIsInTrackingMode == false) {
                                            //Clears only UI

                                        }
                                        if (inclinationValue != null) {
                                            short data = receivedPacket.getShortValue();
                                            inclinationValue.setUnit(data);
                                            inclinationValue.convertAngle();

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);

                                        }

                                        if(hasDistanceMeasurement == false) {
                                            //  etWallWidth.setText(R.string.blank_value);
                                            //  etWidthLeftOfWindow.setText(R.string.blank_value);
                                            //distanceUnit.setText(R.string.blank_value);
                                        }
                                        hasDistanceMeasurement = false;
                                    }
                                    break;
                                    //Direction Angle Measurement
                                    case Defines.ID_DS_DIRECTION: {
                                        if (deviceIsInTrackingMode == false) {
                                            //Clears only UI

                                        }
                                        float data = receivedPacket.getFloatValue();
                                        directionValue = new MeasuredValue(data);

                                        //if(currentDevice.getModel().equals("D810") == false){

                                        directionValue.setUnit(defaultDirectionAngleUnit);
                                        directionValue.convertAngle();


                                        Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                        //}


                                    }
                                    break;
                                    //Assign units to the Direction Angle  measured Value object and do the conversion
                                    case Defines.ID_DS_DIRECTION_UNIT: {
                                        if (deviceIsInTrackingMode == false) {
                                            //Clears only UI

                                        }
                                        if (directionValue != null) {
                                            short data = receivedPacket.getShortValue();
                                            directionValue.setUnit(data);
                                            directionValue.convertAngle();

                                            Log.d(CLASSTAG, METHODTAG + ": called with id: " + id + ", value: " + data);
                                        }

                                        if(hasDistanceMeasurement == false) {
                                            //  etWallWidth.setText(R.string.blank_value);
                                            // etWidthLeftOfWindow.setText(R.string.blank_value);
                                            // distanceUnit.setText(R.string.blank_value);
                                        }
                                        hasDistanceMeasurement = false;
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentCheckedException e) {
                                Log.e(CLASSTAG, METHODTAG+": Error onAsyncDataReceived ", e);
                            } catch (WrongDataException e) {

                                Log.d(CLASSTAG, METHODTAG + " A wrong value has been set into the UI");
                                showAlert("Wrong Value Received.");

                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(CLASSTAG, METHODTAG+": Error onAsyncDataReceived ", e);
            }
        }else{
            Log.d(CLASSTAG, METHODTAG+": Error onAsyncDataReceived: receivedData object is null  ");
        }

    }

    /**
     *
     * @throws DeviceException
     */


    /**
     *
     * @throws DeviceException
     */

    /**
     * Defines the behavior of the buttons in the Activity
     */



    synchronized void showConnectedDisconnectedDialog(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    alertDialogConnect.show();
                    if(alertDialogDisconnect.isShowing()) {
                        alertDialogDisconnect.dismiss();
                    }
                } else {
                    alertDialogDisconnect.show();
                    if(alertDialogDisconnect.isShowing()) {
                        alertDialogConnect.dismiss();
                    }
                }
            }
        });
    }


    /**
     * Show alert messages
     * @param message message shown in the UI
     */
    public void showAlert(final String message){
        if (isDestroyed){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertBuilder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
                alertBuilder.setMessage(message);
                alertBuilder.setPositiveButton("Ok", null);
                alertBuilder.create().show();
            }
        });
    }

    /**
     * Show bluetooth turnOn dialog
     */
    synchronized void showBluetoothTurnOn(){
        final String METHODTAG = ".showBluetoothTurnOn";

        if(turnOnBluetoothDialogIsShown){
            Log.d(CLASSTAG, METHODTAG +": dialog is already shown");
            return;
        }

        turnOnBluetoothDialogIsShown = true;
        Log.d(CLASSTAG, METHODTAG +": turnOnBluetoothDialogIsShown is true");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BLEInformationActivity.this);
                builder.setMessage("Bluetooth has to be turned on.");
                builder.setCancelable(false);
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
     * Get object error and show it in the UI
     * @param errorObject errorObject send by the API
     */
    @Override
    public void onError(ErrorObject errorObject) {

        final String METHODTAG = ".onError";
        Log.e(CLASSTAG, METHODTAG +": "+ errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

        if (errorObject.getErrorCode() == ErrorDefinitions.AP_NO_WIFI_CONNECTED_CODE){ // || errorObject.getErrorCode() == ErrorDefinitions.AP_IS_CONNECTED_TO_HOTSPOT_CODE ){
            return;
        }
        showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
    }


    /**
     * On reconnect the newly connected device will be set as currentDevice.
     * And a new ReconnectionHelper will be created for the new device object.
     * @param device previously connected device
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
    }

    public static void setCurrentDevice(Device currentDevice, Context context) {
        BLEInformationActivity.currentDevice = currentDevice;
        reconnectionHelper = new ReconnectionHelper(currentDevice, context);
    }

    public void readDataFromResponseObject(Response response)  {

        final String METHODTAG = ".readDataFromResponseObject";

        if (response.getError() != null){

            Log.e(CLASSTAG, METHODTAG +": response error: " + response.getError().getErrorMessage());

            return;
        }

        if(response instanceof ResponseBLEMeasurements){
            this.extractDataFromBLEResponseObject((ResponseBLEMeasurements) response);
        }
    }

    /**
     * The ResponseWifiMeasurementExtract contains all measured data
     * @param response
     */
    public void extractDataFromBLEResponseObject(final ResponseBLEMeasurements response){


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MeasuredValue data = response.getDistanceValue();

                //Distance Measurement
                if(data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){
                    etWallWidth.setText(data.getConvertedValueStrNoUnit());
                    etWidthLeftOfWindow.setText(data.getConvertedValueStrNoUnit());
                    //  distanceUnit.setText(data.getUnitStr());
                }

                //Inclination Angle Measurement
                data = response.getAngleInclination();
                if(data != null && data.getConvertedValue() !=  Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){

                }

                //Direction Angle Measurement
                data = response.getAngleDirection();
                if(data != null && data.getConvertedValue() !=  Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)){

                }
            }

        });

    }


}