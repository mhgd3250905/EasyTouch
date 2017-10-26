package com.skkk.easytouch;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.skkk.easytouch.Utils.SpUtils;

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
    @Bind(R.id.sb_length)
    AppCompatSeekBar sbLength;
    @Bind(R.id.sb_width)
    AppCompatSeekBar sbWidth;
    @Bind(R.id.sb_vibrate)
    AppCompatSeekBar sbVibrate;



    public static final int ITEM_LENGTH_MIN = 40;

    public static final int ITEM_WIDTH_MIN = 11;

    public static final int VIBRATE_MIN = 0;
    
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_setting);
        ButterKnife.bind(this);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        initEvent();

    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        sbLength.setMax(5);
        sbLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: -->" + (ITEM_LENGTH_MIN + progress * 30) * 3);
                upDateTouchViewShape((ITEM_LENGTH_MIN + progress * 30) * 3, 0);
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, (ITEM_LENGTH_MIN + progress * 30) * 3);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbWidth.setMax(5);
        sbWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: -->" + (ITEM_WIDTH_MIN + progress * 2));
                upDateTouchViewShape(0, (ITEM_WIDTH_MIN + progress * 2));
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, (ITEM_WIDTH_MIN + progress * 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbVibrate.setMax(5);
        sbVibrate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() called with: progress = [" + progress + "]");
                vibrator.vibrate(VIBRATE_MIN + progress * 10);
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL, (VIBRATE_MIN + progress * 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        ViewGroup.LayoutParams layoutParams = llTouchContainer.getLayoutParams();
        if (length != 0) {
            layoutParams.height = dp2px(getApplicationContext(), length);
        }
        if (width != 0) {
            layoutParams.width = dp2px(getApplicationContext(), width);
        }
        llTouchContainer.setLayoutParams(layoutParams);
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }
}
