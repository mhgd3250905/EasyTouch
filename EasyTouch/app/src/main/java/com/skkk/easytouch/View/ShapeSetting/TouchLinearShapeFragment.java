package com.skkk.easytouch.View.ShapeSetting;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.MainActivity;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Services.EasyTouchBallService;
import com.skkk.easytouch.Services.EasyTouchLinearService;
import com.skkk.easytouch.Services.FloatService;
import com.skkk.easytouch.Utils.DialogUtils;
import com.skkk.easytouch.Utils.ServiceUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.ColorPickerDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static android.content.Context.VIBRATOR_SERVICE;
import static com.skkk.easytouch.Configs.KEY_TOUCH_UI_BOTTOM_COLOR;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TouchLinearShapeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TouchLinearShapeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @Bind(R.id.iv_touch_top)
    ImageView ivTouchTop;
    @Bind(R.id.iv_touch_mid)
    ImageView ivTouchMid;
    @Bind(R.id.iv_touch_bottom)
    ImageView ivTouchBottom;
    @Bind(R.id.ll_touch_container)
    LinearLayout llTouchContainer;
    @Bind(R.id.sb_height)
    AppCompatSeekBar sbHeight;
    @Bind(R.id.sb_width)
    AppCompatSeekBar sbWidth;
    @Bind(R.id.sb_vibrate)
    AppCompatSeekBar sbVibrate;
    @Bind(R.id.sb_alpha)
    AppCompatSeekBar sbAlpha;


    @Bind(R.id.rb_hide_line_1)
    RadioButton rbHideLine1;
    @Bind(R.id.rb_hide_line_2)
    RadioButton rbHideLine2;
    @Bind(R.id.rb_hide_rect)
    RadioButton rbHideRect;
    @Bind(R.id.rg_hide_theme)
    RadioGroup rgHideTheme;
    @Bind(R.id.container_touch_linear)
    LinearLayout containerTouchLinear;
    @Bind(R.id.tv_ball_pos_custom)
    TextView tvBallPosCustom;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static final int ITEM_HEIGHT_MIN = 40;
    public static final int ITEM_HEIGHT_STEP = 20;
    public static final int ITEM_WIDTH_MIN = 11;
    public static final int ITEM_WIDTH_STEP = 3;
    public static final int VIBRATE_MIN = 0;
    public static final int VIBRATE_STEP = 10;
    private Vibrator vibrator;
    private int setHeight;
    private int setWidth;
    private int setVibrate;
    private boolean isServiceRunning = false;
    private int topColor;
    private int midColor;
    private int bottomColor;
    private int alpha;
    private int theme;
    private int topDrawable;
    private int midDrawable;
    private int bottomDrawable;

    private BroadcastReceiver receiver;
    private boolean linearPosFreeze;//悬浮条是否固定能够

    public TouchLinearShapeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TouchLinearShapeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TouchLinearShapeFragment newInstance(String param1, String param2) {
        TouchLinearShapeFragment fragment = new TouchLinearShapeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initColorSettingBoardcast();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_touch_linear_shape, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vibrator = (Vibrator) getContext().getSystemService(VIBRATOR_SERVICE);

        initEvent();
        initUI();

        if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), "com.skkk.easytouch.Services.EasyTouchLinearService")) {
            llTouchContainer.setVisibility(View.INVISIBLE);
            isServiceRunning = true;
        } else {
            llTouchContainer.setVisibility(View.VISIBLE);
            isServiceRunning = false;
        }
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        setHeight = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, Configs.DEFAULT_TOUCH_HEIGHT);
        sbHeight.setProgress(((setHeight / 3) - ITEM_HEIGHT_MIN) / ITEM_HEIGHT_STEP);

        setWidth = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH, Configs.DEFAULT_TOUCH_WIDTH);
        sbWidth.setProgress((setWidth - ITEM_WIDTH_MIN) / ITEM_WIDTH_STEP);

        setVibrate = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_LINEAR, Configs.DEFAULT_VIBRATE_LEVEL);
        sbVibrate.setProgress((setVibrate - VIBRATE_MIN) / VIBRATE_STEP);

        alpha = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_LINEAR, Configs.DEFAULT_ALPHA);
        sbAlpha.setProgress(alpha);

        if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
            tvBallPosCustom.setText("当前位置未固定，点击固定");
            linearPosFreeze = SpUtils.getBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_LINEAR_FREEZE, false);
            if (linearPosFreeze){//已经固定
                tvBallPosCustom.setText("当前位置已固定，点击取消固定");
            }else {//未固定
                tvBallPosCustom.setText("当前位置未固定，点击固定");
            }
        }else{
            tvBallPosCustom.setText("点击说明");
        }
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        /**
         * 设置高度
         */
        sbHeight.setMax(5);
        sbHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: -->" + (ITEM_HEIGHT_MIN + progress * ITEM_HEIGHT_STEP) * 3);
                upDateTouchViewShape((ITEM_HEIGHT_MIN + progress * ITEM_HEIGHT_STEP) * 3, 0);
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, (ITEM_HEIGHT_MIN + progress * ITEM_HEIGHT_STEP) * 3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 设置宽度
         */
        sbWidth.setMax(5);
        sbWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: -->" + (ITEM_WIDTH_MIN + progress * ITEM_WIDTH_STEP));
                upDateTouchViewShape(0, (ITEM_WIDTH_MIN + progress * ITEM_WIDTH_STEP));
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH, (ITEM_WIDTH_MIN + progress * ITEM_WIDTH_STEP));
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
                upDateTouchViewShape(0, 0);
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_LINEAR, (VIBRATE_MIN + progress * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        /**
         * 设置透明度
         */
        sbAlpha.setMax(255);
        sbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() called with: progress = [" + progress + "]");
                alpha = progress;
                upDateTouchViewShape(0, 0);
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_LINEAR, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        topColor = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_COLOR, Color.RED);
        ivTouchTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog(Configs.LinearPos.TOP.getValue());
            }
        });

        midColor = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_MID_COLOR, Color.GREEN);
        ivTouchMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog(Configs.LinearPos.MID.getValue());

            }
        });

        bottomColor = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_BOTTOM_COLOR, Color.BLUE);
        ivTouchBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog(Configs.LinearPos.BOTTOM.getValue());
            }
        });

        //设置隐藏式主题
        theme = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_THEME_HIDE, -1);
        if (theme == Configs.TOUCH_UI_THEME_HIDE_LINE_1) {
            rbHideLine1.setChecked(true);
        } else if (theme == Configs.TOUCH_UI_THEME_HIDE_LINE_2) {
            rbHideLine2.setChecked(true);
        } else if (theme == Configs.TOUCH_UI_THEME_HIDE_RECT) {
            rbHideRect.setChecked(true);
        }
        rgHideTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_hide_line_1) {
                    theme = Configs.TOUCH_UI_THEME_HIDE_LINE_1;
                } else if (checkedId == R.id.rb_hide_line_2) {
                    theme = Configs.TOUCH_UI_THEME_HIDE_LINE_2;
                } else if (checkedId == R.id.rb_hide_rect) {
                    theme = Configs.TOUCH_UI_THEME_HIDE_RECT;
                }
                SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_THEME_HIDE, theme);
                sbAlpha.setProgress(0);
                upDateTouchViewShape(0, 0);
            }
        });

        //设置是否固定位置：首先判断是否存在可以调整位置的悬浮窗，如果不存在就打开
        tvBallPosCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否打开了悬浮条
                if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
                    if (linearPosFreeze){
                        DialogUtils.showDialog(getContext(), R.drawable.ic_notifications, "提醒",
                                "点击确认取消固定悬浮条位置，悬浮条将可以上下拖动。",
                                "确认", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_LINEAR_FREEZE, false);
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
                                        SpUtils.saveBoolean(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_POS_LINEAR_FREEZE, true);
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
                                    if (ServiceUtils.isServiceRun(getContext().getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
                                        getContext().stopService(new Intent(getContext(), EasyTouchBallService.class));
                                    }

                                    getContext().startService(new Intent(getContext(), EasyTouchLinearService.class));
                                    getContext().startService(new Intent(getContext(), FloatService.class));
                                    containerTouchLinear.setVisibility(View.GONE);
                                }
                            },"取消",null)
                    .show();
                }
            }
        });
    }

    /**
     * 更新UI 重新打开悬浮球服务
     */
    private void restartService() {
        initUI();
        getContext().startService(new Intent(getContext(),EasyTouchLinearService.class));
    }

    /**
     * 更新形状
     *
     * @param length
     * @param width
     */
    public void upDateTouchViewShape(int length, int width) {
        if (isServiceRunning) {
            getContext().startService(new Intent(getContext(), EasyTouchLinearService.class));
        } else {
            ViewGroup.LayoutParams layoutParams = llTouchContainer.getLayoutParams();
            if (length != 0) {
                layoutParams.height = dp2px(getContext().getApplicationContext(), length);
            }
            if (width != 0) {
                layoutParams.width = dp2px(getContext().getApplicationContext(), width);
            }
            llTouchContainer.setLayoutParams(layoutParams);


            topDrawable = R.drawable.shape_react_top;
            midDrawable = R.drawable.shape_react_mid;
            bottomDrawable = R.drawable.shape_react_bottom;


            setImageViewDrawableColor(ivTouchTop, topDrawable, topColor, alpha);
            setImageViewDrawableColor(ivTouchMid, midDrawable, midColor, alpha);
            setImageViewDrawableColor(ivTouchBottom, bottomDrawable, bottomColor, alpha);

            if (theme == Configs.TOUCH_UI_THEME_HIDE_LINE_1) {
                containerTouchLinear.setBackgroundResource(R.drawable.bg_linear_line_left_1);
            } else if (theme == Configs.TOUCH_UI_THEME_HIDE_LINE_2) {
                containerTouchLinear.setBackgroundResource(R.drawable.bg_linear_line_left_2);
            } else if (theme == Configs.TOUCH_UI_THEME_HIDE_RECT) {
                containerTouchLinear.setBackgroundResource(R.drawable.bg_linear_rect_left);
            }
        }
    }

    /**
     * 设置自定义颜色的drawable
     *
     * @param iv
     * @param drawableRes
     * @param color
     */
    private void setImageViewDrawableColor(ImageView iv, @DrawableRes int drawableRes, int color, int alpha) {
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(drawableRes, getContext().getTheme());
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int argb = Color.argb(alpha, red, green, blue);

        drawable.setColor(argb);
        iv.setImageDrawable(drawable);
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }


    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        getActivity().unregisterReceiver(receiver);
        super.onDestroyView();
    }

    public void initColorSettingBoardcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Configs.BROADCAST_SHAPE_COLOR_SHETTING);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int pos = intent.getIntExtra(Configs.KEY_SHAPE_COLOR_SETTING, Configs.KEY_SHAPE_COLOR_SETTING_TOP);
                showColorDialog(pos);
            }
        };
        getActivity().registerReceiver(receiver, intentFilter);
    }

    /**
     * 显示颜色选择弹窗
     *
     * @param pos
     */
    public void showColorDialog(int pos) {
        ColorPickerDialog colorPickerDialog = null;
        if (pos == Configs.LinearPos.TOP.getValue()) {
            colorPickerDialog = new ColorPickerDialog(getContext(),
                    "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                @Override
                public void colorChanged(int color) {
                    GradientDrawable drawable = (GradientDrawable) ivTouchTop.getDrawable();
                    drawable.setColor(color);
                    topColor = color;
                    ivTouchTop.setImageDrawable(drawable);
                    SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_COLOR, color);
                    upDateTouchViewShape(0, 0);

                }
            });
        } else if (pos == Configs.LinearPos.MID.getValue()) {
            colorPickerDialog = new ColorPickerDialog(getContext(),
                    "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                @Override
                public void colorChanged(int color) {
                    GradientDrawable drawable = (GradientDrawable) ivTouchMid.getDrawable();
                    drawable.setColor(color);
                    midColor = color;
                    ivTouchMid.setImageDrawable(drawable);
                    SpUtils.saveInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_MID_COLOR, color);
                    upDateTouchViewShape(0, 0);

                }
            });
        } else if (pos == Configs.LinearPos.BOTTOM.getValue()) {
            colorPickerDialog = new ColorPickerDialog(getContext(), bottomColor,
                    "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                @Override
                public void colorChanged(int color) {
                    GradientDrawable drawable = (GradientDrawable) ivTouchBottom.getDrawable();
                    drawable.setColor(color);
                    ivTouchBottom.setImageDrawable(drawable);
                    bottomColor = color;
                    SpUtils.saveInt(getContext().getApplicationContext(), KEY_TOUCH_UI_BOTTOM_COLOR, color);
                    upDateTouchViewShape(0, 0);

                }
            });
        }
        colorPickerDialog.show();
    }


}
