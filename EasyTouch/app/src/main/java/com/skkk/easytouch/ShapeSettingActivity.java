package com.skkk.easytouch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.skkk.easytouch.Services.EasyTouchService;
import com.skkk.easytouch.Utils.ServiceUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.ColorPickerDialog;
import com.skkk.easytouch.View.SettingItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShapeSettingActivity extends AppCompatActivity {

    private static final String TAG = "ShapeSettingActivity";

    @Bind(R.id.tb_shape_settings)
    Toolbar tbShapeSettings;
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


    public static final int ITEM_HEIGHT_MIN = 40;
    public static final int ITEM_HEIGHT_STEP = 30;
    public static final int ITEM_WIDTH_MIN = 11;
    public static final int ITEM_WIDTH_STEP = 3;
    public static final int VIBRATE_MIN = 0;
    public static final int VIBRATE_STEP = 10;
    @Bind(R.id.sivTheme)
    SettingItemView sivTheme;
    @Bind(R.id.sb_alpha)
    AppCompatSeekBar sbAlpha;

    private Vibrator vibrator;
    private int setHeight;
    private int setWidth;
    private int setVibrate;
    private boolean isServiceRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_setting);
        ButterKnife.bind(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initEvent();
        initUI();

        if (ServiceUtils.isServiceRun(getApplicationContext(), "com.skkk.easytouch.Services.EasyTouchService")) {
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
        setHeight = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, Configs.DEFAULT_TOUCH_HEIGHT);
        sbHeight.setProgress(((setHeight / 3) - ITEM_HEIGHT_MIN) / ITEM_HEIGHT_STEP);

        setWidth = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH, Configs.DEFAULT_TOUCH_WIDTH);
        sbWidth.setProgress((setWidth - ITEM_WIDTH_MIN) / ITEM_WIDTH_STEP);

        setVibrate = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL, Configs.DEFAULT_VIBRATE_LEVEL);
        sbVibrate.setProgress((setHeight - VIBRATE_MIN) / VIBRATE_STEP);
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
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, (ITEM_HEIGHT_MIN + progress * ITEM_HEIGHT_STEP) * 3);
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
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH, (ITEM_WIDTH_MIN + progress * ITEM_WIDTH_STEP));
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
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL, (VIBRATE_MIN + progress * 10));
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
                upDateTouchViewShape(0, 0);
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        int topColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_COLOR, Color.BLACK);
        ivTouchTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(ShapeSettingActivity.this,
                        "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        GradientDrawable drawable = (GradientDrawable) ivTouchTop.getDrawable();
                        drawable.setColor(color);
                        ivTouchTop.setImageDrawable(drawable);
                        SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_COLOR, color);
                    }
                });
                colorPickerDialog.show();
            }
        });

        final int midColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_MID_COLOR, Color.BLACK);
        ivTouchMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(ShapeSettingActivity.this, midColor,
                        "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        GradientDrawable drawable = (GradientDrawable) ivTouchMid.getDrawable();
                        drawable.setColor(color);
                        ivTouchMid.setImageDrawable(drawable);
                        SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_MID_COLOR, color);
                    }
                });
                colorPickerDialog.show();
            }
        });

        final int bottomColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_BOTTOM_COLOR, Color.BLACK);
        ivTouchBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog(ShapeSettingActivity.this, bottomColor,
                        "请选择填充颜色", new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color) {
                        GradientDrawable drawable = (GradientDrawable) ivTouchBottom.getDrawable();
                        drawable.setColor(color);
                        ivTouchBottom.setImageDrawable(drawable);
                        SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_BOTTOM_COLOR, color);
                    }
                });
                colorPickerDialog.show();
            }
        });

        sivTheme.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 更新形状
     *
     * @param length
     * @param width
     */
    public void upDateTouchViewShape(int length, int width) {
        if (isServiceRunning) {
            startService(new Intent(ShapeSettingActivity.this, EasyTouchService.class));
        } else {
            ViewGroup.LayoutParams layoutParams = llTouchContainer.getLayoutParams();
            if (length != 0) {
                layoutParams.height = dp2px(getApplicationContext(), length);
            }
            if (width != 0) {
                layoutParams.width = dp2px(getApplicationContext(), width);
            }
            llTouchContainer.setLayoutParams(layoutParams);
        }
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }

}
