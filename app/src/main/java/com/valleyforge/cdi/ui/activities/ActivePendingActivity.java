package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.ForgotPasswordResponse;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.model.ProjectListResponse;
import com.valleyforge.cdi.generated.tables.PListTable;
import com.valleyforge.cdi.ui.adapters.ProjectListAdapter;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class ActivePendingActivity extends AppCompatActivity implements View.OnClickListener{
    private RetrofitInterface.UserProjectListClient UserProjectListAdapter;

    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;
    Context mContext;


    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.active_project_header)
    TextView tvActiveProjectHeader;

    @BindView(R.id.status)
    TextView tvGoToDashboard;

    @BindView(R.id.pending_project_header)
    TextView tvPendingProjectHeader;

    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.active_projects_button)
    LinearLayout btnActiveProjects;

    @BindView(R.id.pending_projects_button)
    LinearLayout btnPendingProjects;

    String projectStatus = "active";

    ArrayList<PListTable> showProjectList = null;
    ArrayList<PListTable> projectListTable;
    ProjectListAdapter projectListAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_pending);
        ButterKnife.bind(this);
        mContext = ActivePendingActivity.this;
        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvGoToDashboard.setVisibility(View.VISIBLE);
        tvGoToDashboard.setText("Dashboard");
        tvGoToDashboard.setTextColor(Color.parseColor("#252525"));
        tvGoToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivePendingActivity.this,NavigationActivity.class);
                        startActivity(intent);
                        finish();
            }
        });
        tvAppTitle.setText(R.string.active_pending_text_header);

        btnActiveProjects.setOnClickListener(this);
        btnPendingProjects.setOnClickListener(this);

        setUpRestAdapter();
        getProjectsList();

    }

    private void setUpRestAdapter() {
        UserProjectListAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProjectListClient.class, BASE_URL, this);

    }

    private void getProjectsList() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<ProjectListResponse> call = UserProjectListAdapter.userProjectListData(PrefUtils.getUserId(this));
        if (NetworkUtils.isNetworkConnected(this)) {
            call.enqueue(new Callback<ProjectListResponse>() {

                @Override
                public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                            setProjectList(response);
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<ProjectListResponse> call, Throwable t) {


                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            showProjectList();
            SnakBarUtils.networkConnected(this);
            LoadingDialog.cancelLoading();
        }
    }


    public static List<PListTable> getAll() {
        return new Select()
                .from(PListTable.class)
                .execute();
    }


    public void showProjectList()
    {
        List<PListTable> pListTables = getAll();
        projectListTable = new ArrayList<>();
        showProjectList = new ArrayList<>();
        //Adding all the items of the inventories to arraylist
        for (int i = 0; i < pListTables.size(); i++) {
            PListTable pListTable = new PListTable();
            pListTable.p_id = pListTables.get(i).p_id;
            pListTable.pname = pListTables.get(i).pname;
            pListTable.projectId = pListTables.get(i).projectId;
            pListTable.projectPercentage = pListTables.get(i).projectPercentage;
            pListTable.projectStatus = pListTables.get(i).projectStatus;

            projectListTable.add(pListTable);
            int projectpercentage = Integer.parseInt(pListTables.get(i).projectPercentage);

            if (projectStatus.equals("active")) {
                if (projectpercentage > 0 && !pListTables.get(i).projectStatus.equals("Completed") ) {
                    showProjectList.add(pListTable);
                }
            }
            else
            {
                if (projectpercentage == 0) {
                    showProjectList.add(pListTable);
                }
            }
        }

        sendToAdapter();
    }


    public void sendToAdapter()
    {

        projectListAdapter = new ProjectListAdapter(this, R.layout.layout_project_list_item, R.id.active_pending_cardView, showProjectList);
        listview.setAdapter(projectListAdapter);
        LoadingDialog.cancelLoading();
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);

    }

    private void setProjectList(Response<ProjectListResponse> response) {

        new Delete().from(PListTable.class).execute();
        projectListTable = new ArrayList<>();
        showProjectList = new ArrayList<>();

        for (int i = response.body().getPlist().size() - 1; i >= 0; i--) {
            PListTable pListTable = new PListTable();
            pListTable.pname = response.body().getPlist().get(i).getPname();
            pListTable.p_id = response.body().getPlist().get(i).getId();
            pListTable.projectId = response.body().getPlist().get(i).getProjectId();
            pListTable.projectPercentage = response.body().getPlist().get(i).getProjectPercentage();
            pListTable.projectStatus = response.body().getPlist().get(i).getProjectStatus();

            pListTable.save();
            projectListTable.add(pListTable);

            int projectpercentage = Integer.parseInt(response.body().getPlist().get(i).getProjectPercentage());

            if (projectStatus.equals("active")) {
                if (projectpercentage > 0 && !response.body().getPlist().get(i).getProjectStatus().equals("Completed")) {
                    showProjectList.add(pListTable);
                }
            }
            else
            {
                if (projectpercentage == 0) {
                    showProjectList.add(pListTable);
                }
            }




        }

       sendToAdapter();

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.active_projects_button:
                tvActiveProjectHeader.setTextColor(Color.parseColor("#ffffff")); // custom color
                btnActiveProjects.setBackgroundColor(Color.parseColor("#048700"));
                tvPendingProjectHeader.setTextColor(Color.parseColor("#252525")); // custom color
                btnPendingProjects.setBackgroundColor(Color.parseColor("#ffffff"));


                projectStatus = "active";
                getProjectsList();
                break;

            case R.id.pending_projects_button:
                tvPendingProjectHeader.setTextColor(Color.parseColor("#ffffff")); // custom color
                btnPendingProjects.setBackgroundColor(Color.parseColor("#048700"));
                tvActiveProjectHeader.setTextColor(Color.parseColor("#252525")); // custom color
                btnActiveProjects.setBackgroundColor(Color.parseColor("#ffffff"));

                projectStatus = "pending";
                getProjectsList();

                break;

            case R.id.back_icon:

                super.onBackPressed();
                break;

            default:
                break;
        }

    }

}
