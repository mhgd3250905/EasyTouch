package com.skkk.easytouch.View.FunctionSelect;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.SpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FunctionDetailBaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FunctionDetailBaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_OP_TYPE = "arg_op_type";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.tv_item_back)
    TextView tvItemBack;
    @Bind(R.id.tv_item_home)
    TextView tvItemHome;
    @Bind(R.id.tv_item_recent)
    TextView tvItemRecent;
    @Bind(R.id.tv_item_notification)
    TextView tvItemNotification;
    @Bind(R.id.tv_item_location)
    TextView tvItemLocation;
    @Bind(R.id.tv_item_voice)
    TextView tvItemVoice;
    @Bind(R.id.tv_item_pay)
    TextView tvItemPay;
    @Bind(R.id.tv_item_app)
    TextView tvItemApp;
    @Bind(R.id.tv_item_menu)
    TextView tvItemMenu;
    @Bind(R.id.tv_item_previous_app)
    TextView tvItemPreviousApp;
    @Bind(R.id.tv_item_shot_screen)
    TextView tvItemShotScreen;

    // TODO: Rename and change types of parameters
    private String opType;
    private String mParam2;


    public FunctionDetailBaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param opType Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FunctionDetailBaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FunctionDetailBaseFragment newInstance(String opType, String param2) {
        FunctionDetailBaseFragment fragment = new FunctionDetailBaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OP_TYPE, opType);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            opType = getArguments().getString(ARG_OP_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_function_detail_base, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        //如果是自定义菜单中元素的功能，那么就没必要显示菜单
        if (opType.startsWith(FuncConfigs.VALUE_FUNC_OP_MENU_BALL)){
            tvItemMenu.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_item_back, R.id.tv_item_home, R.id.tv_item_recent, R.id.tv_item_notification,
            R.id.tv_item_location, R.id.tv_item_voice, R.id.tv_item_pay, R.id.tv_item_app,
            R.id.tv_item_menu,R.id.tv_item_previous_app,R.id.tv_item_lock_screen,
            R.id.tv_item_shot_screen})
    public void onViewClicked(View view) {
        int funcType = 0;
        switch (view.getId()) {
            case R.id.tv_item_back:
                funcType = FuncConfigs.Func.BACK.getValue();
                break;
            case R.id.tv_item_home:
                funcType = FuncConfigs.Func.HOME.getValue();
                break;
            case R.id.tv_item_recent:
                funcType = FuncConfigs.Func.RECENT.getValue();
                break;
            case R.id.tv_item_notification:
                funcType = FuncConfigs.Func.NOTIFICATION.getValue();
                break;
            case R.id.tv_item_location:
                funcType = FuncConfigs.Func.TRUN_POS.getValue();
                break;
            case R.id.tv_item_voice:
                funcType = FuncConfigs.Func.VOICE_MENU.getValue();
                break;
            case R.id.tv_item_pay:
                funcType = FuncConfigs.Func.PAY_MENU.getValue();
                break;
            case R.id.tv_item_app:
                funcType = FuncConfigs.Func.APP_MENU.getValue();
                break;
            case R.id.tv_item_menu:
                funcType = FuncConfigs.Func.MENU.getValue();
                break;
            case R.id.tv_item_previous_app:
                funcType = FuncConfigs.Func.PREVIOUS_APP.getValue();
                break;
            case R.id.tv_item_lock_screen:
                funcType = FuncConfigs.Func.LOCK_SCREEN.getValue();
                break;
            case R.id.tv_item_shot_screen:
                funcType = FuncConfigs.Func.SHOT_SCREEN.getValue();
                break;
        }
        SpUtils.saveInt(getContext().getApplicationContext(), opType, funcType);
        getActivity().finish();
    }

    @OnClick(R.id.tv_item_menu)
    public void onViewClicked() {
    }
}
