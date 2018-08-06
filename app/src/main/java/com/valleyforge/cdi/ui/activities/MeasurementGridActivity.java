package com.valleyforge.cdi.ui.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.ui.adapters.HLVAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MeasurementGridActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.parent_layout)
    LinearLayout llParentLayout;
    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;

    CardView cvAddRoom;

    @BindView(R.id.logout)
    ImageView ivLogout;
    View dynamicChildLayout;

    @BindView(R.id.card_view_child)
    LinearLayout llChildLayout;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    ArrayList<String> alName;


    public  void addRoom(View v)
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final Button addRoomBtn = alertLayout.findViewById(R.id.add_room_btn);

       /* final EditText etUsername = alertLayout.findViewById(R.id.et_username);
        final EditText etEmail = alertLayout.findViewById(R.id.et_email);
        final CheckBox cbToggle = alertLayout.findViewById(R.id.cb_show_pass);*/

       /* cbToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // to encode password in dots
                    etEmail.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // to display the password in normal text
                    etEmail.setTransformationMethod(null);
                }
            }
        });*/

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        /*alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String user = etUsername.getText().toString();
               // String pass = etEmail.getText().toString();
               // Toast.makeText(getBaseContext(), "Username: " + user + " Email: " + pass, Toast.LENGTH_SHORT).show();
            }
        });*/
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(1000, 500);
        addRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   dialog.cancel();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_grid);
        ButterKnife.bind(this);

        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvAppTitle.setText("Measurement Grid");

        for (int i=0; i<5; i++) {
            alName = new ArrayList<>(Arrays.asList("Room No-101", "Room No-102", "Room No-103", "Room No-104", "Room No-105", "Room No-106", "Room No-107", "Room No-108"));

            dynamicChildLayout = getLayoutInflater().inflate(R.layout.layout_dyanmically_generated, llChildLayout, false);
            cvAddRoom = (CardView) dynamicChildLayout.findViewById(R.id.add_room_cardview);
            dynamicChildLayout.setId(Integer.parseInt(String.valueOf(i)));
            llParentLayout.addView(dynamicChildLayout);
            final int id_ = cvAddRoom.getId();

            cvAddRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MeasurementGridActivity.this, "Button with id =" + id_ +
                            " is clicked",Toast.LENGTH_SHORT).show();
                        cvAddRoom.setCardBackgroundColor(Color.parseColor("#252525"));


                    addRoom(v);
                }
            });



            mRecyclerView = (RecyclerView)dynamicChildLayout.findViewById(R.id.recycler_view);
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
