package com.valleyforge.cdi.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.annotation.RequiresApi;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.ApiEndPoints;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.Allimage;
import com.valleyforge.cdi.generated.model.ImageList;
import com.valleyforge.cdi.generated.model.MeasurementDetail;
import com.valleyforge.cdi.generated.model.MeasurementResponse;
import com.valleyforge.cdi.generated.model.SubmitWindowDetailResponse;
import com.valleyforge.cdi.generated.model.UploadPhotoResponse;
import com.valleyforge.cdi.generated.model.WindowsListResponse;
import com.valleyforge.cdi.generated.model.Windowslist;
import com.valleyforge.cdi.generated.tables.MeasurementDetailTable;
import com.valleyforge.cdi.generated.tables.PListTable;
import com.valleyforge.cdi.ui.adapters.HLVAdapter;
import com.valleyforge.cdi.ui.adapters.HLVImagesAdapter;
import com.valleyforge.cdi.ui.services.MyJobService;
import com.valleyforge.cdi.utils.IImageCompressTaskListener;
import com.valleyforge.cdi.utils.ImageCompressTask;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;
import static java.lang.Integer.parseInt;

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
    Uri imageUri;

    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private ImageCompressTask imageCompressTask;
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
    private boolean isMeasurementDataSubmitted = false;

    String wallWidth, widthLeftOfWindow, ibWidthOfWindow, ibLengthOfWindow, widthRightOfWindow, lengthCielFlr, pocketDepth, carpetInst, additionalData;


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
    @BindView(R.id.spinner_carpet_inst)
    Spinner spCarpetInst;
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
                imageList.setImageId(combineImageList.get(i).getImageId());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType, windowId);
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
                imageList.setImageId(combineImageList.get(i).getImageId());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType, windowId);
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
                imageList.setImageId(combineImageList.get(i).getImageId());
                imageList.setimageType(selectedImageType);
                combineImageListToBeShown.add(imageList);
            }
        }

        mRecyclerViewImages.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewImages.setLayoutManager(mLayoutManager);

        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType, windowId);
        mRecyclerViewImages.setAdapter(mAdapter);
        LoadingDialog.cancelLoading();

    }


    @OnClick(R.id.add_more_images)
    public void addImages() {
        Log.e("abhi", "addImages:................... ");
        Checkpermission();

    }


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

    @BindView(R.id.carpet_inst_ll)
    LinearLayout llCarpetInst;



    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.recycler_view_images)
    RecyclerView mRecyclerViewImages;

    @BindView(R.id.add_new_window_cardview)
    CardView cvAddWindow;

    @BindView(R.id.submit_button)
    Button btnSubmitWindowDetail;


    String floorPlanId, roomId, floorName, roomName,imagesID,selectedCarpetInstValue;
    boolean isWindow = false;
    boolean isCeilToCeil =false;
    boolean isWallToWall =false;
    String selectedImageType = "ceiltofloor" ;

    static ArrayList<ImageList> combineImageList = new ArrayList<>();
    ArrayList<ImageList> combineImageListToBeShown = null;


    private RetrofitInterface.WindowsListClient WindowListAdapter;
    private RetrofitInterface.MeasurementDataClient MeasurementAdapter;
    private RetrofitInterface.uploadPhotosClient UploadPhotoAdapter;
    private RetrofitInterface.SubmitWindowsData SubmitWindowDataAdapter;

    ArrayList<Windowslist> alWindows;
    //RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @OnClick(R.id.submit_button)
    public void submitWindowData(View view) {
        btnSubmitWindowDetail.setVisibility(View.VISIBLE);
        for (int i = combineImageList.size() - 1; i >= 0; i--) {
            if (combineImageList.get(i).getimageType().equals("ceiltofloor")) {
                isCeilToCeil = true;
            }
            if (combineImageList.get(i).getimageType().equals("walltowall")) {
                isWallToWall = true;
            }
            if (combineImageList.get(i).getimageType().equals("windows")) {
                isWindow = true;
            }
        }

        if (isWindow && isWallToWall && isCeilToCeil) {
            sendWindowData(view);
        }
        else
        {
            Toast.makeText(BLEInformationActivity.this, "Please Add All Images", Toast.LENGTH_SHORT).show();

        }


    }

    private void sendWindowData(final View view) {

        Log.e("abhi", "sendMeasurementData: ..................." + windowId);
        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<SubmitWindowDetailResponse> call = SubmitWindowDataAdapter.windowDetail(floorPlanId, roomId,windowId,PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<SubmitWindowDetailResponse>() {

                @Override
                public void onResponse(Call<SubmitWindowDetailResponse> call, retrofit2.Response<SubmitWindowDetailResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                            isMeasurementDataSubmitted = true;
                            isCeilToCeil = false;
                            isWallToWall = false;
                            isWindow = false;
                            for (int i=0; i<response.body().getMeasurementDetails().size(); i++)
                            {
                                if (response.body().getSubmittedForReview().equals(1))
                                {

                                    Log.e("abhi", "onResponse: .........................submitted for review " );
                                    LayoutInflater inflater = getLayoutInflater();
                                    View alertLayout = inflater.inflate(R.layout.layout_project_completion, null);
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(BLEInformationActivity.this);
                                    builder1.setView(alertLayout);
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(BLEInformationActivity.this,NavigationActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                                else
                                {

                                    Log.e("abhi", "onResponse:.............. Project not completed "  );

                                }
                                windowId = String.valueOf(response.body().getMeasurementDetails().get(i).getId());
                            }
                            detailsScreen(view);


                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(BLEInformationActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<SubmitWindowDetailResponse> call, Throwable t) {

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            saveMeasurementDataInDb();
            ComponentName componentName = new ComponentName(this, MyJobService.class);
            JobInfo jobInfo = null;
            JobScheduler jobScheduler = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                jobInfo = new JobInfo.Builder(parseInt(windowId), componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
                jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
                assert jobScheduler != null;
                int resultCode = jobScheduler.schedule(jobInfo);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Log.d("abhi", "Job scheduled!");
                } else {
                    Log.d("abhi", "Job not scheduled");
                }
            }


            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }

    }

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
                    List<Allimage> testList = new ArrayList<>();
                    measurementScreen(v, windowName,"SameActivity", wallWidth, widthLeftOfWindow, widthRightOfWindow, ibLengthOfWindow, ibWidthOfWindow, lengthCielFlr,carpetInst, pocketDepth, "No", "No", testList, "");

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
        btnSubmitWindowDetail.setVisibility(View.GONE);
        isMeasurementDataSubmitted = false;
        isCeilToCeil = false;
        isWallToWall = false;
        isWindow = false;
        windowId = null;
        windowName = null;
        if(combineImageList != null) {
            combineImageList.clear();
        }

        if(combineImageListToBeShown !=null) {
            combineImageListToBeShown.clear();
        }
        etWallWidth.setText("");
        etWidthLeftOfWindow.setText("");
        etIbWidthOfWindow.setText("");
        etIbLengthOfWindow.setText("");
        etWidthRightOfWindow.setText("");
        etLengthCielFlr.setText("");
        etPocketDepth.setText(pocketDepth);
       /* if (carpetInst.equals("Yes")) {
            spCarpetInst.setSelection(0);
        }
        else
        {
            spCarpetInst.setSelection(1);
        }*/
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
        btnSubmitWindowDetail.setVisibility(View.GONE);
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

    public void measurementScreen(View view, String window, String pathFrom, String ewallWidth, String widthLeftWindow, String widthRightWindow, String ibLengthWindow, String ibWidthWindow, String lengthCeilFlr, String carpetInst, String pocketDepth, String windowApproval, String windowStatus, List<Allimage> allimages, String id) {
        btnSubmitWindowDetail.setVisibility(View.GONE);
        if (windowStatus.equals("Yes"))
        {
            isMeasurementDataSubmitted = true;
        }
        windowName = window;
        windowCompletionStatus = windowStatus;
        windowApprovalCheck = windowApproval;
        windowId = id;
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
            if (carpetInst.equals("Yes")) {
                spCarpetInst.setSelection(0);

            }
            else
            {

                spCarpetInst.setSelection(1);
            }

            for (int j=0; j < allimages.size(); j++)
            {

               imagesID =  allimages.get(j).getId();
               String url = allimages.get(j).getUrl();
                String[] separated = url.split("@__@");


                ImageList imageList = new ImageList();
                imageList.setimageUrl(ApiEndPoints.IMAGE_URL + allimages.get(j).getUrl());
                imageList.setImageId(imagesID);
                //for (int k=0; k<combineImageList.get(i).get)
                imageList.setimageType(separated[0]);
                combineImageList.add(imageList);
            }

        }




    }

    @OnClick(R.id.pictures)
    public void picturesScreen(View view) {
        if (windowId != null && isMeasurementDataSubmitted) {
            tvAppTitle.setText("Room Pictures");
            btnSubmitWindowDetail.setVisibility(View.VISIBLE);
            tvPictures.setTextColor(Color.parseColor("#ffffff")); // custom color
            llPictures.setBackgroundColor(Color.parseColor("#048700"));
            tvDetails.setTextColor(Color.parseColor("#252525")); // custom color
            llDetails.setBackgroundColor(Color.parseColor("#ffffff"));
            tvMeasurement.setTextColor(Color.parseColor("#252525")); // custom color
            llMeasurement.setBackgroundColor(Color.parseColor("#ffffff"));
            tvCeilToFloor.setBackgroundColor(Color.parseColor("#ffffff"));
            ceilToFloorButtonSelected();

            llOnDetailsClick.setVisibility(View.GONE);
            llOnMeasurementClick.setVisibility(View.GONE);
            llOnPicturesClick.setVisibility(View.VISIBLE);
        }
        else if(!isMeasurementDataSubmitted)
        {
            Toast.makeText(BLEInformationActivity.this,"Please Fill Measurement Details", Toast.LENGTH_SHORT).show();
        }

        else
        {
            Toast.makeText(BLEInformationActivity.this,"Please Select Window", Toast.LENGTH_SHORT).show();

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
        carpetInst = selectedCarpetInstValue;
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
                pocketDepth, carpetInst,windowCompletionStatus,windowApprovalCheck,windowId,PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<MeasurementResponse>() {

                @Override
                public void onResponse(Call<MeasurementResponse> call, retrofit2.Response<MeasurementResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                            btnSubmitWindowDetail.setVisibility(View.VISIBLE);
                            isMeasurementDataSubmitted = true;
                         /*
                            for (int i=0; i<response.body().getMeasurementDetails().size(); i++)
                            {
                                if (response.body().getSubmittedForReview().equals("1"))
                                {

                                    Log.e("abhi", "onResponse: .........................submitted for review " );
                                    LayoutInflater inflater = getLayoutInflater();
                                    View alertLayout = inflater.inflate(R.layout.layout_project_completion, null);
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(BLEInformationActivity.this);
                                    builder1.setView(alertLayout);
                                    builder1.setCancelable(true);

                                    builder1.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent intent = new Intent(BLEInformationActivity.this,NavigationActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                    dialog.cancel();
                                                }
                                            });


                                    AlertDialog alert = builder1.create();
                                    alert.show();
                                }
                                else
                                {

                                    Log.e("abhi", "onResponse:.............. Project not completed "  );


                                }
                                windowId = String.valueOf(response.body().getMeasurementDetails().get(i).getId());
                            }*/
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

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            saveMeasurementDataInDb();
            ComponentName componentName = new ComponentName(this, MyJobService.class);
            JobInfo jobInfo = null;
            JobScheduler jobScheduler = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                jobInfo = new JobInfo.Builder(parseInt(windowId), componentName)
                        .setRequiresCharging(false)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .build();
                jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
                assert jobScheduler != null;
                int resultCode = jobScheduler.schedule(jobInfo);
                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    Log.d("abhi", "Job scheduled!");
                } else {
                    Log.d("abhi", "Job not scheduled");
                }
            }


            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }

    }


    public static List<MeasurementDetailTable> getAll() {
        return new Select()
                .from(MeasurementDetailTable.class)
                .execute();
    }

    private void saveMeasurementDataInDb() {

        List<MeasurementDetailTable> measurementDetailTables = getAll();

        Log.e("abhi", "windowId:..................... "+ windowId );

        //Adding all the items of the inventories to arraylist
        for (int i = 0; i < measurementDetailTables.size(); i++) {
            if (windowId.equals(measurementDetailTables.get(i).windowId))
            {
               // Long windowIdToBeDeleted = Long.valueOf(measurementDetailTables.get(i).windowId);
                MeasurementDetailTable.delete(MeasurementDetailTable.class, measurementDetailTables.get(i).getId());
                Log.e("abhi", "saveMeasurementDataInDb: window id deleted.........."+measurementDetailTables.get(i).getId() );
            }

            Log.e("abhi", "saveMeasurementDataInDb: .................. window id list" +measurementDetailTables.get(i).windowId );

        }

        MeasurementDetailTable measurementDetailTable = new MeasurementDetailTable();
       // measurementDetailTable.m_id = windowId;
        measurementDetailTable.floorPlanId = floorPlanId;
        measurementDetailTable.roomId = roomId;
        measurementDetailTable.windowName = windowName;
        measurementDetailTable.wallWidth = wallWidth;

        measurementDetailTable.widthLeftWindow = widthLeftOfWindow;
        measurementDetailTable.ibWidthWindow = ibWidthOfWindow;
        measurementDetailTable.ibLengthWindow = ibLengthOfWindow;
        measurementDetailTable.widthRightWindow = widthRightOfWindow;
        measurementDetailTable.lengthCeilFlr = lengthCielFlr;

        measurementDetailTable.pocketDepth = pocketDepth;
        measurementDetailTable.carpetInst = carpetInst;
        measurementDetailTable.windowCompletionStatus = windowCompletionStatus;
        measurementDetailTable.windowApprovalCheck = windowApprovalCheck;
        measurementDetailTable.userId = PrefUtils.getUserId(BLEInformationActivity.this);
        measurementDetailTable.windowId = windowId;
        measurementDetailTable.save();


    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_information);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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
        spCarpetInst.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCarpetInstValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Spinner Drop down elements
        List<String> carpetInstOptions = new ArrayList<String>();
        carpetInstOptions.add("Yes");
        carpetInstOptions.add("No");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, carpetInstOptions);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spCarpetInst.setAdapter(dataAdapter);
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
        SubmitWindowDataAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.SubmitWindowsData.class, BASE_URL, this);

    }

    private void setWindowsList() {
        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<WindowsListResponse> call = WindowListAdapter.windowsListData(parseInt(floorPlanId), roomId);
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
                               /* for (int j=0; j<response.body().getWindowslist().get(i).getAllimages().size(); j++)
                                {

                                }*/
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
            LoadingDialog.cancelLoading();
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
        mExecutorService.shutdown();

        mExecutorService = null;
        imageCompressTask = null;

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

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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


            Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), imageUri, new String[]{MediaStore.Images.Media.DATA});

            if(cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //Create ImageCompressTask and execute with Executor.
                imageCompressTask = new ImageCompressTask(this, path, iImageCompressTaskListener);

                mExecutorService.execute(imageCompressTask);
            }


        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {



            Uri uri = data.getData();
            Cursor cursor = MediaStore.Images.Media.query(getContentResolver(), uri, new String[]{MediaStore.Images.Media.DATA});

            if(cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //Create ImageCompressTask and execute with Executor.
                imageCompressTask = new ImageCompressTask(this, path, iImageCompressTaskListener);

                mExecutorService.execute(imageCompressTask);
            }



            }




        }




    //image compress task callback
    private IImageCompressTaskListener iImageCompressTaskListener = new IImageCompressTaskListener() {
        @Override
        public void onComplete(List<File> compressed) {

            File file = compressed.get(0);

            Log.e("ImageCompressor", "New photo size ==> " + file.length()); //log new file size.
            sendImagesToServerFromCamera(file.getAbsolutePath());

        }

        @Override
        public void onError(Throwable error) {
            //very unlikely, but it might happen on a device with extremely low storage.
            //log it, log.WhatTheFuck?, or show a dialog asking the user to delete some files....etc, etc
            Log.wtf("ImageCompressor", "Error occurred", error);
        }
    };



    private void sendImagesToServerFromCamera(String imgString) {
        MultipartBody.Part fileToUpload = null;
        if (imgString != null) {
            File imgPath = new File(imgString);

            RequestBody mFile = RequestBody.create(MediaType.parse("image/jpg"), imgPath);
            fileToUpload = MultipartBody.Part.createFormData("file", imgPath.getName(), mFile);
        }
        RequestBody userId = RequestBody.create(
                MediaType.parse("text/plain"),
                PrefUtils.getUserId(this));

        RequestBody measurementId = RequestBody.create(
                MediaType.parse("text/plain"),
                windowId);

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
                            imageList.setImageId(response.body().getImageId());
                            imageList.setimageType(selectedImageType);
                            if (selectedImageType.equals("ceiltofloor")) {
                                for (int k = 0; k < combineImageList.size(); k++) {
                                    if (combineImageList.get(k).getimageType().equals("ceiltofloor")) {
                                        combineImageList.remove(k);
                                    }

                                }
                            }
                            else if (selectedImageType.equals("walltowall") ) {
                                for (int k = 0; k < combineImageList.size(); k++) {
                                    if (combineImageList.get(k).getimageType().equals("walltowall")) {
                                        combineImageList.remove(k);
                                    }

                                }
                            }
                            combineImageList.add(imageList);


                        }


                        combineImageListToBeShown = new ArrayList<>();
                        for (int i = combineImageList.size() - 1; i >= 0; i--) {
                            if (combineImageList.get(i).getimageType().equals(selectedImageType)) {
                                ImageList imageList = new ImageList();
                                imageList.setimageUrl(combineImageList.get(i).getimageUrl());
                                imageList.setImageId(combineImageList.get(i).getImageId());
                                imageList.setimageType(selectedImageType);
                                combineImageListToBeShown.add(imageList);
                            }
                        }

                        mRecyclerViewImages.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(BLEInformationActivity.this, LinearLayoutManager.HORIZONTAL, false);
                        mRecyclerViewImages.setLayoutManager(mLayoutManager);

                        mAdapter = new HLVImagesAdapter(BLEInformationActivity.this, combineImageListToBeShown, selectedImageType,windowId);
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
            LoadingDialog.cancelLoading();
        }
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

    public void updateCombinedImageListFromAdapter(String imageId)
    {
        for (int i = combineImageList.size() - 1; i >= 0; i--) {
            if (combineImageList.get(i).getImageId().equals(imageId)) {
                combineImageList.remove(i);
            }
        }
    }


}