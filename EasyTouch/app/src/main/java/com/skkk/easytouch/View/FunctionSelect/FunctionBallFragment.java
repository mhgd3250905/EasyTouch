package com.skkk.easytouch.View.FunctionSelect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.SettingItemView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FunctionBallFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.siv_function_click)
    SettingItemView sivFunctionClick;
    @Bind(R.id.siv_function_long_click)
    SettingItemView sivFunctionLongClick;
    @Bind(R.id.siv_function_touch_left)
    SettingItemView sivFunctionTouchLeft;
    @Bind(R.id.siv_function_touch_right)
    SettingItemView sivFunctionTouchRight;
    @Bind(R.id.siv_function_touch_up)
    SettingItemView sivFunctionTouchUp;
    @Bind(R.id.siv_function_touch_down)
    SettingItemView sivFunctionTouchDown;
    @Bind(R.id.siv_function_menu_number)
    SettingItemView sivFunctionMenuNumber;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FunctionBallFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FunctionBallFragment newInstance(String param1, String param2) {
        FunctionBallFragment fragment = new FunctionBallFragment();
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
        View view = inflater.inflate(R.layout.fragment_function_ball, container, false);
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
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_CLICK, sivFunctionClick);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_LONG_CLICK, sivFunctionLongClick);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_UP, sivFunctionTouchUp);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_LEFT, sivFunctionTouchLeft);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_BOTTOM, sivFunctionTouchDown);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT, sivFunctionTouchRight);
    }

    /**
     * 根据操作类型来确定对应保存的操作目的
     *
     * @param opType
     * @param item
     */
    private void setItemDesc(String opType, SettingItemView item) {
        int funcType = SpUtils.getInt(getContext().getApplicationContext(), opType, FuncConfigs.Func.BACK.getValue());
        String funcDesc = "";
        if (funcType == FuncConfigs.Func.BACK.getValue()) {//返回键
            funcDesc = "返回";
        } else if (funcType == FuncConfigs.Func.HOME.getValue()) {//Home键
            funcDesc = "主页";
        } else if (funcType == FuncConfigs.Func.RECENT.getValue()) {//任务键
            funcDesc = "任务";
        } else if (funcType == FuncConfigs.Func.NOTIFICATION.getValue()) {//通知栏
            funcDesc = "通知";
        } else if (funcType == FuncConfigs.Func.TRUN_POS.getValue()) {//切换位置
            funcDesc = "切换位置";
        } else if (funcType == FuncConfigs.Func.VOICE_MENU.getValue()) {//声音菜单
            funcDesc = "音量菜单";
        } else if (funcType == FuncConfigs.Func.PAY_MENU.getValue()) {//支付菜单
            funcDesc = "支付菜单";
        } else if (funcType == FuncConfigs.Func.APP_MENU.getValue()) {//app菜单
            funcDesc = "快捷App菜单";
        } else if (funcType == FuncConfigs.Func.MENU.getValue()) {//app菜单
            funcDesc = "二级菜单";
        } else if (funcType == FuncConfigs.Func.PREVIOUS_APP.getValue()) {//app菜单
            funcDesc = "上一个应用";
        }
        item.setValue(funcDesc);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FuncConfigs.REQUEST_SELECT_FUNC_DETAIL) {
            initUI();
        }

    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.siv_function_click, R.id.siv_function_long_click, R.id.siv_function_touch_left, R.id.siv_function_touch_right, R.id.siv_function_touch_up, R.id.siv_function_touch_down, R.id.siv_function_menu_number})
    public void onViewClicked(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.siv_function_click:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_CLICK);
                break;
            case R.id.siv_function_long_click:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_LONG_CLICK);
                break;
            case R.id.siv_function_touch_left:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_FLING_LEFT);
                break;
            case R.id.siv_function_touch_right:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT);
                break;
            case R.id.siv_function_touch_up:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_FLING_UP);
                break;
            case R.id.siv_function_touch_down:
                intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_FLING_BOTTOM);
                break;
            case R.id.siv_function_menu_number:
                break;
        }
        intent.setClass(getActivity(), FunctionDetailSelectActivity.class);
        getActivity().startActivityForResult(intent, FuncConfigs.REQUEST_SELECT_FUNC_DETAIL);
    }
}
