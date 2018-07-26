package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.utils.PrefUtils;


public class SplashActivity extends AppCompatActivity {

    Context mContext;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_splash);
        mContext = SplashActivity.this;
        calltoSplash();
    }


    public void calltoSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CheckLogin();
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void CheckLogin() {
        Boolean isLoggedIn = PrefUtils.getUserLoggedIn(this);
        if (isLoggedIn) {
            Intent i = new Intent(SplashActivity.this, NavigationActivity.class);
            i.putExtra("LOGIN_TYPE", PrefUtils.getUserType(SplashActivity.this));
            startActivity(i);
        } else {
            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(i);
       }
    }

    }


