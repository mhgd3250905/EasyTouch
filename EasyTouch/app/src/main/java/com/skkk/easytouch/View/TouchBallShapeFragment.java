package com.skkk.easytouch.View;


import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.SpUtils;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int radius;
    private int vibrate;
    private int alpha;

    public static final int RADIUS_MIN=15;
    public static final int RADIUS_STEP=5;
    public static final int VIBRATE_MIN = 0;
    public static final int VIBRATE_STEP = 10;
    private Vibrator vibrator;


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

    /**
     * 初始化UI
     */
    private void initUI() {
        radius = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_RADIUS, dp2px(20));
        sbRadius.setProgress((radius - RADIUS_MIN) / RADIUS_STEP);

        vibrate = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_BALL, Configs.DEFAULT_VIBRATE_LEVEL);
        sbVibrate.setProgress((vibrate - VIBRATE_MIN) / VIBRATE_STEP);

        alpha = SpUtils.getInt(getContext().getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_BALL, Configs.DEFAULT_ALPHA);
        sbAlpha.setProgress(alpha);

        upDateTouchViewShape(radius);
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
    }

    /**
     * 更新形状
     *
     * @param radius
     */
    public void upDateTouchViewShape(int radius) {
        ViewGroup.LayoutParams layoutParams = llTouchContainer.getLayoutParams();
        if (radius != 0) {
            layoutParams.width = 2 * dp2px(radius);
            layoutParams.height = 2 * dp2px(radius);
        }
        llTouchContainer.setLayoutParams(layoutParams);

        ivTouchBall.setAlpha((float) alpha/255);
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
