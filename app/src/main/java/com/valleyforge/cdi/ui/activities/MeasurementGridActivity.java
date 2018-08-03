package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.ui.adapters.HLVAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeasurementGridActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.parent_layout)
    LinearLayout llParentLayout;
    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;


    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.card_view_child)
    LinearLayout llChildLayout;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> alName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_grid);
        ButterKnife.bind(this);
        alName = new ArrayList<>(Arrays.asList("Room No-101", "Room No-102", "Room No-103", "Room No-104", "Room No-105", "Room No-106", "Room No-107", "Room No-108"));

        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvAppTitle.setText("Measurement Grid");

        for (int i=0; i<5; i++) {

            View dynamicChildLayout = getLayoutInflater().inflate(R.layout.layout_dyanmically_generated, llChildLayout, false);
            llParentLayout.addView(dynamicChildLayout);

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new HLVAdapter(this, alName);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}
