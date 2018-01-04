package com.skkk.easytouch.View.FunctionSelect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skkk.easytouch.R;
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

    /**
     * 点击事件
     * @param view
     */
    @OnClick({R.id.siv_function_click, R.id.siv_function_long_click, R.id.siv_function_touch_left, R.id.siv_function_touch_right, R.id.siv_function_touch_up, R.id.siv_function_touch_down, R.id.siv_function_menu_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.siv_function_click:

                break;
            case R.id.siv_function_long_click:
                break;
            case R.id.siv_function_touch_left:
                break;
            case R.id.siv_function_touch_right:
                break;
            case R.id.siv_function_touch_up:
                break;
            case R.id.siv_function_touch_down:
                break;
            case R.id.siv_function_menu_number:
                break;
        }
    }
}
