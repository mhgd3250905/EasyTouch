package com.skkk.easytouch.View.FunctionSelect;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.DpUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.SettingItemView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.skkk.easytouch.Utils.SpUtils.KEY_MENU_BALL_COUNT;


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
    @Bind(R.id.container_function_menu_number)
    LinearLayout containerFunctionMenuNumber;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int menuBallCount;

    private AlertDialog alertDialog;


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
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        initEvent();
    }

    /**
     * 初始化UI
     */
    public void initUI() {
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_CLICK, sivFunctionClick);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_LONG_CLICK, sivFunctionLongClick);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_UP, sivFunctionTouchUp);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_LEFT, sivFunctionTouchLeft);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_BOTTOM, sivFunctionTouchDown);
        setItemDesc(FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT, sivFunctionTouchRight);
        initMenuCountView();

        for (int i = 0; i < menuBallCount; i++) {
            setItemDesc(FuncConfigs.VALUE_FUNC_OP_MENU_BALL + i, (SettingItemView) containerFunctionMenuNumber.getChildAt(i + 2));
        }
    }

    /**
     * 设置菜单元素
     */
    private void initMenuCountView() {
        if (containerFunctionMenuNumber.getChildCount() > 1) {
            for (int i = containerFunctionMenuNumber.getChildCount(); i > 1; i--) {
                containerFunctionMenuNumber.removeView(containerFunctionMenuNumber.getChildAt(i));
            }
        }
        menuBallCount = SpUtils.getInt(getContext().getApplicationContext(), SpUtils.KEY_MENU_BALL_COUNT, 0);
        sivFunctionMenuNumber.setValue(String.format(getString(R.string.function_menu_number_value), menuBallCount));
        if (menuBallCount > 0) {
            for (int i = 0; i < menuBallCount; i++) {//遍历添加每一个菜单元素
                //添加菜单元素
                SettingItemView sivMenuBall = new SettingItemView(getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (i == menuBallCount - 1) {
                    layoutParams.setMargins(DpUtils.dp2px(getContext(), 10), DpUtils.dp2px(getContext(), 10), DpUtils.dp2px(getContext(), 10), DpUtils.dp2px(getContext(), 10));
                } else {
                    layoutParams.setMargins(DpUtils.dp2px(getContext(), 10), DpUtils.dp2px(getContext(), 10), DpUtils.dp2px(getContext(), 10), 0);
                }
                sivMenuBall.setLayoutParams(layoutParams);
                sivMenuBall.setTitle(String.format(getString(R.string.function_menu_ball_title), i + 1));
                sivMenuBall.setValue(getString(R.string.function_item_message));

                //设置点击事件
                final int finalI = i;
                sivMenuBall.setSettingItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //保存菜单元素的Key为String+index的方式
                        Intent intent = new Intent();
                        intent.putExtra(FuncConfigs.KEY_FUNC_OP, FuncConfigs.VALUE_FUNC_OP_MENU_BALL + finalI);
                        intent.setClass(getActivity(), FunctionDetailSelectActivity.class);
                        getActivity().startActivityForResult(intent, FuncConfigs.REQUEST_SELECT_FUNC_DETAIL);
                    }
                });
                containerFunctionMenuNumber.addView(sivMenuBall);
            }
        }
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        //设置二级菜单数量的事件
        sivFunctionMenuNumber.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker numberPicker = new NumberPicker(getContext());
                numberPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        return String.valueOf(value);
                    }
                });
                numberPicker.setMaxValue(5);
                numberPicker.setMinValue(0);
                numberPicker.setValue(menuBallCount);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.ic_warning);
                builder.setView(numberPicker);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SpUtils.saveInt(getContext().getApplicationContext(), KEY_MENU_BALL_COUNT, numberPicker.getValue());
                        menuBallCount = numberPicker.getValue();
                        initMenuCountView();
                        initUI();
                        alertDialog.dismiss();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
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
        } else if (funcType == FuncConfigs.Func.LOCK_SCREEN.getValue()) {//app菜单
            funcDesc = "锁屏";
        }else if (funcType == FuncConfigs.Func.SHOT_SCREEN.getValue()) {//app菜单
            funcDesc = "截屏";
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
