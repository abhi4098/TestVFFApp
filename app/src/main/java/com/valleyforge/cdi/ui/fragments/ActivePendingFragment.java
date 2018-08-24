package com.valleyforge.cdi.ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.Plist;
import com.valleyforge.cdi.generated.model.ProjectListResponse;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ActivePendingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ActivePendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivePendingFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RetrofitInterface.UserProjectListClient UserProjectListAdapter;

    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.active_project_header)
    TextView tvActiveProjectHeader;

    @BindView(R.id.pending_project_header)
    TextView tvPendingProjectHeader;

    @BindView(R.id.logout)
    ImageView ivLogout;

    @BindView(R.id.active_projects_button)
    LinearLayout btnActiveProjects;

    @BindView(R.id.pending_projects_button)
    LinearLayout btnPendingProjects;

    String projectStatus = "In Progress";

    ArrayList<Plist> projectList = null;
    ProjectListAdapter projectListAdapter;




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ActivePendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivePendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivePendingFragment newInstance(String param1, String param2) {
        ActivePendingFragment fragment = new ActivePendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_active_pending, container, false);
        ButterKnife.bind(this,rootView);


        btnActiveProjects.setOnClickListener(this);
        btnPendingProjects.setOnClickListener(this);

        setUpRestAdapter();
        getProjectsList();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void setUpRestAdapter() {
        UserProjectListAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserProjectListClient.class, BASE_URL, getActivity());

    }

    private void getProjectsList() {
        LoadingDialog.showLoadingDialog(getActivity(),"Loading...");
        Call<ProjectListResponse> call = UserProjectListAdapter.userProjectListData(PrefUtils.getUserId(getActivity()),projectStatus);
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<ProjectListResponse>() {

                @Override
                public void onResponse(Call<ProjectListResponse> call, Response<ProjectListResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getType() == 1) {
                            setProjectList(response);
                            Toast.makeText(getActivity(),response.body().getMsg(),Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(getActivity(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
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
            SnakBarUtils.networkConnected(getActivity());
            LoadingDialog.cancelLoading();
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
            projectList.add(plist);
        }

       /* projectListAdapter = new ProjectListAdapter(this.getActivity(), R.layout.layout_project_list_item, R.id.active_pending_cardView, projectList);
        listview.setAdapter(projectListAdapter);
        LoadingDialog.cancelLoading();
        //listview.setDivider(new ColorDrawable(getResources().getColor(R.color.colorAccent)));
        listview.setDividerHeight(1);
        listview.setTextFilterEnabled(true);*/
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.active_projects_button:
                tvActiveProjectHeader.setTextColor(Color.parseColor("#ffffff")); // custom color
                btnActiveProjects.setBackgroundColor(Color.parseColor("#048700"));
                tvPendingProjectHeader.setTextColor(Color.parseColor("#252525")); // custom color
                btnPendingProjects.setBackgroundColor(Color.parseColor("#ffffff"));


                projectStatus = "In Progress";
                getProjectsList();
                break;

            case R.id.pending_projects_button:
                tvPendingProjectHeader.setTextColor(Color.parseColor("#ffffff")); // custom color
                btnPendingProjects.setBackgroundColor(Color.parseColor("#048700"));
                tvActiveProjectHeader.setTextColor(Color.parseColor("#252525")); // custom color
                btnActiveProjects.setBackgroundColor(Color.parseColor("#ffffff"));

                projectStatus = "Pending";
                getProjectsList();

                break;

            default:
                break;
        }

    }
}
