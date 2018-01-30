package com.skkk.easytouch.View.AppSelect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skkk.easytouch.Bean.AppInfoBean;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Services.EasyTouchBallService;
import com.skkk.easytouch.Services.EasyTouchLinearService;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.BaseAdapter;
import com.skkk.easytouch.View.ScaleRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppFragment extends Fragment {
    private static final String APP_INDEX = "app_index";
    private static final String APP_TYPE = "app_type";
    private static final String TOUCH_TYPE = "touch_type";
    @Bind(R.id.rv_apps)
    ScaleRecyclerView rvApps;
    @Bind(R.id.container_whole_menu_apps)
    GridLayout containerWholeMenuApps;


    private AppAdapter adapter;
    private LinearLayoutManager layoutManager;

    private int appIndex = -1;
    private int appType;
    private int touchType;
    private List<AppInfoBean> allApps;


    public AppFragment() {
        // Required empty public constructor
    }


    public static AppFragment newInstance(int appIndex, int appType, int touchType) {
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

        initAppsMenu();
        initEvent();
    }

    /**
     * 初始化快捷选择的APP们
     */
    private void initAppsMenu() {
        for (int i = 0; i <10; i++) {
            final ImageView ivApp = (ImageView) containerWholeMenuApps.getChildAt(i);
            if (i!=appIndex) {
                ivApp.setImageResource(R.drawable.ic_add_white_48dp);
            }
            String shortCutStr = "";
            if (appType == Configs.AppType.APP.getValue()) {
                if (touchType == Configs.TouchType.LINEAR.getValue()) {
                    shortCutStr = SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_LINEAR_MENU_TOP_APPS_ + i, "");
                } else if (touchType == Configs.TouchType.BALL.getValue()) {
                    shortCutStr = SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_LINEAR_MENU_TOP_APPS_ + i, "");
                }
            } else if (appType == Configs.AppType.SHORTCUT.getValue()) {
                if (touchType == Configs.TouchType.LINEAR.getValue()) {
                    shortCutStr = SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_LINEAR_MENU_BOTTOM_APPS_ + i, "");
                } else if (touchType == Configs.TouchType.BALL.getValue()) {
                    shortCutStr = SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_BALL_MENU_BOTTOM_APPS_ + i, "");
                }
            }
            final int finalIndex = i;
            if (!TextUtils.isEmpty(shortCutStr)) {
                final AppInfoBean appInfo = new Gson().fromJson(shortCutStr, AppInfoBean.class);
                if (appInfo != null) {
                    ivApp.setImageDrawable(PackageUtils.getInstance(getContext().getApplicationContext()).getShortCutIcon(appInfo));
                }
            }
            ivApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appIndex = finalIndex;
                    initAppsMenu();
                }
            });

            if (i==appIndex){
                ivApp.setImageResource(R.drawable.vector_drawable_selection);
            }
        }
    }

    private void initEvent() {
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                if (appIndex==-1){
                    Toast.makeText(getActivity(), "您还未选择需要切换的应用。", Toast.LENGTH_SHORT).show();
                    return;
                }

                AppInfoBean appInfoBean = adapter.getmDataList().get(pos);
                String appInfoJson = new Gson().toJson(appInfoBean);

                if (appType == Configs.AppType.APP.getValue()) {
                    if (touchType == Configs.TouchType.LINEAR.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_LINEAR_MENU_TOP_APPS_ + appIndex, appInfoJson);
                    } else if (touchType == Configs.TouchType.BALL.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_LINEAR_MENU_TOP_APPS_ + appIndex, appInfoJson);
                    }
                } else if (appType == Configs.AppType.SHORTCUT.getValue()) {
                    if (touchType == Configs.TouchType.LINEAR.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_LINEAR_MENU_BOTTOM_APPS_ + appIndex, appInfoJson);
                    } else if (touchType == Configs.TouchType.BALL.getValue()) {
                        SpUtils.saveString(getContext(), Configs.KEY_BALL_MENU_BOTTOM_APPS_ + appIndex, appInfoJson);
                    }
                }
                appIndex = -1;
                initAppsMenu();
//                if (touchType == Configs.TouchType.LINEAR.getValue()) {
//                    getActivity().startService(new Intent(getActivity(), EasyTouchLinearService.class));
//
//                } else if (touchType == Configs.TouchType.BALL.getValue()) {
//                    getActivity().startService(new Intent(getActivity(), EasyTouchBallService.class));
//
//                }
//                getActivity().finish();
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
        ButterKnife.unbind(this);
        if (touchType == Configs.TouchType.LINEAR.getValue()) {
            getActivity().startService(new Intent(getActivity(), EasyTouchLinearService.class));

        } else if (touchType == Configs.TouchType.BALL.getValue()) {
            getActivity().startService(new Intent(getActivity(), EasyTouchBallService.class));

        }
        super.onDestroyView();
    }
}