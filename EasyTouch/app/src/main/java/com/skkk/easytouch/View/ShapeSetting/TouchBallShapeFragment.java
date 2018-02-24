package com.skkk.easytouch.View.ShapeSetting;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Services.EasyTouchBallService;
import com.skkk.easytouch.Services.EasyTouchLinearService;
import com.skkk.easytouch.Services.FloatService;
import com.skkk.easytouch.Utils.DialogUtils;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.Utils.ServiceUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.BallDrawableSelect.BallDrawableSelectActivity;
import com.skkk.easytouch.View.SettingSwitchItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TouchBallShapeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TouchBallShapeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @Bind(R.id.ivTouchBall)
    ImageView ivTouchBall;
    @Bind(R.id.ll_touch_container)
    RelativeLayout llTouchContainer;
    @Bind(R.id.sb_radius)
    AppCompatSeekBar sbRadius;
    @Bind(R.id.sb_vibrate)
    AppCompatSeekBar sbVibrate;
    @Bind(R.id.sb_alpha)
    AppCompatSeekBar sbAlpha;
    @Bind(R.id.tv_ball_drawable)
    TextView tvBallDrawable;
    @Bind(R.id.tv_ball_pos_custom)
    TextView tvBallPosCustom;
    @Bind(R.id.ssiv_pos_freeze)
    SettingSwitchItemView ssivPosFreeze;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int radius;
    private int vibrate;
    private int alpha;

    public static final int RADIUS_MIN = 15;
    public static final int RADIUS_STEP = 5;
    public static final int VIBRATE_MIN = 0;
    public static final int VIBRATE_STEP = 10;
    private Vibrator vibrator;
    private String drawableName;
    private boolean isServiceRunning = false;
    private boolean linearPosFreeze;//固定悬浮球位置


    public TouchBallShapeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TouchBallShapeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TouchBallShapeFragment newInstance(String param1, String param2) {
        TouchBallShapeFragment fragment = new TouchBallShapeFragment();
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
        View view = inflater.inflate(R.layout.fragment_touch_ball_shape, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);
        initUI();
        initEvent();
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
        //如果悬浮球存在，那么就更新悬浮球
        if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
            ivTouchBall.setVisibility(View.INVISIBLE);
            getActivity().stopService(new Intent(getActivity(), EasyTouchBallService.class));
            getActivity().startService(new Intent(getActivity(), EasyTouchBallService.class));
            isServiceRunning = true;
        } else {
            isServiceRunning = false;
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        radius = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_RADIUS, Configs.DEFAULT_TOUCH_WIDTH_BALL);
        sbRadius.setProgress((radius - RADIUS_MIN) / RADIUS_STEP);

        vibrate = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_BALL, Configs.DEFAULT_VIBRATE_LEVEL);
        sbVibrate.setProgress((vibrate - VIBRATE_MIN) / VIBRATE_STEP);

        alpha = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_BALL, Configs.DEFAULT_ALPHA);
        sbAlpha.setProgress(alpha);

        drawableName = SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL, "ball_0");
        upDateTouchViewShape(radius);

//        if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
//            tvBallPosCustom.setText("当前位置未固定，点击固定");
//            linearPosFreeze = SpUtils.getBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, false);
//            if (linearPosFreeze){//已经固定
//                tvBallPosCustom.setText("当前位置已固定，点击取消固定");
//            }else {//未固定
//                tvBallPosCustom.setText("当前位置未固定，点击固定");
//            }
//        }else{
//            tvBallPosCustom.setText("点击说明");
//        }

        if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
            ssivPosFreeze.setTitle("当前位置未固定，点击固定");
            linearPosFreeze = SpUtils.getBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, false);
            ssivPosFreeze.setSwichChecked(linearPosFreeze);

            if (linearPosFreeze){//已经固定
                ssivPosFreeze.setTitle("当前位置已固定，点击取消固定");
            }else {//未固定
                ssivPosFreeze.setTitle("当前位置未固定，点击固定");
            }
        }else{
            ssivPosFreeze.setTitle("当前未开启悬浮球");
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        /**
         * 设置半径
         */
        sbRadius.setMax(5);
        sbRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: -->" + (RADIUS_MIN + progress * RADIUS_STEP));
                upDateTouchViewShape(RADIUS_MIN + progress * RADIUS_STEP);
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_RADIUS, RADIUS_MIN + progress * RADIUS_STEP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /**
         * 设置震动等级
         */
        sbVibrate.setMax(5);
        sbVibrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() called with: progress = [" + progress + "]");
                vibrator.vibrate(VIBRATE_MIN + progress * VIBRATE_STEP);
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_BALL, (VIBRATE_MIN + progress * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        sbAlpha.setMax(255);
        sbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() called with: progress = [" + progress + "]");
                alpha = progress;
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_BALL, progress);
                upDateTouchViewShape(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 点击选择图片
         */
        tvBallDrawable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BallDrawableSelectActivity.class));
            }
        });

        ssivPosFreeze.setOnSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //是否打开了悬浮球
                if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
                    if (isChecked){//已经位置固定
                        DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
                                "点击确认取消固定悬浮条位置，悬浮条将可以上下拖动。",
                                "确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        linearPosFreeze=false;
                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, false);
                                        restartService();
                                    }
                                }, "取消", null)
                                .show();
                    }else {
                        DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
                                "点击确认固定悬浮条位置，悬浮条将不可上下拖动。",
                                "确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        linearPosFreeze=true;
                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, true);
                                        restartService();
                                    }
                                }, "取消", null)
                                .show();
                    }
                }else {//未打开悬浮条，那么打开
                    DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
                            "检测到当前未开启悬浮条，点击确认将打开悬浮条，然后点击固定位置进行固定。",
                            "确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tvBallPosCustom.setText("点击确认");
                                    if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
                                        getContext().stopService(new Intent(getContext(), EasyTouchLinearService.class));
                                    }

                                    getContext().startService(new Intent(getContext(), EasyTouchBallService.class));
                                    getContext().startService(new Intent(getContext(), FloatService.class));
                                    llTouchContainer.setVisibility(View.GONE);
                                }
                            },"取消",null)
                            .show();
                }
            }
        });

//        //设置是否固定位置：首先判断是否存在可以调整位置的悬浮窗，如果不存在就打开
//        tvBallPosCustom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //是否打开了悬浮条
//                if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
//                    if (linearPosFreeze){
//                        DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
//                                "点击确认取消固定悬浮条位置，悬浮条将可以上下拖动。",
//                                "确认", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, false);
//                                        restartService();
//                                    }
//                                }, "取消", null)
//                                .show();
//                    }else {
//                        DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
//                                "点击确认固定悬浮条位置，悬浮条将不可上下拖动。",
//                                "确认", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_BALL_FREEZE, true);
//                                        restartService();
//                                    }
//                                }, "取消", null)
//                                .show();
//                    }
//                }else {//未打开悬浮条，那么打开
//                    DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
//                            "检测到当前未开启悬浮条，点击确认将打开悬浮条，然后点击固定位置进行固定。",
//                            "确认", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    tvBallPosCustom.setText("点击确认");
//                                    if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
//                                        getContext().stopService(new Intent(getContext(), EasyTouchLinearService.class));
//                                    }
//
//                                    getContext().startService(new Intent(getContext(), EasyTouchBallService.class));
//                                    getContext().startService(new Intent(getContext(), FloatService.class));
//                                    llTouchContainer.setVisibility(View.GONE);
//                                }
//                            },"取消",null)
//                            .show();
//                }
//            }
//        });
    }

    /**
     * 更新UI 重新打开悬浮球服务
     */
    private void restartService() {
        initUI();
        getContext().startService(new Intent(getContext(),EasyTouchBallService.class));
    }

    /**
     * 更新形状
     *
     * @param radius
     */
    public void upDateTouchViewShape(int radius) {
        if (isServiceRunning) {
            getActivity().startService(new Intent(getActivity(), EasyTouchBallService.class));
        } else {
            ViewGroup.LayoutParams layoutParams = llTouchContainer.getLayoutParams();
            if (radius != 0) {
                layoutParams.width = 2 * dp2px(radius);
                layoutParams.height = 2 * dp2px(radius);
            }
            llTouchContainer.setLayoutParams(layoutParams);
            if (drawableName.equals(Configs.KEY_PHOTO_CUSTOM_DRAWABLE)) {
                ivTouchBall.setImageURI(Uri.parse(SpUtils.getString(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL_CUSTOM, "ball_0")));
            } else {
                ivTouchBall.setImageResource(PackageUtils.getResource(getContext().getApplicationContext(), drawableName));
            }
            ivTouchBall.setAlpha((float) alpha / 255);
            ivTouchBall.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private int dp2px(float dp) {
        final float scale = getContext().getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }
}
