package com.skkk.easytouch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skkk.easytouch.Utils.PackageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.rv_apps)
    RecyclerView rvApps;

    private AppAdapter adapter;
    private LinearLayoutManager layoutManager;

    private String mParam1;
    private String mParam2;


    public AppFragment() {
        // Required empty public constructor
    }


    public static AppFragment newInstance(String param1, String param2) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
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
        View view = inflater.inflate(R.layout.fragment_app, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter=new AppAdapter(getContext(), PackageUtils.getInstance(getContext().getApplicationContext()).getAllApps());
        layoutManager=new LinearLayoutManager(getContext());
        rvApps.setAdapter(adapter);
        rvApps.setLayoutManager(layoutManager);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
