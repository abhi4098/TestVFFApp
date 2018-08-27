package com.valleyforge.cdi.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.api.ApiAdapter;
import com.valleyforge.cdi.api.RetrofitInterface;
import com.valleyforge.cdi.generated.model.DashboardDataResponse;
import com.valleyforge.cdi.generated.model.LoginResponse;
import com.valleyforge.cdi.ui.activities.ActivePendingActivity;
import com.valleyforge.cdi.ui.activities.BLEInformationActivity;
import com.valleyforge.cdi.ui.activities.CompletedProjectsActivity;
import com.valleyforge.cdi.ui.activities.LoginActivity;
import com.valleyforge.cdi.ui.activities.NavigationActivity;
import com.valleyforge.cdi.utils.LoadingDialog;
import com.valleyforge.cdi.utils.NetworkUtils;
import com.valleyforge.cdi.utils.PrefUtils;
import com.valleyforge.cdi.utils.SnakBarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.valleyforge.cdi.api.ApiEndPoints.BASE_URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link  interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RetrofitInterface.UserDashboardClient UserDashboardAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Fragment fragment = null;


    @BindView(R.id.active_pending_count)
    TextView tvactiveCount;


    @BindView(R.id.active_pending_cardView)
    CardView cvActivePending;


    @BindView(R.id.competed_project_cardView)
    CardView cvCompletedProjects;


    @BindView(R.id.completed_count)
    TextView tvCompletedCount;



   /* @OnClick(R.id.active_pending_cardView)
    public void openActivePending() {
        fragment = new ActivePendingFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        TextView tvAppTitle = (TextView) getActivity().findViewById(R.id.tv_app_title);
        ImageView ivBackIcon = (ImageView) getActivity().findViewById(R.id.back_icon);
        tvAppTitle.setText("Active/Pending Projects");
        ivBackIcon.setVisibility(View.VISIBLE);
    }*/

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this,rootView);
        cvActivePending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivePendingActivity.class);
                startActivity(i);
            }
        });

        cvCompletedProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CompletedProjectsActivity.class);
                startActivity(i);
            }
        });

        setUpRestAdapter();
        getDashboardData();
        return rootView;

    }

    private void setUpRestAdapter() {
        UserDashboardAdapter = ApiAdapter.createRestAdapter(RetrofitInterface.UserDashboardClient.class, BASE_URL, getActivity());

    }

    private void getDashboardData() {
        LoadingDialog.showLoadingDialog(getActivity(),"Loading...");
        Call<DashboardDataResponse> call = UserDashboardAdapter.userDashboardData(PrefUtils.getUserId(getActivity()),PrefUtils.getRole(getActivity()));
        if (NetworkUtils.isNetworkConnected(getActivity())) {
            call.enqueue(new Callback<DashboardDataResponse>() {

                @Override
                public void onResponse(Call<DashboardDataResponse> call, Response<DashboardDataResponse> response) {
                    if (response.isSuccessful()) {
                        if(response.body().getMsg().equals("success")) {
                            Log.e("abhi", "onResponse:........... " +response.body().getPendingprojects() );

                            int activePendingSum = response.body().getPendingprojects() +response.body().getInprogressprojects();
                            PrefUtils.storeActiveCount(String.valueOf(activePendingSum),getActivity());
                            PrefUtils.storeCompletedCount(String.valueOf(response.body().getCompletedprojects()),getActivity());
                            tvactiveCount.setText(String.valueOf(activePendingSum));
                            tvCompletedCount.setText(String.valueOf(response.body().getCompletedprojects()));

                        }
                        else{
                            Toast.makeText(getActivity(),response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        LoadingDialog.cancelLoading();

                    }
                }

                @Override
                public void onFailure(Call<DashboardDataResponse> call, Throwable t) {
                    Log.e("abhi", "onResponse: error....................... "  );

                    LoadingDialog.cancelLoading();
                }


            });

        } else {
            Log.e("abhi", "getDashboardData: offline mode....................");
            tvactiveCount.setText(PrefUtils.getActiveCount(getActivity()));
            tvCompletedCount.setText(PrefUtils.getCompletedCount(getActivity()));
            SnakBarUtils.networkConnected(getActivity());
            LoadingDialog.cancelLoading();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

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

}
