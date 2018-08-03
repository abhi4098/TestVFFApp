package com.valleyforge.cdi.ui.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.model.ProjectListResponse;
import com.valleyforge.cdi.ui.adapters.CompletedProjectListAdapter;
import com.valleyforge.cdi.ui.adapters.ProjectListAdapter;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

public class CompletedProjectsActivity extends AppCompatActivity  implements View.OnClickListener{
    private RetrofitInterface.UserProjectListClient UserProjectListAdapter;
    @BindView(R.id.back_icon)
    ImageView ivBackIcon;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_app_title)
    TextView tvAppTitle;
    Context mContext;

    @BindView(R.id.logout)
    ImageView ivLogout;
    String projectStatus = "Completed";

    ArrayList<Plist> projectList = null;
    CompletedProjectListAdapter projectListAdapter;

    @BindView(R.id.listview)
    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_projects);
        ButterKnife.bind(this);
        mContext = CompletedProjectsActivity.this;
        ivBackIcon.setOnClickListener(this);
        ivLogout.setVisibility(View.GONE);
        tvAppTitle.setText("Completed Projects");
        setUpRestAdapter();
        getProjectsList();
    }


    private void setUpRestAdapter() {
        UserProjectListAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProjectListClient.class, BASE_URL, this);

    }

    private void getProjectsList() {
        LoadingDialog.showLoadingDialog(this,"Loading...");
        Call<ProjectListResponse> call = UserProjectListAdapter.userProjectListData(PrefUtils.getUserId(this),projectStatus);
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
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            SnakBarUtils.networkConnected(this);
        }
    }

    private void setProjectList(Response<ProjectListResponse> response) {

        projectList = new ArrayList<>();
        for (int i = response.body().getPlist().size() - 1; i >= 0; i--) {
            Plist plist = new Plist();

            plist.setPname(response.body().getPlist().get(i).getPname());
            plist.setProjectId(response.body().getPlist().get(i).getProjectId());
            plist.setProjectPercentage(response.body().getPlist().get(i).getProjectPercentage());
            plist.setId(response.body().getPlist().get(i).getId());
            plist.setAssignPm(response.body().getPlist().get(i).getAssignPm());
            projectList.add(plist);
        }

        projectListAdapter = new CompletedProjectListAdapter(this, R.layout.layout_completed_project_list_item, R.id.active_pending_cardView, projectList);
        listview.setAdapter(projectListAdapter);
        LoadingDialog.cancelLoading();
        //listview.setDivider(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);

    }


    @Override
    public void onClick(View v) {
       super.onBackPressed();
    }
}
