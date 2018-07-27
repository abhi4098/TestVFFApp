package com.valleyforge.cdi.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.valleyforge.cdi.R;
import com.valleyforge.cdi.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this,rootView);
        // etUsername.setEnabled(false);
        etUsername.setFocusable(false);
        etUsername.setText(PrefUtils.getUserName(getActivity()));

        // etuserEmail.setEnabled(false);
        etuserEmail.setFocusable(false);
        etuserEmail.setText(PrefUtils.getEmail(getActivity()));

        // etUserAdd.setEnabled(false);
        etUserAdd.setFocusable(false);
        etUserAdd.setText(PrefUtils.getUserAdd(getActivity()));

        // etuserPhone.setEnabled(false);
        etuserPhone.setFocusable(false);
        etuserPhone.setText(PrefUtils.getUserPhone(getActivity()));
        //etUsername.setText(PrefUtils.getUserName(this));
        //etUsername.setText(PrefUtils.getUserName(this));
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
