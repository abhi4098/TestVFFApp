package com.valleyforge.cdi.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.AddFloorResponse;
import com.valleyforge.cdi.generated.model.DynamicallyAddedView;
import com.valleyforge.cdi.generated.model.FloorDetailsResponse;
import com.valleyforge.cdi.generated.model.Floorslist;
import com.valleyforge.cdi.generated.model.Room;
import com.valleyforge.cdi.ui.adapters.FloorListAdapter;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class MeasurementGridActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;


    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.status)
    TextView tvGoToDashboard;

    @BindView(R.id.add_floor_button)
    Button btnAddFloor;
    
    @BindView(R.id.listview)
    ListView listview;



    public static ArrayList<Floorslist> alFloorList;
    FloorListAdapter floorListAdapter;

    private RetrofitInterface.UserFloorDetailsClient UserFloorDetailAdapter;
    private RetrofitInterface.AddFloorClient AddFloorAdapter;
    String floorName;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("abhi", "onResume: ................................." );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_grid);
        ButterKnife.bind(this);
        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvGoToDashboard.setVisibility(View.VISIBLE);
        btnAddFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFloor(v);

            }
        });
        tvGoToDashboard.setText("Dashboard");
        tvGoToDashboard.setTextColor(Color.parseColor("#252525"));
        tvGoToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeasurementGridActivity.this,NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvAppTitle.setText("Measurement Grid");
        setUpRestAdapter();
        setFloorDetails();


    }

    private void addFloor(View v) {


        LayoutInflater inflater = this.getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_add_floor, null);
        final EditText etFloorName = alertLayout.findViewById(R.id.floor_name);
        final Button btnAddFloor = alertLayout.findViewById(R.id.add_floor_btn);



        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
               /* Toast.makeText(context, "Cancel clicked", Toast.LENGTH_SHORT).show();
                holder.cvAddRoom.setCardBackgroundColor(Color.parseColor("#048700"));*/

            }
        });


        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getWindow().setLayout(700, 320);
        btnAddFloor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floorName = etFloorName.getText().toString();

                if (floorName == null || floorName.equals("")) {
                    etFloorName.setError("Please Add Floor Name");

                }
                else
                {
                    addFloorApiCall();
                    dialog.cancel();

                }

            }
        });

    }


    private void setUpRestAdapter() {
        UserFloorDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserFloorDetailsClient.class, BASE_URL, this);
        AddFloorAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.AddFloorClient.class, BASE_URL, this);


    }

    private void addFloorApiCall() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<AddFloorResponse> call = AddFloorAdapter.addFloorData(floorName,PrefUtils.getProjectId(this),PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<AddFloorResponse>() {

                @Override
                public void onResponse(Call<AddFloorResponse> call, Response<AddFloorResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType().equals(1)) {
                            Log.e("abhi", "onResponse:  add floor response................." );
                            setFloorDetails();
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<AddFloorResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }

    private void setFloorDetails() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<FloorDetailsResponse> call = UserFloorDetailAdapter.userFloorDetailData(PrefUtils.getProjectId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<FloorDetailsResponse>() {

                @Override
                public void onResponse(Call<FloorDetailsResponse> call, Response<FloorDetailsResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType().equals("1")) {
                            Log.e("abhi", "onResponse: ....................."+response.body().getFloorslist().size() );
                            alFloorList = new ArrayList<>();
                           for (int i=response.body().getFloorslist().size()-1; i>=0; i--) {


                               Floorslist  floorslist  = new Floorslist();
                               floorslist.setId(response.body().getFloorslist().get(i).getId());
                               floorslist.setFloor(response.body().getFloorslist().get(i).getFloor());
                               floorslist.setRoomscount(response.body().getFloorslist().get(i).getRoomscount());
                               floorslist.setCompletedroomscount(response.body().getFloorslist().get(i).getCompletedroomscount());

                               alFloorList.add(floorslist);



                           }

                            floorListAdapter = new FloorListAdapter(MeasurementGridActivity.this, R.layout.layout_dyanmically_generated, R.id.floor_name, alFloorList);
                            listview.setAdapter(floorListAdapter);
                            //listview.setDivider(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
                            listview.setDividerHeight(1);
                            listview.setTextFilterEnabled(true);
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<FloorDetailsResponse> call, Throwable t) {
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
