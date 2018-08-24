package com.valleyforge.cdi.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ForgotPasswordResponse;
import com.valleyforge.cdi.generated.model.LoginResponse;
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

public class ForgotPasswordActivity extends AppCompatActivity {
    private RetrofitInterface.UserForgotPasswordClient UserForgotPasswordAdapter;

    String useremail ;
    @BindView(R.id.forgot_password_button)
    Button btnForgotPassword;

    @BindView(R.id.email)
    AutoCompleteTextView actvForgotPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);


        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail = actvForgotPassword.getText().toString();
                Log.e("abhi", "onCreate: .........." +useremail );
                if(useremail.equals(""))
                {
                    Toast.makeText(getApplicationContext(),"Enter Email",Toast.LENGTH_SHORT).show();

                }
                else if(!isEmailValid(useremail))
                {
                    Toast.makeText(getApplicationContext(),"Email is not valid",Toast.LENGTH_SHORT).show();
                }
                else {
                    setUpRestAdapter();
                    getForgotPasswordLink();
                }
            }
        });
    }

    private void setUpRestAdapter() {
        UserForgotPasswordAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserForgotPasswordClient.class, BASE_URL, this);

    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }
    private void getForgotPasswordLink() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<ForgotPasswordResponse> call = UserForgotPasswordAdapter.userForgotPasswordData(useremail);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<ForgotPasswordResponse>() {

                @Override
                public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {

                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }
}
