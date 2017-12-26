package com.skkk.easytouch.View.AppSelect;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Services.EasyTouchBallService;
import com.skkk.easytouch.Services.EasyTouchLinearService;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AppFragment extends Fragment {
    private static final String APP_INDEX = "app_index";
    private static final String APP_TYPE = "app_type";
    private static final String TOUCH_TYPE = "touch_type";
    @Bind(R.id.rv_apps)
    RecyclerView rvApps;

    private AppAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int appIndex;
    private int appType;
    private int touchType;
    private List<ResolveInfo> allApps;


    public AppFragment() {
        // Required empty public constructor
    }


    public static AppFragment newInstance(int appIndex, int appType,int touchType) {
        AppFragment fragment = new AppFragment();
        Bundle args = new Bundle();
        args.putInt(APP_INDEX, appIndex);
        args.putInt(APP_TYPE, appType);
        args.putInt(TOUCH_TYPE, touchType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appIndex = getArguments().getInt(APP_INDEX);
            appType = getArguments().getInt(APP_TYPE);
            touchType = getArguments().getInt(TOUCH_TYPE);
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
        allApps = new ArrayList<>();
        if (appType == Configs.AppType.APP.getValue()) {
            allApps = PackageUtils.getInstance(getContext().getApplicationContext()).getAllApps();
        } else if (appType == Configs.AppType.SHORTCUT.getValue()) {
            allApps = PackageUtils.getInstance(getContext().getApplicationContext()).getAllShortCuts();
        }
        adapter = new AppAdapter(getContext(), allApps);
        layoutManager = new LinearLayoutManager(getContext());
        rvApps.setAdapter(adapter);
        rvApps.setLayoutManager(layoutManager);
        initEvent();
    }

    private void initEvent() {
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                ResolveInfo appInfoBean = adapter.getmDataList().get(pos);
                String appInfoJson = new Gson().toJson(appInfoBean);
                if (appType == Configs.AppType.APP.getValue()) {
                    if (touchType== Configs.TouchType.LINEAR.getValue()){
                        SpUtils.saveString(getContext(), Configs.KEY_LINEAR_MENU_TOP_APPS_ + appIndex, appInfoJson);
                    }else if (touchType== Configs.TouchType.BALL.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_BALL_MENU_TOP_APPS_ + appIndex, appInfoJson);
                    }
                } else if (appType == Configs.AppType.SHORTCUT.getValue()) {
                    if (touchType== Configs.TouchType.LINEAR.getValue()){
                        SpUtils.saveString(getContext(), Configs.KEY_LINEAR_MENU_BOTTOM_APPS_ + appIndex, appInfoJson);
                    }else if (touchType== Configs.TouchType.BALL.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_BALL_MENU_BOTTOM_APPS_ + appIndex, appInfoJson);
                    }
                }
                if (touchType== Configs.TouchType.LINEAR.getValue()){
                    getActivity().startService(new Intent(getActivity(), EasyTouchLinearService.class));

                }else if (touchType== Configs.TouchType.BALL.getValue()) {
                    getActivity().startService(new Intent(getActivity(), EasyTouchBallService.class));

                }
                getActivity().finish();
            }
        });
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
