package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.generated.model.ProjectDetailResponse;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private RetrofitInterface.UserProfileDetailsClient UserProfileDetailAdapter;

    private RetrofitInterface.UpdateProfileDetailsClient UpdateProfileDetailAdapter;

    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;


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

    String userName,userPhone,userAdd,userEmail;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mContext = ProfileActivity.this;
        ivBackIcon.setOnClickListener(this);
        tvAppTitle.setText("Profile");
        ivLogout.setVisibility(View.GONE);



        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //etUsername.setFocusableInTouchMode(true);
                etuserEmail.setFocusableInTouchMode(true);
                etUserAdd.setFocusableInTouchMode(true);
                etuserPhone.setFocusableInTouchMode(true);
                btnEditProfile.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);

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
                           /* for (int i=0; i<response.body().getData().size(); i++) {
                                Log.e("abhi.........      ", "onResponse: "+response.body().getData().get(i).getName() );
                                etUsername.setText(response.body().getData().get(i).getName());
                                etuserEmail.setText(response.body().getData().get(i).getEmail());
                                etUserAdd.setText(response.body().getData().get(i).getAddress());
                                etuserPhone.setText(response.body().getData().get(i).getPhone());
                            }
*/
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
        }
    }

    private void setUpRestAdapter() {
        UserProfileDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProfileDetailsClient.class, BASE_URL, this);
        UpdateProfileDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UpdateProfileDetailsClient.class, BASE_URL, this);

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
        }
    }


    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}
