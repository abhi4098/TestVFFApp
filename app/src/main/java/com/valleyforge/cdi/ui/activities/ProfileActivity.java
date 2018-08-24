package com.valleyforge.cdi.ui.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.ApiEndPoints;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ImageList;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.generated.model.ProjectDetailResponse;
import com.valleyforge.cdi.generated.model.UploadPhotoResponse;
import com.valleyforge.cdi.ui.adapters.HLVImagesAdapter;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private RetrofitInterface.UserProfileDetailsClient UserProfileDetailAdapter;
    private RetrofitInterface.uploadProfilePhotoClient UploadProfilePhotoAdapter;
    private RetrofitInterface.UpdateProfileDetailsClient UpdateProfileDetailAdapter;

    private int PICK_FROM_GALLERY = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private final String[] requiredPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;

    @BindView(R.id.progress)
    ProgressBar imageProgressBar;


    @BindView(R.id.logout)
    ImageView ivLogout;

    Context mContext;

    @BindView(R.id.username)
    EditText etUsername;

    @BindView(R.id.email)
    EditText etuserEmail;

    @BindView(R.id.phone)
    EditText etuserPhone;

    @BindView(R.id.user_address)
    EditText etUserAdd;

    @BindView(R.id.profile_edit_button)
    Button btnEditProfile;


    @BindView(R.id.submit_button)
    Button btnSubmit;

    @BindView(R.id.upload_pic)
    de.hdodenhof.circleimageview.CircleImageView btnUploadPhoto;

    @BindView(R.id.person_image)
    de.hdodenhof.circleimageview.CircleImageView personImage;


    String userName,userPhone,userAdd,userEmail;
    String imgDecodableString;

    @BindView(R.id.status)
    TextView tvGoToDashboard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mContext = ProfileActivity.this;
        ivBackIcon.setOnClickListener(this);
        tvAppTitle.setText("Profile");
        ivLogout.setVisibility(View.GONE);

        tvGoToDashboard.setVisibility(View.VISIBLE);
        tvGoToDashboard.setText("Dashboard");
        tvGoToDashboard.setTextColor(Color.parseColor("#252525"));
        tvGoToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Checkpermission();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadingDialog.showLoadingDialog(ProfileActivity.this,"Loading...");
                //etUsername.setFocusableInTouchMode(true);
                etuserEmail.setFocusableInTouchMode(true);
                etUserAdd.setFocusableInTouchMode(true);
                etuserPhone.setFocusableInTouchMode(true);
                btnEditProfile.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                LoadingDialog.cancelLoading();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = etUsername.getText().toString();
                userEmail = etuserEmail.getText().toString();
                userAdd = etUserAdd.getText().toString();
                userPhone = etuserPhone.getText().toString();

                Log.e("abhi", "onClick:............... " + userName + " " + userPhone + " " +userAdd + " " +userEmail  );
                submitUpdatedDetails();

            }
        });

        setUpRestAdapter();
        setProfileDetails();
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
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }




    private void setProfilePicURL(String profilepicUrlComplete) {
        Glide.with(this).load(profilepicUrlComplete).asBitmap().centerCrop().dontAnimate().dontTransform().listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                imageProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                imageProgressBar.setVisibility(View.GONE);
                return false;
            }
        })
                .into(new BitmapImageViewTarget(personImage) {
                    @Override
                    protected void setResource(Bitmap bitmap) {
                        Bitmap output;

                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                        } else {
                            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
                        }

                        Canvas canvas = new Canvas(output);

                        final int color = 0xff424242;
                        final Paint paint = new Paint();
                        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

                        float r = 0;

                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            r = bitmap.getHeight() / 2;
                        } else {
                            r = bitmap.getWidth() / 2;
                        }

                        paint.setAntiAlias(true);
                        canvas.drawARGB(0, 0, 0, 0);
                        paint.setColor(color);
                        canvas.drawCircle(r, r, r, paint);
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        canvas.drawBitmap(bitmap, rect, rect, paint);
                        Log.e("abhi", "setResource: -----------output"+output );
                        personImage.setImageBitmap(output);
                        imageProgressBar.setVisibility(View.GONE);


                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 0 && resultCode == RESULT_OK) {


            Bitmap bp = (Bitmap) data.getExtras().get("data");

            if (bp !=null) {
                personImage.setImageBitmap(getCircularBitmap(bp));
                Uri tempUri = getImageUri(getApplicationContext(), bp);
                File filePath = new File(getRealPathFromURI(tempUri));
                imageProgressBar.setVisibility(View.VISIBLE);
               // setProfilePicURL(filePath.getPath());
                sendImagesToServerFromCamera(filePath.getPath());
            }


        } else if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {

           personImage.setBackgroundResource(R.drawable.profile_background);


            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                setProfilePicURL(imgDecodableString);
               sendImagesToServerFromCamera(imgDecodableString);
            }

            Log.e("abhi", "onActivityResult: image decodable "+imgDecodableString );
            imageProgressBar.setVisibility(View.VISIBLE);
            Log.e("abhi", "onActivityResult:.......... " +imgDecodableString );



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
                PrefUtils.getUserId(this));

        RequestBody userid = RequestBody.create(
                MediaType.parse("text/plain"),
                PrefUtils.getUserId(this));



        LoadingDialog.showLoadingDialog(this, "Loading...");
        Call<UploadPhotoResponse> call = UploadProfilePhotoAdapter.uploadProfileData(fileToUpload, userid);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<UploadPhotoResponse>() {

                @Override
                public void onResponse(Call<UploadPhotoResponse> call, retrofit2.Response<UploadPhotoResponse> response) {

                    if (response.isSuccessful()) {
                        if (response.body().getType() == 1) {
                             String profileUrl = response.body().getImageurl();
                            Log.e("abhi", "onResponse: ....................." +profileUrl );
                            if (profileUrl !=null) {
                                setProfilePicURL(profileUrl);
                            }
                            else
                            {
                                personImage.setBackground(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.profile_background));
                            }

                        }

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


    private void submitUpdatedDetails() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<LoginResponse> call = UpdateProfileDetailAdapter.updateProfileDetailData(PrefUtils.getUserId(this),PrefUtils.getContextId(this),userName,userEmail,userPhone,userAdd);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                            finish();

                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }

    private void setUpRestAdapter() {
        UserProfileDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProfileDetailsClient.class, BASE_URL, this);
        UpdateProfileDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UpdateProfileDetailsClient.class, BASE_URL, this);
        UploadProfilePhotoAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.uploadProfilePhotoClient.class, BASE_URL, this);

    }

    private void setProfileDetails() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<LoginResponse> call = UserProfileDetailAdapter.userProfileDetailData(PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<LoginResponse>() {

                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                            for (int i=0; i<response.body().getData().size(); i++) {
                                Log.e("abhi.........      ", "onResponse: "+response.body().getData().get(i).getName() );
                                etUsername.setText(response.body().getData().get(i).getName());
                                etuserEmail.setText(response.body().getData().get(i).getEmail());
                                etUserAdd.setText(response.body().getData().get(i).getAddress());
                                etuserPhone.setText(response.body().getData().get(i).getPhone());
                                Log.e("abhi", "onResponse:.......................image url " + response.body().getData().get(i).getProfileImageUrl());
                                setProfilePicURL(response.body().getData().get(i).getProfileImageUrl());
                            }

                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }


    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}
