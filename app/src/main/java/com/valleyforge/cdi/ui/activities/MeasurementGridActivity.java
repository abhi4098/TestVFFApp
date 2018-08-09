package com.valleyforge.cdi.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
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



    @BindView(R.id.listview)
    ListView listview;



    public static ArrayList<Floorslist> alFloorList;
    FloorListAdapter floorListAdapter;

    private RetrofitInterface.UserFloorDetailsClient UserFloorDetailAdapter;

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
        tvAppTitle.setText("Measurement Grid");
        setUpRestAdapter();
        setFloorDetails();


    }


    private void setUpRestAdapter() {
        UserFloorDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserFloorDetailsClient.class, BASE_URL, this);

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
                           for (int i=0; i<response.body().getFloorslist().size(); i++) {


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
        }
    }
    @Override
    public void onClick(View v) {
        super.onBackPressed();
    }
}
