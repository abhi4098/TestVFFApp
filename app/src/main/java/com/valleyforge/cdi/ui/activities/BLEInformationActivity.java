package com.valleyforge.cdi.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ImageList;
import com.valleyforge.cdi.generated.model.MeasurementResponse;
import com.valleyforge.cdi.generated.model.UploadPhotoResponse;
import com.valleyforge.cdi.generated.model.WindowsListResponse;
import com.valleyforge.cdi.generated.model.Windowslist;
import com.valleyforge.cdi.ui.adapters.HLVAdapter;
import com.valleyforge.cdi.ui.adapters.HLVImagesAdapter;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

/**
 * UI to diplay bluetooth device information.
 * Excluding Yeti.
 */
public class BLEInformationActivity extends AppCompatActivity implements ReceivedDataListener, Device.ConnectionListener, ErrorListener, ReconnectionHelper.ReconnectListener {

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

    private int PICK_FROM_GALLERY = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private final String[] requiredPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    String imgDecodableString;


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

    String wallWidth, widthLeftOfWindow, ibWidthOfWindow, ibLengthOfWindow, widthRightOfWindow, lengthCielFlr, pocketDepth, carpetInst, additionalData;


    String IMAGE_URL = "http://myhostapp.com/vff-staging-new/la-assets/floors/";
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
    @BindView(R.id.carpet_inst)
    EditText etCarpetInst;
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
    RelativeLayout llOnDetailsClick;

    @BindView(R.id.ll_on_pictures_click)
    LinearLayout llOnPicturesClick;


    @BindView(R.id.ll_on_measurement_click)
    LinearLayout llOnMeasurementClick;

    /*@BindView(R.id.ceiling_pic_added_layout)
    LinearLayout llCeilingPicAddedLayout;*/

    /*@BindView(R.id.progress)
    ProgressBar imageProgressBar;*/

    @BindView(R.id.add_more_images)
    ImageView imageViewAddMoreImages;

    @BindView(R.id.ceiling_to_floor_textview)
    TextView tvCeilToFloor;

    @BindView(R.id.wall_to_wall_textview)
    TextView tvWallToWall;

    @BindView(R.id.windows_textview)
    TextView tvWindows;

    String imageTypeToBeSendViaAPi = "Ceiling to Floor";

    @OnClick(R.id.ceiling_to_floor_textview)
    public void ceilToFloorButtonSelected() {
        tvCeilToFloor.setBackgroundColor(Color.parseColor("#ffffff"));
        tvWallToWall.setBackgroundColor(Color.parseColor("#fbebdc"));
        tvWindows.setBackgroundColor(Color.parseColor("#fbebdc"));
        selectedImageType = "ceiltofloor";
        imageTypeToBeSendViaAPi = "Ceiling to Floor";
        combineImageListToBeShown = new ArrayList<>();
        for (int i = combineImageList.size() - 1; i >= 0; i--) {
            if (combineImageList.get(i).getimageType().equals(selectedImageType)) {
                ImageList imageList = new ImageList();
                imageList.setimageUrl(combineImageList.get(i).getimageUrl());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType);
        mRecyclerViewImages.setAdapter(mAdapter);
        LoadingDialog.cancelLoading();


    }

    @OnClick(R.id.wall_to_wall_textview)
    public void wallToWallButtonSelected() {
        Log.e("abhi", "wallToWallButtonSelected:................... ");
        selectedImageType = "walltowall";
        imageTypeToBeSendViaAPi = "Wall to Wall";
        tvCeilToFloor.setBackgroundColor(Color.parseColor("#fbebdc"));
        tvWallToWall.setBackgroundColor(Color.parseColor("#ffffff"));
        tvWindows.setBackgroundColor(Color.parseColor("#fbebdc"));
        combineImageListToBeShown = new ArrayList<>();
        for (int i = combineImageList.size() - 1; i >= 0; i--) {
            if (combineImageList.get(i).getimageType().equals(selectedImageType)) {
                ImageList imageList = new ImageList();
                imageList.setimageUrl(combineImageList.get(i).getimageUrl());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType);
        mRecyclerViewImages.setAdapter(mAdapter);
        LoadingDialog.cancelLoading();


    }

    @OnClick(R.id.windows_textview)
    public void windowsButtonSelected() {
        Log.e("abhi", "windowsButtonSelected:................... ");
        selectedImageType = "windows";
        imageTypeToBeSendViaAPi = "Windows";
        tvCeilToFloor.setBackgroundColor(Color.parseColor("#fbebdc"));
        tvWallToWall.setBackgroundColor(Color.parseColor("#fbebdc"));
        tvWindows.setBackgroundColor(Color.parseColor("#ffffff"));
        combineImageListToBeShown = new ArrayList<>();
        for (int i = combineImageList.size() - 1; i >= 0; i--) {
            if (combineImageList.get(i).getimageType().equals(selectedImageType)) {
                ImageList imageList = new ImageList();
                imageList.setimageUrl(combineImageList.get(i).getimageUrl());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType);
        mRecyclerViewImages.setAdapter(mAdapter);
        LoadingDialog.cancelLoading();

    }


    @OnClick(R.id.add_more_images)
    public void addImages() {
        Log.e("abhi", "addImages:................... ");
        Checkpermission();

    }


    /*@BindView(R.id.person_image)
    ImageView personImage;*/
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

    @BindView(R.id.floor_count)
    TextView tvFloorCount;

    @BindView(R.id.rooms_count)
    TextView tvRoomCount;

    /*@BindView(R.id.additional_data)
    EditText etAdditionalData;*/

   /* @BindView(R.id.ceiling_to_floor_button)
    LinearLayout llCeilingToFloor;

    @BindView(R.id.wall_to_wall_btn)
    LinearLayout llWallToWall;

    @BindView(R.id.wall_to_wall_pic_added_layout)
    LinearLayout llWallToWallPicAddedLayout;

    @BindView(R.id.windows_added_layout)
    LinearLayout llWindowsAddedLayout;


    @BindView(R.id.windows_btn)
    LinearLayout llWindows;*/

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.recycler_view_images)
    RecyclerView mRecyclerViewImages;

    @BindView(R.id.add_new_window_cardview)
    CardView cvAddWindow;


    String floorPlanId, roomId, floorName, roomName;

    String selectedImageType = "ceiltofloor";

    ArrayList<ImageList> combineImageList = new ArrayList<>();
    ArrayList<ImageList> combineImageListToBeShown = null;


    private RetrofitInterface.WindowsListClient WindowListAdapter;
    private RetrofitInterface.MeasurementDataClient MeasurementAdapter;
    private RetrofitInterface.uploadPhotosClient UploadPhotoAdapter;

    ArrayList<Windowslist> alWindows;
    //RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;



    @OnClick(R.id.add_new_window_cardview)
    public void addNewWindow(View view) {
        cvAddWindow.setCardBackgroundColor(Color.parseColor("#252525"));
        addWindow();


    }

    String windowName, windowDescription,windowCompletionStatus,windowApprovalCheck,windowId;

    public void addWindow() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_add_windows, null);
        final Button addWindowBtn = alertLayout.findViewById(R.id.add_window_btn);

        final EditText etWindowName = alertLayout.findViewById(R.id.window_name);
        final EditText etWindowDescription = alertLayout.findViewById(R.id.window_description);


        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BLEInformationActivity.this, "Cancel clicked", Toast.LENGTH_SHORT).show();

            }
        });


        final android.support.v7.app.AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(1000, 550);
        addWindowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvAddWindow.setCardBackgroundColor(Color.parseColor("#048700"));
                windowName = etWindowName.getText().toString();
                windowDescription = etWindowDescription.getText().toString();
                if (windowName != null && !windowName.equals("")) {
                    dialog.cancel();
                    List<String> testList = new ArrayList<>();
                    measurementScreen(v, windowName,"SameActivity", wallWidth, widthLeftOfWindow, widthRightOfWindow, ibLengthOfWindow, ibWidthOfWindow, lengthCielFlr,carpetInst, pocketDepth, "No", "No", testList, "id");

                } else {
                    etWindowName.setError("Please Add Window Name");

                }

            }
        });
    }


    // listen to changes to the bluetooth adapter
    BroadcastReceiver bluetoothAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String METHODTAG = ".bluetoothAdapterReceiver.receive()";
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
        setUpRestAdapter();
        setWindowsList();


    }


    @OnClick(R.id.measurements)
    public void measureScreen(View view) {
        if (windowName != null) {
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
        else
        {
            Toast.makeText(BLEInformationActivity.this,"Please Select a Window", Toast.LENGTH_SHORT).show();
        }


    }

    public void measurementScreen(View view, String window, String pathFrom, String ewallWidth, String widthLeftWindow, String widthRightWindow, String ibLengthWindow, String ibWidthWindow, String lengthCeilFlr, String carpetInst, String pocketDepth, String windowApproval, String windowStatus, List<String> allimages, String id) {
        windowName = window;
        windowCompletionStatus = windowStatus;
        windowApprovalCheck = windowApproval;
        windowId = id;
        Log.e("abhi", "measurementScreen: ....." +ewallWidth + " " +widthLeftWindow);
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

        if (pathFrom.equals("fromHLVAdapter"))
        {

            etWallWidth.setText(ewallWidth);
            etWidthLeftOfWindow.setText(widthLeftWindow);
            etIbWidthOfWindow.setText(ibWidthWindow);
            etIbLengthOfWindow.setText(ibLengthWindow);
            etWidthRightOfWindow.setText(widthRightWindow);
            etLengthCielFlr.setText(lengthCeilFlr);
            etPocketDepth.setText(pocketDepth);
            etCarpetInst.setText(carpetInst);

            for (int j=0; j < allimages.size(); j++)
            {
                String url = allimages.get(j);
                String[] separated = url.split("/");

                Log.e("abhi", "measurementScreen:................folder name " +separated[0]);

                ImageList imageList = new ImageList();
                imageList.setimageUrl(IMAGE_URL + allimages.get(j));
                Log.e("abhi", "measurementScreen:................folder name " +imageList.getimageUrl());
            }

        }




    }

    @OnClick(R.id.pictures)
    public void picturesScreen(View view) {
        if (windowName != null) {
            tvAppTitle.setText("Room Pictures");
            tvPictures.setTextColor(Color.parseColor("#ffffff")); // custom color
            llPictures.setBackgroundColor(Color.parseColor("#048700"));
            tvDetails.setTextColor(Color.parseColor("#252525")); // custom color
            llDetails.setBackgroundColor(Color.parseColor("#ffffff"));
            tvMeasurement.setTextColor(Color.parseColor("#252525")); // custom color
            llMeasurement.setBackgroundColor(Color.parseColor("#ffffff"));
            tvCeilToFloor.setBackgroundColor(Color.parseColor("#ffffff"));

            llOnDetailsClick.setVisibility(View.GONE);
            llOnMeasurementClick.setVisibility(View.GONE);
            llOnPicturesClick.setVisibility(View.VISIBLE);
        }
        else
        {
            Toast.makeText(BLEInformationActivity.this,"Please Select a Window", Toast.LENGTH_SHORT).show();

        }

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
        ibWidthOfWindow = etIbWidthOfWindow.getText().toString();
        ibLengthOfWindow = etIbLengthOfWindow.getText().toString();
        widthRightOfWindow = etWidthRightOfWindow.getText().toString();
        lengthCielFlr = etLengthCielFlr.getText().toString();
        pocketDepth = etPocketDepth.getText().toString();
        carpetInst = etCarpetInst.getText().toString();
        additionalData = "comment";

        Log.e("abhi", "wallWidth:  ............" + wallWidth);
        Log.e("abhi", "widthLeftOfWindow:  ............" + widthLeftOfWindow);
        Log.e("abhi", "ibWidthOfWindow:  ............" + ibWidthOfWindow);
        Log.e("abhi", "ibLengthOfWindow:  ............" + ibLengthOfWindow);
        Log.e("abhi", "widthRightOfWindow:  ............" + widthRightOfWindow);
        Log.e("abhi", "lengthCielFlr:  ............" + lengthCielFlr);
        Log.e("abhi", "pocketDepth:  ............" + pocketDepth);
        Log.e("abhi", "carpetInst:  ............" + carpetInst);
        Log.e("abhi", "additionalData:  ............" + additionalData);


        if ((wallWidth != null && !wallWidth.equals("")) &&(widthLeftOfWindow != null && !widthLeftOfWindow.equals(""))
                &&(ibWidthOfWindow != null && !ibWidthOfWindow.equals(""))&&(ibLengthOfWindow != null && !ibLengthOfWindow.equals(""))
                &&(widthRightOfWindow != null && !widthRightOfWindow.equals(""))&& (lengthCielFlr != null && !lengthCielFlr.equals(""))
                && (pocketDepth != null && !pocketDepth.equals(""))&&(carpetInst != null && !carpetInst.equals("")))
        {
            setUpRestAdapter();
            sendMeasurementData(view);

        } else if (widthLeftOfWindow == null && widthLeftOfWindow.equals("")) {
            etWidthLeftOfWindow.setError("Add Width Left Of Window");
        } else if (ibWidthOfWindow == null && ibWidthOfWindow.equals("")) {
            etIbWidthOfWindow.setError("Add Ib Width Of Window");
        } else if (ibLengthOfWindow == null && ibLengthOfWindow.equals("")) {
            etIbLengthOfWindow.setError("Add Ib Length Of Window");
        } else if (widthRightOfWindow == null && widthRightOfWindow.equals("")) {
            etWidthRightOfWindow.setError("Add Width Right Of Window");
        } else if (lengthCielFlr == null && lengthCielFlr.equals("")) {
            etLengthCielFlr.setError("Add Length Ciel Flr");
        } else if (pocketDepth == null && pocketDepth.equals("")) {
            etPocketDepth.setError("Add Pocket Depth");
        }
        else if (wallWidth == null && wallWidth.equals("")){
            etWallWidth.setError("Add Wall Width");
            }
        else if (carpetInst == null && carpetInst.equals("")){
            etWallWidth.setError("Add Carpet Inst");
        }

            else
        {
            Toast.makeText(BLEInformationActivity.this,"Please fill all required Fields", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendMeasurementData(final View view) {
        Log.e("abhi", "sendMeasurementData: ..................." + windowId);
        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<MeasurementResponse> call = MeasurementAdapter.measurementData(floorPlanId, roomId, windowName, wallWidth, widthLeftOfWindow, ibWidthOfWindow, ibLengthOfWindow, widthRightOfWindow, lengthCielFlr,
                pocketDepth, carpetInst,windowCompletionStatus,windowApprovalCheck,windowId);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<MeasurementResponse>() {

                @Override
                public void onResponse(Call<MeasurementResponse> call, retrofit2.Response<MeasurementResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                            Log.e("abhi", "onResponse:........... " + response.body().getMsg());
                            picturesScreen(view);


                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<MeasurementResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... ");

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
        }

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
        floorPlanId = getIntent().getStringExtra("FLOOR_ID");
        roomId = getIntent().getStringExtra("ROOM_ID");
        floorName = getIntent().getStringExtra("FLOOR_NAME");
        roomName = getIntent().getStringExtra("ROOM_NAME");
        tvFloorCount.setText(floorName);
        tvRoomCount.setText(roomName);
        Log.e("abhi", "onCreate: ................................floor plan id " + floorPlanId + " room id " + roomId);
        setUpRestAdapter();
        setWindowsList();

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

        alertDialogConnect = alertConnectedBuilder.create();
        alertDialogDisconnect = alertDisconnectedBuilder.create();
    }


    private void setUpRestAdapter() {
        WindowListAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.WindowsListClient.class, BASE_URL, this);
        MeasurementAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.MeasurementDataClient.class, BASE_URL, this);
        UploadPhotoAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.uploadPhotosClient.class, BASE_URL, this);

    }

    private void setWindowsList() {
        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<WindowsListResponse> call = WindowListAdapter.windowsListData(Integer.parseInt(floorPlanId), roomId);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<WindowsListResponse>() {

                @Override
                public void onResponse(Call<WindowsListResponse> call, retrofit2.Response<WindowsListResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                            Log.e("abhi", "onResponse:........... " + response.body().getMsg());
                            alWindows = new ArrayList<>();
                            for (int i = 0; i < response.body().getWindowslist().size(); i++) {


                                Windowslist windowslist = new Windowslist();
                                windowslist.setFloorPlanId(response.body().getWindowslist().get(i).getFloorPlanId());
                                windowslist.setFloorRoom(response.body().getWindowslist().get(i).getFloorRoom());
                                windowslist.setId(response.body().getWindowslist().get(i).getId());
                                windowslist.setWindow(response.body().getWindowslist().get(i).getWindow());
                                windowslist.setWindowStatus(response.body().getWindowslist().get(i).getWindowStatus());
                                windowslist.setWindowApproval(response.body().getWindowslist().get(i).getWindowApproval());


                                windowslist.setWallWidth(response.body().getWindowslist().get(i).getWallWidth());
                                windowslist.setIbLengthWindow(response.body().getWindowslist().get(i).getIbLengthWindow());
                                windowslist.setIbWidthWindow(response.body().getWindowslist().get(i).getIbWidthWindow());
                                windowslist.setWidthLeftWindow(response.body().getWindowslist().get(i).getWidthLeftWindow());
                                windowslist.setWidthRightWindow(response.body().getWindowslist().get(i).getWidthRightWindow());
                                windowslist.setPocketDepth(response.body().getWindowslist().get(i).getPocketDepth());
                                windowslist.setCarpetInst(response.body().getWindowslist().get(i).getCarpetInst());
                                windowslist.setLengthCeilFlr(response.body().getWindowslist().get(i).getLengthCeilFlr());

                                windowslist.setAllimages(response.body().getWindowslist().get(i).getAllimages());



                                Log.e("abhi", "onResponse:................. " + response.body().getWindowslist().get(i).getAllimages());


                                alWindows.add(windowslist);


                            }

                            mRecyclerView.setHasFixedSize(true);
                            mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            mAdapter = new HLVAdapter(BLEInformationActivity.this, alWindows, "windowList", "test", "test2");
                            mRecyclerView.setAdapter(mAdapter);
                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            LoadingDialog.cancelLoading();
                        } else {
                            LoadingDialog.cancelLoading();
                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<WindowsListResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... ");

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
        }
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
        if (currentDevice != null) {
            currentDevice.setConnectionListener(this);
            currentDevice.setReceiveDataListener(this);
            currentDevice.setErrorListener(this);

            status.setText(currentDevice.getConnectionState().toString());
            status.setTextColor(Color.parseColor("#048700"));

            if (currentDevice.getConnectionState().equals(Device.ConnectionState.connected)) {

                String model = currentDevice.getModel();
                if (model.isEmpty() == false) {
                    // setUI(model);
                }


            }
        }

        // Register activity for reconnection
        if (reconnectionHelper != null) {
            reconnectionHelper.setErrorListener(this);
            reconnectionHelper.setReconnectListener(this);
        }

        if (reconnectionIsRunning) {
            status.setText(R.string.reconnecting);
        }

        // start bt connection
        if (currentDevice != null) {
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
        if (commandDialog != null) {
            commandDialog.dismiss();
        }

        if (customCommandDialog != null) {
            customCommandDialog.dismiss();
        }

        // Pause the bluetooth connection
        if (currentDevice != null) {
            currentDevice.pauseBTConnection();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final String METHODTAG = ".onDestroy";
        isDestroyed = true;

        //unregister activity for connection changes
        if (currentDevice != null) {
            currentDevice.setReceiveDataListener(null);
            currentDevice.setConnectionListener(null);
            currentDevice.setErrorListener(null);
            currentDevice = null;
        }
        //unregister activity for reconnection
        if (reconnectionHelper != null) {
            reconnectionHelper.setErrorListener(null);
            reconnectionHelper.setReconnectListener(null);
            reconnectionHelper.stopReconnecting();
            reconnectionHelper = null;
            reconnectionIsRunning = false;
        }

        if (currentDevice != null) {
            //Disconnect the device
            currentDevice.disconnect();
            Log.d(CLASSTAG, METHODTAG + "Disconnected Device: " + currentDevice.modelName);

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
    synchronized void checkForReconnection() {
        final String METHODTAG = ".checkForReconnection";
        Log.d(CLASSTAG, METHODTAG + ": called");
        if (currentDevice == null) {
            Log.d(CLASSTAG, METHODTAG + ": device is null");
            return;
        }
        if (currentDevice.getConnectionState() == Device.ConnectionState.connected) {
            Log.d(CLASSTAG, METHODTAG + ": device connectionstate is connected ?!?!");
            return;
        }
        if (!currentDevice.getConnectionType().equals(Types.ConnectionType.ble)) {
            Log.d(CLASSTAG, METHODTAG + ": device is not ble ???");
            return;
        }
        if (turnOnBluetoothDialogIsShown) {
            Log.d(CLASSTAG, METHODTAG + ": turnOnBluetoothDialogIsShown is true");
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

                if (!bluetoothIsAvailable) {
                    Log.d(CLASSTAG, METHODTAG + ": bluetooth is not available");

                    // show alert to turn on bluetooth
                    showBluetoothTurnOn();

                    return;
                }

                // if the reconnection is not running already, it will be called
                if (!reconnectionIsRunning) {
                    reconnectionIsRunning = true;

                    Log.d(CLASSTAG, METHODTAG + ": start reconnecting!!");
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
     * @param state  the current connection state. If state is disconnected, the device object is not valid anymore. No connection can be established with this object any more.
     */
    @Override
    public void onConnectionStateChanged(Device device, final Device.ConnectionState state) {

        final String METHODTAG = ".onConnectionStateChanged";
        Log.d(CLASSTAG, METHODTAG + ": " + device.getDeviceID() + ", state: " + state);

        try {
            // set the current state as text
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText(state.toString());
                }
            });

            // if disconnected, try reconnecting
            if (state == Device.ConnectionState.disconnected) {
                showConnectedDisconnectedDialog(false);
                checkForReconnection();
                return;
            } else {
                // if connected ask for model. this will result in onDataReceived()

                //setUI(currentDevice.getModel());

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

        final String METHODTAG = ".onDataReceived";
        Log.d(CLASSTAG, METHODTAG + ": called.");


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
                                            } else if (isWidthLeftOfwindowSelected) {
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
                                            } else if (isIBWidthOfWindow) {
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
                                            } else if (isIBLenghtOfWindow) {

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
                                            } else if (isWidthRightOfwindowSelected) {

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
                                            } else if (isLengthCeilFlr) {
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
                                            } else {
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

                                        if (hasDistanceMeasurement == false) {
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

                                        if (hasDistanceMeasurement == false) {
                                            //  etWallWidth.setText(R.string.blank_value);
                                            // etWidthLeftOfWindow.setText(R.string.blank_value);
                                            // distanceUnit.setText(R.string.blank_value);
                                        }
                                        hasDistanceMeasurement = false;
                                    }
                                    break;
                                }
                            } catch (IllegalArgumentCheckedException e) {
                                Log.e(CLASSTAG, METHODTAG + ": Error onAsyncDataReceived ", e);
                            } catch (WrongDataException e) {

                                Log.d(CLASSTAG, METHODTAG + " A wrong value has been set into the UI");
                                showAlert("Wrong Value Received.");

                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(CLASSTAG, METHODTAG + ": Error onAsyncDataReceived ", e);
            }
        } else {
            Log.d(CLASSTAG, METHODTAG + ": Error onAsyncDataReceived: receivedData object is null  ");
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


    /**
     * Show alert messages
     *
     * @param message message shown in the UI
     */
    public void showAlert(final String message) {
        if (isDestroyed) {
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
    synchronized void showBluetoothTurnOn() {
        final String METHODTAG = ".showBluetoothTurnOn";

        if (turnOnBluetoothDialogIsShown) {
            Log.d(CLASSTAG, METHODTAG + ": dialog is already shown");
            return;
        }

        turnOnBluetoothDialogIsShown = true;
        Log.d(CLASSTAG, METHODTAG + ": turnOnBluetoothDialogIsShown is true");

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
                Log.d(CLASSTAG, METHODTAG + ": SHOW");
            }
        });
    }

    /**
     * Get object error and show it in the UI
     *
     * @param errorObject errorObject send by the API
     */
    @Override
    public void onError(ErrorObject errorObject) {

        final String METHODTAG = ".onError";
        Log.e(CLASSTAG, METHODTAG + ": " + errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());

        if (errorObject.getErrorCode() == ErrorDefinitions.AP_NO_WIFI_CONNECTED_CODE) { // || errorObject.getErrorCode() == ErrorDefinitions.AP_IS_CONNECTED_TO_HOTSPOT_CODE ){
            return;
        }
        showAlert(errorObject.getErrorMessage() + ", errorCode: " + errorObject.getErrorCode());
    }


    /**
     * On reconnect the newly connected device will be set as currentDevice.
     * And a new ReconnectionHelper will be created for the new device object.
     *
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

    public void readDataFromResponseObject(Response response) {

        final String METHODTAG = ".readDataFromResponseObject";

        if (response.getError() != null) {

            Log.e(CLASSTAG, METHODTAG + ": response error: " + response.getError().getErrorMessage());

            return;
        }

        if (response instanceof ResponseBLEMeasurements) {
            this.extractDataFromBLEResponseObject((ResponseBLEMeasurements) response);
        }
    }

    /**
     * The ResponseWifiMeasurementExtract contains all measured data
     *
     * @param response
     */
    public void extractDataFromBLEResponseObject(final ResponseBLEMeasurements response) {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MeasuredValue data = response.getDistanceValue();

                //Distance Measurement
                if (data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)) {
                    etWallWidth.setText(data.getConvertedValueStrNoUnit());
                    etWidthLeftOfWindow.setText(data.getConvertedValueStrNoUnit());
                    //  distanceUnit.setText(data.getUnitStr());
                }

                //Inclination Angle Measurement
                data = response.getAngleInclination();
                if (data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)) {

                }

                //Direction Angle Measurement
                data = response.getAngleDirection();
                if (data != null && data.getConvertedValue() != Defines.defaultFloatValue && !data.getUnitStr().equals(Defines.defaultStringValue)) {

                }
            }

        });

    }


    private void Checkpermission() {

        if (getPermissions()) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                makeRequest();
            } else {
                makeRequest();
            }
        } else {
            setDialogForImage();
        }
    }

    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                requiredPermissions,
                REQUEST_WRITE_STORAGE);
    }


    private void setDialogForImage() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_from_source);
        ImageView btnCamera = (ImageView) dialog.findViewById(R.id.btnCamera);
        ImageView btnDocs = (ImageView) dialog.findViewById(R.id.btnDoc);
        TextView txtDoc = (TextView) dialog.findViewById(R.id.txtDoc);
        btnDocs.setVisibility(View.GONE);
        txtDoc.setVisibility(View.GONE);
        ImageView btnGallery = (ImageView) dialog.findViewById(R.id.btnGallery);

        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP;
        wmlp.x = 0;   //x position
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
        wmlp.y = (int) px; //y position
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        dialog.getWindow().setLayout((6 * width) / 10, Toolbar.LayoutParams.WRAP_CONTENT);
        dialog.show();


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);

                dialog.cancel();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                dialog.cancel();

            }
        });

    }


    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 500, 500);

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRect(0, 0, 500, 500, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {


            Bitmap bp = (Bitmap) data.getExtras().get("data");

            if (bp != null) {
                // personImage.setImageBitmap(getCircularBitmap(bp));
                Uri tempUri = getImageUri(getApplicationContext(), bp);
                File filePath = new File(getRealPathFromURI(tempUri));
                // imageProgressBar.setVisibility(View.VISIBLE);
                // setProfilePicURL(filePath.getPath());
                sendImagesToServerFromCamera(filePath.getPath());
                Log.e("abhi", "onActivityResult:   on taking pic from camera......................" + filePath.getPath());
            }


        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {

            //  personImage.setBackgroundResource(R.drawable.profile_icon);


            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                //setProfilePicURL(imgDecodableString);
                sendImagesToServerFromCamera(imgDecodableString);
            }

            Log.e("abhi", "onActivityResult: image decodable " + imgDecodableString);
            //imageProgressBar.setVisibility(View.VISIBLE);
            Log.e("abhi", "onActivityResult:..........  from gallery " + imgDecodableString);


        }
    }


    private void sendImagesToServerFromCamera(String imgString) {
        MultipartBody.Part fileToUpload = null;
        if (imgString != null) {
            File imgPath = new File(imgString);

            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpg"), imgPath);
            fileToUpload = MultipartBody.Part.createFormData("file", imgPath.getName(), mFile);
        }
        RequestBody userId = RequestBody.create(
                MediaType.parse("text/plain"),
                "8");

        RequestBody measurementId = RequestBody.create(
                MediaType.parse("text/plain"),
                "5");

        final RequestBody imageType = RequestBody.create(
                MediaType.parse("text/plain"),
                imageTypeToBeSendViaAPi);


        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<UploadPhotoResponse> call = UploadPhotoAdapter.uploadImageData(userId, measurementId, fileToUpload, imageType);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<UploadPhotoResponse>() {

                @Override
                public void onResponse(Call<UploadPhotoResponse> call, retrofit2.Response<UploadPhotoResponse> response) {

                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {

                            Log.e("abhi", "onResponse: .............................................." + response.body().getMsg());
                            Log.e("abhi", "onResponse: image link............" + response.body().getImageurl());
                            ImageList imageList = new ImageList();
                            imageList.setimageUrl(response.body().getImageurl());
                            imageList.setimageType(selectedImageType);
                            combineImageList.add(imageList);


                        }
                        combineImageListToBeShown = new ArrayList<>();
                        for (int i = combineImageList.size() - 1; i >= 0; i--) {
                            if (combineImageList.get(i).getimageType().equals(selectedImageType)) {
                                ImageList imageList = new ImageList();
                                imageList.setimageUrl(combineImageList.get(i).getimageUrl());
                                imageList.setimageType(selectedImageType);
                                combineImageListToBeShown.add(imageList);
                            }
                        }

                        mRecyclerViewImages.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        mRecyclerViewImages.setLayoutManager(mLayoutManager);

                        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType);
                        mRecyclerViewImages.setAdapter(mAdapter);
                        LoadingDialog.cancelLoading();


                    }
                }

                @Override
                public void onFailure(Call<UploadPhotoResponse> call, Throwable t) {
                    Log.e("abhi", "onFailure: ............" + t.getCause());
                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromURI(Uri tempUri) {
        Cursor cursor = getContentResolver().query(tempUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private boolean getPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            return true;
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


}