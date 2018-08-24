package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.Pdetail;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.model.ProjectDetailResponse;
import com.valleyforge.cdi.generated.model.ProjectListResponse;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class ProjectDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RetrofitInterface.UserProjectDetailClient UserProjectDetailAdapter;
    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;
    Context mContext;

    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.property_name)
    TextView tvPropertyName;
    @BindView(R.id.on_site_poc_contact)
    TextView tvOnSitePocContact;
    @BindView(R.id.date_of_appointment)
    TextView tvDateOfAppointment;
    @BindView(R.id.project_pm)
    TextView tvProjectPm;
    @BindView(R.id.on_site_poc)
    TextView tvOnSitePOC;
    @BindView(R.id.start_date)
    TextView tvStartDate;
    @BindView(R.id.end_date)
    TextView tvEndDate;
    @BindView(R.id.location_address)
    TextView tvLocationAddress;
    @BindView(R.id.other_details)
    TextView tvOtherDetails;

    @BindView(R.id.begin_project_button)
    Button btnbeginProject;

    @BindView(R.id.ll_on_site_poc)
    LinearLayout llOnSitePoc;

  /*  @OnClick(R.id.ll_on_site_poc)
    public void onSitePOCDetails()
       {
           PopupMenu menu = new PopupMenu(this,);
           menu.getMenu().add("AGIL");
           menu.getMenu().add("AGILarasan");
           menu.getMenu().add("Arasan");
           menu.show();
       }*/


    @BindView(R.id.status)
    TextView tvGoToDashboard;

    String projectId ;

    ArrayList<Pdetail> projectDetail = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        ButterKnife.bind(this);

        mContext = ProjectDetailActivity.this;
        /*tvAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this ,NavigationActivity.class);
                startActivity(intent);
                finish();

            }
        });*/
        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvGoToDashboard.setVisibility(View.VISIBLE);
        tvGoToDashboard.setText("Dashboard");
        tvGoToDashboard.setTextColor(Color.parseColor("#252525"));
        tvGoToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectDetailActivity.this,NavigationActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvAppTitle.setText("Project Detail");
        btnbeginProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProjectDetailActivity.this, SearchDevicesActivity.class);
                startActivity(i);
            }
        });
      /*  llOnSitePoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    menu = new PopupMenu(ProjectDetailActivity.this,v, Gravity.RIGHT);
                }
                menu.getMenu().add("AGIL");
                menu.getMenu().add("AGILarasan");
                menu.getMenu().add("Arasan");
                menu.show();
            }
        });*/
        projectId =getIntent().getStringExtra("PROJECT_ID");
        setUpRestAdapter();
        getProjectDetail();

    }

    private void setUpRestAdapter() {
        UserProjectDetailAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProjectDetailClient.class, BASE_URL, this);

    }

    private void getProjectDetail() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Log.e("abhi", "getProjectDetail:................. " +projectId);
        Call<ProjectDetailResponse> call = UserProjectDetailAdapter.userProjectDetailData(projectId);
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<ProjectDetailResponse>() {

                @Override
                public void onResponse(Call<ProjectDetailResponse> call, Response<ProjectDetailResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                           setProjectDetail(response);
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<ProjectDetailResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }

    private void setProjectDetail(Response<ProjectDetailResponse> response) {

        projectDetail = new ArrayList<>();
        for (int i = response.body().getPdetail().size() - 1; i >= 0; i--) {
            Pdetail pDetail = new Pdetail();

            pDetail.setId(response.body().getPdetail().get(i).getId());
            pDetail.setProjectId(response.body().getPdetail().get(i).getProjectId());
            pDetail.setProjectManagerName(response.body().getPdetail().get(i).getProjectManagerName());
            pDetail.setProjectAssignedPm(response.body().getPdetail().get(i).getProjectAssignedPm());

            pDetail.setProjectStatus(response.body().getPdetail().get(i).getProjectStatus());
            pDetail.setProjectStartDate(response.body().getPdetail().get(i).getProjectStartDate());
            pDetail.setProjectEndDate(response.body().getPdetail().get(i).getProjectEndDate());
            pDetail.setPropertiesAddress(response.body().getPdetail().get(i).getPropertiesAddress());

            pDetail.setPropertiesAssignedTo(response.body().getPdetail().get(i).getPropertiesAssignedTo());
            pDetail.setPropertiesContactPersonName(response.body().getPdetail().get(i).getPropertiesContactPersonName());
            pDetail.setPropertiesContactPersonPhone(response.body().getPdetail().get(i).getPropertiesContactPersonPhone());
            pDetail.setPropertiesDescription(response.body().getPdetail().get(i).getPropertiesDescription());

            pDetail.setPropertiesEmail(response.body().getPdetail().get(i).getPropertiesEmail());
            pDetail.setPropertiesName(response.body().getPdetail().get(i).getPropertiesName());
           /* pDetail.setA(response.body().getPdetail().get(i).getProjectManagerName());
            pDetail.setProjectAssignedPm(response.body().getPdetail().get(i).getProjectAssignedPm());*/

           PrefUtils.storeProjectId(response.body().getPdetail().get(i).getId(),this);
            Log.e("abhi", "setProjectDetail:............................... "  +response.body().getPdetail().get(i).getAppointmentDate());

            tvPropertyName.setText(response.body().getPdetail().get(i).getPropertiesName());

           tvDateOfAppointment.setText(response.body().getPdetail().get(i).getAppointmentDate());

            tvProjectPm.setText(response.body().getPdetail().get(i).getProjectManagerName());

            tvOnSitePOC.setText(response.body().getPdetail().get(i).getPropertiesContactPersonName());

            tvStartDate.setText(response.body().getPdetail().get(i).getProjectStartDate());
            tvOnSitePocContact.setText(response.body().getPdetail().get(i).getPropertiesContactPersonPhone());

            tvEndDate.setText(response.body().getPdetail().get(i).getProjectEndDate());

            tvLocationAddress.setText(response.body().getPdetail().get(i).getPropertiesAddress());

            tvOtherDetails.setText(response.body().getPdetail().get(i).getPropertiesDescription());

            if (!response.body().getPdetail().get(i).getProjectStatus().equals("Completed"))
            {
                btnbeginProject.setVisibility(View.VISIBLE);
            }


            projectDetail.add(pDetail);

        }


    }



    @Override
    public void onClick(View v) {
                super.onBackPressed();
    }
}
