package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
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





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mContext = ProfileActivity.this;
        ivBackIcon.setOnClickListener(this);
        tvAppTitle.setText("Profile");
        ivLogout.setVisibility(View.GONE);

       // etUsername.setEnabled(false);
        etUsername.setFocusable(false);
        etUsername.setText(PrefUtils.getUserName(this));

       // etuserEmail.setEnabled(false);
        etuserEmail.setFocusable(false);
        etuserEmail.setText(PrefUtils.getEmail(this));

       // etUserAdd.setEnabled(false);
        etUserAdd.setFocusable(false);
        etUserAdd.setText(PrefUtils.getUserAdd(this));

       // etuserPhone.setEnabled(false);
        etuserPhone.setFocusable(false);
        etuserPhone.setText(PrefUtils.getUserPhone(this));
        //etUsername.setText(PrefUtils.getUserName(this));
        //etUsername.setText(PrefUtils.getUserName(this));

    }

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}
