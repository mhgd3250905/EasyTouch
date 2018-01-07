package com.skkk.easytouch.Services;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.IntentUtils;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.AppSelect.AppSelectActivity;
import com.skkk.easytouch.View.CircleImageView;
import com.skkk.easytouch.View.FunctionSelect.FuncConfigs;

import static com.skkk.easytouch.Configs.DEFAULT_TOUCH_WIDTH;
import static com.skkk.easytouch.Configs.DEFAULT_VIBRATE_LEVEL;
import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_LEFT;

public class EasyTouchBallService extends EasyTouchBaseService implements View.OnTouchListener {
    private static final String TAG = "EasyTouchBallService";
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mMenuParams;
    private WindowManager.LayoutParams mMenuDetailParams;
    private View touchView;
    private float lastX;
    private float lastY;
    private boolean isMove;

    private RelativeLayout llTouchContainer;
    private GestureDetector ballDetector;
    private float dx;
    private float dy;

    private int touchWidth = DEFAULT_TOUCH_WIDTH;//悬浮条的宽度 单位dp
    private int touchHeight = Configs.DEFAULT_TOUCH_HEIGHT;//悬浮条的高度 单位dp


    private int screenWidth;
    private int screenHeight;
    private int leftBorder;
    private int rightBorder;
    private int direction;
    private int directionX;
    private CircleImageView ivTouchBall;
    private boolean canMove = false;
    private AnimatorSet set;
    private ObjectAnimator scaleXAnim;
    private ObjectAnimator scaleYAnim;
    private Handler handler = new Handler();
    private Runnable longClickRunnable;
    private boolean isRepeat = false;

    private Runnable pressRunnable;
    private boolean isMenuShow = false;
    private boolean isMenuDetailShow = false;
    private View menuView;
    private View menuDetailView;
    private RelativeLayout menuContainer;
    //    private ImageView ivMenuBall0, ivMenuBall1, ivMenuBall2, ivMenuBall3, ivMenuBall4;
    private ObjectAnimator transXAnimShow;
    private ObjectAnimator transXAnimHide;
    private ObjectAnimator transYAnimShow;
    private ObjectAnimator transYAnimHide;
    private ObjectAnimator menuBallScaleXAnim;
    private ObjectAnimator menuBallScaleYAnim;
    private AnimatorSet ballMenuAnimSet;
    private ObjectAnimator menuBallAlphAnim;
    private SeekBar sbSystemAudio;
    private SeekBar sbMediaAudio;
    private ImageView ivAudioSystem;
    private ImageView ivAudioMedia;
    private ImageView ivAudioAlarm;
    private TextView tvAudioMode;
    private SeekBar sbAlarmAudio;
    private Switch switchMode;

    private RelativeLayout containerMenuDetailVoice;
    private LinearLayout containerMenuDetailVoiceContent;
    private RelativeLayout containerMenuDetailVoiceBack;
    private ImageView ivMenuDetailVoiceBack;

    private RelativeLayout containerMenuDetailPay;
    private LinearLayout containerMenuDetailPayContent;
    private RelativeLayout containerMenuDetailPayBack;
    private ImageView ivMenuDetailPayBack;

    private RelativeLayout containerMenuDetailApps;
    private LinearLayout containerMenuDetailAppsContent;
    private RelativeLayout containerMenuDetailAppsBack;
    private ImageView ivMenuDetailAppBack;
    private LinearLayout containerMenuDetailAppsTop;
    private LinearLayout containerMenuDetailAppsBottom;

    private ImageView ivAlipayScan;
    private ImageView ivAlipayPay;
    private ImageView ivWeixinScan;
    private ImageView ivMenuDetailBack;
    private ObjectAnimator hideMenuDetailAnim;
    private final int HIDE_MENU_DETAIL_FAST = 100;
    private final int HIDE_MENU_DETAIL_SLOW = 200;
    private ObjectAnimator touchBallScaleXAnim;
    private ObjectAnimator touchBallScaleYAnim;
    private int menuWidth;
    private float touchAlpha;
    private String drawableName;
    private boolean isScaleAnim = false;


    @Override
    public void onCreate() {
        super.onCreate();

        //设置音量管理器
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //点击后定时取消按钮的显示效果
        pressRunnable = new Runnable() {
            @Override
            public void run() {
                hideTouchBall();
            }
        };

        longClickRunnable = new Runnable() {
            @Override
            public void run() {
                scaleXAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleX", 1f, 1.3f, 1f);
                scaleYAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleY", 1f, 1.3f, 1f);
                set = new AnimatorSet();
                set.play(scaleXAnim).with(scaleYAnim);
                set.setDuration(300);
                final Runnable runnable = this;
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (isRepeat) {
                            handler.postDelayed(runnable, 300);
                        }
                    }
                });
                set.start();
            }
        };

        if (windowManager == null) {
            windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }

        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        leftBorder = 0;
        rightBorder = screenWidth;

        menuWidth = dp2px(200f);

        //设置悬浮窗的LP
        mParams = new WindowManager.LayoutParams();
        mParams.packageName = getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        //设置一级菜单的LP
        mMenuParams = new WindowManager.LayoutParams();
        mMenuParams.packageName = getPackageName();
        mMenuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mMenuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mMenuParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mMenuParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mMenuParams.format = PixelFormat.RGBA_8888;
        mMenuParams.gravity = Gravity.LEFT | Gravity.TOP;

        //设置二级菜单的LP
        mMenuDetailParams = new WindowManager.LayoutParams();
        mMenuDetailParams.packageName = getPackageName();
        mMenuDetailParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mMenuDetailParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mMenuDetailParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mMenuDetailParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mMenuDetailParams.format = PixelFormat.RGBA_8888;
        mMenuDetailParams.gravity = Gravity.LEFT | Gravity.TOP;

        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        if (direction == TOUCH_UI_DIRECTION_LEFT) {
            directionX = leftBorder;
        } else {
            directionX = rightBorder;
        }

        mParams.x = directionX;
        mParams.y = screenHeight - menuWidth;

        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        if (direction == Configs.Position.LEFT.getValue()) {
            directionX = leftBorder;
        } else if (direction == Configs.Position.RIGHT.getValue()) {
            directionX = rightBorder;
        }

        mMenuParams.x = directionX;
        mMenuParams.y = screenHeight - menuWidth;

        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball, null);
        llTouchContainer = (RelativeLayout) touchView.findViewById(R.id.ll_touch_container);
        ivTouchBall = (CircleImageView) touchView.findViewById(R.id.ivTouchBall);

        menuView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball_menu, null);
        menuContainer = (RelativeLayout) menuView.findViewById(R.id.container_menu_ball);
//        ivMenuBall0 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_0);
//        ivMenuBall1 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_1);
//        ivMenuBall2 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_2);
//        ivMenuBall3 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_3);
//        ivMenuBall4 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_4);


        menuDetailView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball_menu_detail, null);

        containerMenuDetailVoice = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_voice);
        sbSystemAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_system_audio);
        sbMediaAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_media_audio);
        sbAlarmAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_alarm_audio);
        ivAudioSystem = (ImageView) menuDetailView.findViewById(R.id.iv_audio_system);
        ivAudioMedia = (ImageView) menuDetailView.findViewById(R.id.iv_audio_media);
        ivAudioAlarm = (ImageView) menuDetailView.findViewById(R.id.iv_audio_alarm);
        switchMode = (Switch) menuDetailView.findViewById(R.id.switch_mode);
        tvAudioMode = (TextView) menuDetailView.findViewById(R.id.tv_audio_mode);
        containerMenuDetailVoiceContent = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_voice_content);
        containerMenuDetailVoiceBack = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_voice_back);
        ivMenuDetailVoiceBack = (ImageView) menuDetailView.findViewById(R.id.iv_menu_detail_voice_back);

        containerMenuDetailPay = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_pay);
        ivAlipayScan = (ImageView) menuDetailView.findViewById(R.id.iv_scan_alipay);
        ivAlipayPay = (ImageView) menuDetailView.findViewById(R.id.iv_pay_alipay);
        ivWeixinScan = (ImageView) menuDetailView.findViewById(R.id.iv_scan_weixin);
        containerMenuDetailPayContent = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_pay_content);
        containerMenuDetailPayBack = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_pay_back);
        ivMenuDetailPayBack = (ImageView) menuDetailView.findViewById(R.id.iv_menu_detail_pay_back);


        containerMenuDetailApps = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_apps);
        ivMenuDetailBack = (ImageView) menuDetailView.findViewById(R.id.iv_menu_detail_back);

        containerMenuDetailAppsBack = (RelativeLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_app_back);
        ivMenuDetailAppBack = (ImageView) menuDetailView.findViewById(R.id.iv_menu_detail_app_back);
        containerMenuDetailAppsContent = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_app_content);

        containerMenuDetailAppsTop = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_app_top);
        containerMenuDetailAppsBottom = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_app_bottom);

        windowManager.addView(touchView, mParams);
    }


    /**
     * 设置悬浮窗在左边的时候的布局
     *
     * @param v
     */
    private void setMenuBallLeftLayoutParams(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.ALIGN_PARENT_LEFT);
        v.setLayoutParams(lp);
    }

    /**
     * 设置悬浮窗在右边的时候的布局
     *
     * @param v
     */
    private void setMenuBallRightLayoutParams(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.ALIGN_PARENT_RIGHT);

        v.setLayoutParams(lp);
    }

    /**
     * 设置悬浮窗在左边的时候的Detail布局
     *
     * @param v
     */
    private void setMenuBallDetailAlignStartLayoutParams(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_PARENT_END);
        lp.addRule(RelativeLayout.ALIGN_PARENT_START);
        v.setLayoutParams(lp);
    }

    /**
     * 设置悬浮窗在右边的时候的Detail布局
     *
     * @param v
     */
    private void setMenuBallDetailAlignEndLayoutParams(View v) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) v.getLayoutParams();
        lp.removeRule(RelativeLayout.ALIGN_PARENT_START);
        lp.addRule(RelativeLayout.ALIGN_PARENT_END);
        v.setLayoutParams(lp);
    }

    /**
     * 根据横竖屏幕切换悬浮球对应的位置
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //横屏
                // 1.获取当前的位置
                if (isMenuDetailShow) {
                    mMenuDetailParams.y = mMenuDetailParams.y * screenWidth / screenHeight;
                    windowManager.updateViewLayout(menuDetailView, mMenuDetailParams);
                } else if (isMenuShow) {
                    mMenuParams.y = mMenuParams.y * screenWidth / screenHeight;
                    windowManager.updateViewLayout(menuView, mMenuParams);
                } else {


                    mParams.y = mParams.y * screenWidth / screenHeight;
                    windowManager.updateViewLayout(touchView, mParams);
                }

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                //竖屏
                if (isMenuDetailShow) {
                    mMenuDetailParams.y = mMenuDetailParams.y * screenHeight / screenWidth;
                    windowManager.updateViewLayout(menuDetailView, mMenuDetailParams);
                } else if (isMenuShow) {
                    mMenuParams.y = mMenuParams.y * screenHeight / screenWidth;
                    windowManager.updateViewLayout(menuView, mMenuParams);
                } else {
                    mParams.y = mParams.y * screenHeight / screenWidth;
                    windowManager.updateViewLayout(touchView, mParams);
                }

            }
        } catch (Exception ex) {

        }

    }

    /**
     * 设置触摸块UI
     */
    private void initTouchUI() {
        //初始化震动等级
        vibrateLevel = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL_BALL, DEFAULT_VIBRATE_LEVEL);
        touchWidth = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_RADIUS, DEFAULT_TOUCH_WIDTH);
        touchAlpha = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA_BALL, Configs.DEFAULT_ALPHA) * 1f;
        drawableName = SpUtils.getString(getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL, "ball_0");

        ViewGroup.LayoutParams containerLp = llTouchContainer.getLayoutParams();
        containerLp.width = 2 * dp2px(touchWidth);
        containerLp.height = 2 * dp2px(touchWidth);
        llTouchContainer.setLayoutParams(containerLp);
        ivTouchBall.setAlpha(touchAlpha / 255f);

        if (drawableName.equals(Configs.KEY_PHOTO_CUSTOM_DRAWABLE)) {
            ivTouchBall.setImageURI(Uri.parse(SpUtils.getString(getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL_CUSTOM, "ball_0")));
        } else {
            ivTouchBall.setImageResource(PackageUtils.getResource(getApplicationContext(), drawableName));
        }

        //添加菜单元素
        initMenuBalls();
        //设置是件监听
        initEvent();

        //如果二级菜单打开
        if (isMenuDetailShow) {
            windowManager.updateViewLayout(menuDetailView, mMenuDetailParams);
        } else if (isMenuShow) {//如果一级菜单打开
            windowManager.updateViewLayout(menuView, mMenuParams);
        } else {//如果没有菜单打开
            windowManager.updateViewLayout(touchView, mParams);
        }
    }

    /**
     * 添加菜单元素
     */
    private void initMenuBalls() {
        //先清除已经存在的Ball
        if (menuContainer.getChildCount() > 1) {
            for (int i = menuContainer.getChildCount(); i > 0; i--) {
                menuContainer.removeView(menuContainer.getChildAt(i));
            }
        }
        //根据设置添加Ball
        int menuBallCount = SpUtils.getInt(getApplicationContext(), SpUtils.KEY_MENU_BALL_COUNT, 0);
        if (menuBallCount > 0) {
            for (int i = 0; i < menuBallCount; i++) {
                CircleImageView ivBall = new CircleImageView(this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dp2px(40), dp2px(40));
                lp.addRule(RelativeLayout.CENTER_VERTICAL);
                ivBall.setLayoutParams(lp);
                initMenuBallDrawable(ivBall, SpUtils.getInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MENU_BALL + i, FuncConfigs.Func.VOICE_MENU.getValue()));
                final int finalI = i;
                ivBall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goMenuBallEvent(FuncConfigs.VALUE_FUNC_OP_MENU_BALL + finalI, finalI);
                    }
                });
                menuContainer.addView(ivBall);
            }
        }
    }

    /**
     * 设置菜单元素的图标的图形
     *
     * @param ivBall
     * @param funcType
     */
    private void initMenuBallDrawable(ImageView ivBall, int funcType) {
        int drawableId = R.drawable.vector_ball_menu_voice;
        if (funcType == FuncConfigs.Func.BACK.getValue()) {
            drawableId = R.drawable.vector_drawable_back;
        } else if (funcType == FuncConfigs.Func.HOME.getValue()) {
            drawableId = R.drawable.vector_drawable_home;
        } else if (funcType == FuncConfigs.Func.RECENT.getValue()) {
            drawableId = R.drawable.vector_drawable_recent;
        } else if (funcType == FuncConfigs.Func.NOTIFICATION.getValue()) {
            drawableId = R.drawable.vector_drawable_notification;
        } else if (funcType == FuncConfigs.Func.PREVIOUS_APP.getValue()) {
            drawableId = R.drawable.vector_drawable_previous;
        } else if (funcType == FuncConfigs.Func.TRUN_POS.getValue()) {
            drawableId = R.drawable.vector_drawable_trun_pos;
        } else if (funcType == FuncConfigs.Func.LOCK_SCREEN.getValue()) {
            drawableId = R.drawable.vector_drawable_screen_lock;
        } else if (funcType == FuncConfigs.Func.VOICE_MENU.getValue()) {
            drawableId = R.drawable.vector_ball_menu_voice;
        } else if (funcType == FuncConfigs.Func.PAY_MENU.getValue()) {
            drawableId = R.drawable.vector_drawable_pay_menu;
        } else if (funcType == FuncConfigs.Func.APP_MENU.getValue()) {
            drawableId = R.drawable.vector_ball_menu_apps;
        } else if (funcType == FuncConfigs.Func.APPS.getValue()) {
            drawableId = R.drawable.vector_ball_menu_apps;
        }
        ivBall.setImageResource(drawableId);
    }


    /**
     * 设置事件
     */
    private void initEvent() {
        initTouchBallEvent();
        initMenuEvent();
        initMenuDetailVoiceEvent();
        initMneuDetailPayEvent();
        initMenuDetailAppEvent();
    }


    /**
     * 设置声音设置详情菜单事件
     */
    private void initMenuDetailVoiceEvent() {
        containerMenuDetailVoice.post(new Runnable() {
            @Override
            public void run() {
                containerMenuDetailVoiceBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMenuDetailEnterAnim(menuDetailView, HIDE_MENU_DETAIL_SLOW, new Configs.OnAnimEndListener() {
                            @Override
                            public void onAnimEnd() {
                                hideMenuDetailContainer();
                            }
                        }, true);
                    }
                });
            }
        });
        //设置静音模式切换监听
        switchMode.post(new Runnable() {
            @Override
            public void run() {
                switchMode.setChecked(audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT);
                switchMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            tvAudioMode.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlackBody));
                        } else {
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            tvAudioMode.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sbSystemAudio.getProgress(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sbMediaAudio.getProgress(), 0);
                            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, sbAlarmAudio.getProgress(), 0);
                        }
                    }
                });
            }
        });

        //设置系统音量
        sbSystemAudio.post(new Runnable() {
            @Override
            public void run() {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
                sbSystemAudio.setMax(maxVolume);
                sbSystemAudio.setProgress(curVolume);
                //设置音量控制变化监听
                sbSystemAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        //如果在静音模式下拖动不实际设置音量
                        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT) {
                            audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });

        //闹钟音量调节
        sbAlarmAudio.post(new Runnable() {
            @Override
            public void run() {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                sbAlarmAudio.setMax(maxVolume);
                sbAlarmAudio.setProgress(curVolume);
                //设置音量控制变化监听
                sbAlarmAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });

        //媒体音量调节
        sbMediaAudio.post(new Runnable() {
            @Override
            public void run() {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                sbMediaAudio.setMax(maxVolume);
                sbMediaAudio.setProgress(curVolume);
                //设置音量控制变化监听
                sbMediaAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });


    }

    /**
     * 设置APP点击事件
     */
    private void initMenuDetailAppEvent() {
        containerMenuDetailApps.post(new Runnable() {
            @Override
            public void run() {
                containerMenuDetailAppsBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMenuDetailEnterAnim(menuDetailView, HIDE_MENU_DETAIL_SLOW, new Configs.OnAnimEndListener() {
                            @Override
                            public void onAnimEnd() {
                                hideMenuDetailContainer();
                            }
                        }, true);
                    }
                });
            }
        });
        for (int i = 0; i <= 4; i++) {
            ImageView ivApp = (ImageView) containerMenuDetailAppsTop.getChildAt(i);
            String shortCutStr = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_TOP_APPS_ + i, "");
            final int finalIndex = i;
            if (TextUtils.isEmpty(shortCutStr)) {
                ivApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSelectAppActivity(finalIndex, Configs.AppType.APP.getValue());
                    }
                });

            } else {
                final ResolveInfo appInfo = new Gson().fromJson(shortCutStr, ResolveInfo.class);
                if (appInfo != null) {
                    ivApp.setImageDrawable(PackageUtils.getInstance(getApplicationContext()).getShortCutIcon(appInfo));
                    ivApp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMenuDetailEnterAnim(menuDetailView, HIDE_MENU_DETAIL_FAST, new Configs.OnAnimEndListener() {
                                @Override
                                public void onAnimEnd() {
                                    hideMenuDetailContainer();
                                }
                            }, false);
                            PackageUtils.getInstance(getApplicationContext()).startAppActivity(appInfo);
                        }
                    });
                    ivApp.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            startSelectAppActivity(finalIndex, Configs.AppType.APP.getValue());
                            return true;
                        }
                    });
                }
            }
        }

        for (int i = 0; i <= 4; i++) {
            ImageView ivApp = (ImageView) containerMenuDetailAppsBottom.getChildAt(i);
            String shortCutStr = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_BOTTOM_APPS_ + i, "");
            final int finalIndex = i;
            if (TextUtils.isEmpty(shortCutStr)) {
                ivApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSelectAppActivity(finalIndex, Configs.AppType.SHORTCUT.getValue());
                    }
                });
            } else {
                final ResolveInfo appInfo = new Gson().fromJson(shortCutStr, ResolveInfo.class);
                if (appInfo != null) {
                    ivApp.setImageDrawable(PackageUtils.getInstance(getApplicationContext()).getShortCutIcon(appInfo));
                    ivApp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            hideMenuDetailEnterAnim(menuDetailView, HIDE_MENU_DETAIL_FAST, new Configs.OnAnimEndListener() {
                                @Override
                                public void onAnimEnd() {
                                    hideMenuDetailContainer();
                                }
                            }, false);
                            PackageUtils.getInstance(getApplicationContext()).startAppActivity(appInfo);
                        }
                    });
                    ivApp.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            startSelectAppActivity(finalIndex, Configs.AppType.SHORTCUT.getValue());
                            return true;
                        }
                    });
                }
            }
        }
    }

    /**
     * 设置支付点击事件
     */
    private void initMneuDetailPayEvent() {
        /*
        * 设置支付事件监听
        * */
        containerMenuDetailPay.post(new Runnable() {
            @Override
            public void run() {
                containerMenuDetailPayBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideMenuDetailEnterAnim(menuDetailView, HIDE_MENU_DETAIL_SLOW, new Configs.OnAnimEndListener() {
                            @Override
                            public void onAnimEnd() {
                                hideMenuDetailContainer();
                            }
                        }, false);
                    }
                });
            }
        });
        ivAlipayScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.toAliPayScan(getApplicationContext());
            }
        });

        ivAlipayPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.toAliPayCode(getApplicationContext());
            }
        });

        ivWeixinScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.toWeChatScanDirect(getApplicationContext());
            }
        });


    }

    /**
     * 打开App选择界面
     *
     * @param finalIndex
     * @param value
     */
    private void startSelectAppActivity(int finalIndex, int value) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), AppSelectActivity.class);
        intent.putExtra(Configs.KEY_APP_TYPE, value);
        intent.putExtra(Configs.KEY_BALL_MENU_SELECT_APP_INDEX, finalIndex);
        startActivity(intent);
    }


    /**
     * 设置悬浮球事件
     */
    private void initTouchBallEvent() {
        ivTouchBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivTouchBall.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG, "onLongClick: ");
                vibrator.vibrate(vibrateLevel);
                if (!canMove) {
                    canMove = true;
                    if (!isMenuShow) {
                        showMenuContainer();
                    }
                }
                return false;
            }
        });


        ivTouchBall.setOnTouchListener(this);

        ballDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.i(TAG, "onDown: ");
                //记录move down坐标
                showTouchBall();
                setMoveDownXY(e);
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.i(TAG, "onShowPress: ");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.i(TAG, "onSingleTapUp: ");
                //震动30毫秒
                if (!isMenuShow) {
                    showTouchBall();
                    goOpEvent(FuncConfigs.VALUE_FUNC_OP_CLICK);
                }
                showTouchBallClickAnim();
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (canMove) {
                    //设定为只有上下滑动的时候才可以进行位置调整
                    showTouchBall();
                    refreshMovePlace(e2);
                    if (isMenuShow) {
                        isMenuShow = false;
                        hideMenuContainer(-1, null);
                    }
                }

                return false;
            }

            @Override
            public void onLongPress(final MotionEvent e) {
                Log.i(TAG, "onLongPress: ");

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.i(TAG, "onFling: ");
                if (e2.getX() - e1.getX() > 10 && Math.abs(e1.getY() - e2.getY()) < (Math.abs(e1.getX() - e2.getX()) / 2)) {
                    if (!canMove) {//右划
                        if (direction == Configs.Position.LEFT.getValue()) {
                            goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT);
//                            enterRecents();//任务列表
                        } else if (direction == Configs.Position.RIGHT.getValue()) {
//                            jump2LastApp();//进入上一个应用
                            goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_LEFT);

                        }
                    }
                    showTouchBallFlingAnim(Configs.TouchDirection.RIGHT);
                } else if (e1.getX() - e2.getX() > 10 && Math.abs(e1.getY() - e2.getY()) < (Math.abs(e1.getX() - e2.getX()) / 2)) {
                    if (!canMove) {//左滑
                        if (direction == Configs.Position.LEFT.getValue()) {
//                            jump2LastApp();//进入上一个应用
                            goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_LEFT);

                        } else if (direction == Configs.Position.RIGHT.getValue()) {
//                            enterRecents();//任务列表
                            goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT);

                        }
                    }
                    showTouchBallFlingAnim(Configs.TouchDirection.LEFT);
                } else if (e1.getY() - e2.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > (Math.abs(e1.getX() - e2.getX()) * 2)) {
                    //上滑
                    if (!canMove) {
                        goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_UP);
                    }
                    showTouchBallFlingAnim(Configs.TouchDirection.UP);

                } else if (e2.getY() - e1.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > (Math.abs(e1.getX() - e2.getX()) * 2)) {
                    //下滑
                    if (!canMove) {
                        goOpEvent(FuncConfigs.VALUE_FUNC_OP_FLING_BOTTOM);
                    }
                    showTouchBallFlingAnim(Configs.TouchDirection.DOWN);

                }

                return false;
            }
        });

        ballDetector.setIsLongpressEnabled(false);

    }

    /**
     * 切换点击动画缩放状态
     */
    private void showTouchBallClickAnim() {
        ObjectAnimator scaleXShowAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleYShowAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleY", 1f, 0.5f);
        AnimatorSet scaleShowSet = new AnimatorSet();
        scaleShowSet.play(scaleXShowAnim).with(scaleYShowAnim);
        scaleShowSet.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleShowSet.setDuration(100);

        ObjectAnimator scaleXHideAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleYHideAnim = ObjectAnimator.ofFloat(ivTouchBall, "scaleY", 0.5f, 1f);
        final AnimatorSet scaleHideSet = new AnimatorSet();
        scaleHideSet.play(scaleXHideAnim).with(scaleYHideAnim);
        scaleHideSet.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleHideSet.setDuration(100);

        scaleShowSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                scaleHideSet.start();
            }
        });
        scaleShowSet.start();

    }

    /**
     * 手势滑动动画缩放状态
     */
    private void showTouchBallFlingAnim(Configs.TouchDirection flingDirection) {
        ScaleAnimation scaleAnimation = null;
        float fromX = 0, toX = 0, fromY = 0, toY = 0;
        float pivotX = 0, pivotY = 0;
        if (flingDirection.equals(Configs.TouchDirection.UP)) {
            fromX = 1f;
            toX = 1f;
            fromY = 1f;
            toY = 0.5f;
            pivotX = 0.5f;
            pivotY = 0f;
        } else if (flingDirection.equals(Configs.TouchDirection.LEFT)) {
            fromX = 1f;
            toX = 0.5f;
            fromY = 1f;
            toY = 1f;
            pivotX = 0f;
            pivotY = 0.5f;
        } else if (flingDirection.equals(Configs.TouchDirection.DOWN)) {
            fromX = 1f;
            toX = 1f;
            fromY = 1f;
            toY = 0.5f;
            pivotX = 0.5f;
            pivotY = 1f;
        } else if (flingDirection.equals(Configs.TouchDirection.RIGHT)) {
            fromX = 1f;
            toX = 0.5f;
            fromY = 1f;
            toY = 1f;
            pivotX = 1f;
            pivotY = 0.5f;
        }
        scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        scaleAnimation.setDuration(100);
        final float finalToX = toX;
        final float finalFromX = fromX;
        final float finalToY = toY;
        final float finalFromY = fromY;
        final float finalPivotX = pivotX;
        final float finalPivotY = pivotY;
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(finalToX, finalFromX, finalToY, finalFromY, Animation.RELATIVE_TO_SELF, finalPivotX, Animation.RELATIVE_TO_SELF, finalPivotY);
                scaleAnimation.setDuration(100);
                ivTouchBall.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivTouchBall.startAnimation(scaleAnimation);


    }


    /**
     * 设置一级菜单事件
     */
    private void initMenuEvent() {
        menuContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideMenuContainer(-1, null);
                return false;
            }
        });

//        /*
//        * 点击切换左右位置
//        * */
//        ivMenuBall0.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //隐藏菜单按钮动画
//                hideMenuContainer(-1, new Configs.OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        //切换左右位置
//                        switchTouchPos();
//                    }
//                });
//
//            }
//        });
//
//
//        ivMenuBall1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //隐藏菜单按钮动画
//                hideMenuContainer(1, new Configs.OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        //显示声音二级菜单
//                        showVoiceAdjustView();
//                    }
//                });
//                initMenuDetailVoiceEvent();
//            }
//        });
//
//        ivMenuBall2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideMenuContainer(2, new Configs.OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        //显示快捷App选择界面
//                        showAppsSelectView();
//                    }
//                });
//                initMenuDetailAppEvent();
//            }
//        });
//
//        ivMenuBall3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideMenuContainer(3, new Configs.OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        //选择快捷支付界面
//                        showPaySelectView();
//                    }
//                });
//                initMneuDetailPayEvent();
//            }
//        });
//
//        ivMenuBall4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideMenuContainer(-1, new Configs.OnAnimEndListener() {
//                    @Override
//                    public void onAnimEnd() {
//                        //显示锁屏界面
//                        showLockScreenView();
//                    }
//                });
//            }
//        });
    }


    /**
     * 显示详细菜单-声音设置
     */
    private void showMenuDetailVoice() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(80);

        windowManager.addView(menuDetailView, mMenuDetailParams);
        menuDetailView.post(new Runnable() {
            @Override
            public void run() {

                if (direction == Configs.Position.LEFT.getValue()) {
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailVoiceContent);
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailVoiceBack);
                    ivMenuDetailVoiceBack.setImageResource(R.drawable.ic_arrow_left);
                } else if (direction == Configs.Position.RIGHT.getValue()) {
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailVoiceContent);
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailVoiceBack);
                    ivMenuDetailVoiceBack.setImageResource(R.drawable.ic_arrwo_right);

                }
                enterMenuDetailAnim(menuDetailView, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        containerMenuDetailVoice.setAlpha(1f);
                        containerMenuDetailVoice.setVisibility(View.VISIBLE);
                        containerMenuDetailApps.setVisibility(View.GONE);
                        containerMenuDetailPay.setVisibility(View.GONE);
                    }
                });
                isMenuDetailShow = true;
            }
        });
    }

    /**
     * 二级菜单的进入动画
     *
     * @param containerMenuDetail
     */
    private void enterMenuDetailAnim(View containerMenuDetail, AnimatorListenerAdapter animatorListenerAdapter) {
        ObjectAnimator enterMenuDetailAnim = null;
        if (direction == Configs.Position.LEFT.getValue()) {
            enterMenuDetailAnim = ObjectAnimator.ofFloat(containerMenuDetail, "translationX", dp2px(-300f), 0);
        } else if (direction == Configs.Position.RIGHT.getValue()) {
            enterMenuDetailAnim = ObjectAnimator.ofFloat(containerMenuDetail, "translationX", dp2px(300f), 0);
        }
        if (enterMenuDetailAnim != null) {
            enterMenuDetailAnim.addListener(animatorListenerAdapter);
            enterMenuDetailAnim.start();
        }
    }

    /**
     * 二级菜单的退出动画
     *
     * @param containerMenuDetail
     */
    private void hideMenuDetailEnterAnim(View containerMenuDetail, int duration, final Configs.OnAnimEndListener onAnimEndListener, boolean isAppsMenu) {
        int transFromX = 0;
        int transToX = dp2px(-200);
        if (direction == Configs.Position.LEFT.getValue()) {
            transFromX = 0;
            transToX = dp2px(-200);
            if (isAppsMenu) {
                transToX = dp2px(-300f);
            }
        } else if (direction == Configs.Position.RIGHT.getValue()) {
            transFromX = 0;
            transToX = dp2px(200f);
            if (isAppsMenu) {
                transToX = dp2px(300f);
            }
        }

        hideMenuDetailAnim = ObjectAnimator.ofFloat(containerMenuDetail, "translationX", transFromX, transToX);
        hideMenuDetailAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimEndListener != null) {
                    onAnimEndListener.onAnimEnd();
                }
            }
        });
        hideMenuDetailAnim.setDuration(duration);
        hideMenuDetailAnim.start();
    }

    /**
     * 显示详细菜单-支付设置
     */
    private void showMenuDetailPay() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(80);

        windowManager.addView(menuDetailView, mMenuDetailParams);
        containerMenuDetailPay.post(new Runnable() {
            @Override
            public void run() {
                //显示二级菜单
                containerMenuDetailVoice.setVisibility(View.GONE);
                containerMenuDetailApps.setVisibility(View.GONE);
                containerMenuDetailPay.setVisibility(View.VISIBLE);
                if (direction == Configs.Position.LEFT.getValue()) {
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailPayContent);
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailPayBack);
                    ivMenuDetailPayBack.setImageResource(R.drawable.ic_arrow_left);
                } else if (direction == Configs.Position.RIGHT.getValue()) {
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailPayContent);
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailPayBack);
                    ivMenuDetailPayBack.setImageResource(R.drawable.ic_arrwo_right);

                }
                enterMenuDetailAnim(menuDetailView, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        containerMenuDetailPay.setAlpha(1f);
                        containerMenuDetailPay.setVisibility(View.VISIBLE);
                        containerMenuDetailApps.setVisibility(View.GONE);
                        containerMenuDetailVoice.setVisibility(View.GONE);
                    }
                });
                isMenuDetailShow = true;
            }
        });
    }

    /**
     * 显示详细菜单-App设置
     */
    private void showMenuDetailApp() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(40);

        windowManager.addView(menuDetailView, mMenuDetailParams);
        containerMenuDetailApps.post(new Runnable() {
            @Override
            public void run() {
                //显示二级菜单
                containerMenuDetailVoice.setVisibility(View.GONE);
                containerMenuDetailApps.setVisibility(View.VISIBLE);
                containerMenuDetailPay.setVisibility(View.GONE);
                if (direction == Configs.Position.LEFT.getValue()) {
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailAppsContent);
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailAppsBack);
                    ivMenuDetailAppBack.setImageResource(R.drawable.ic_arrow_left);

                } else if (direction == Configs.Position.RIGHT.getValue()) {
                    setMenuBallDetailAlignEndLayoutParams(containerMenuDetailAppsContent);
                    setMenuBallDetailAlignStartLayoutParams(containerMenuDetailAppsBack);
                    ivMenuDetailAppBack.setImageResource(R.drawable.ic_arrwo_right);

                }
                enterMenuDetailAnim(menuDetailView, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        containerMenuDetailApps.setAlpha(1f);
                        containerMenuDetailApps.setVisibility(View.VISIBLE);
                        containerMenuDetailPay.setVisibility(View.GONE);
                        containerMenuDetailVoice.setVisibility(View.GONE);
                    }
                });
                isMenuDetailShow = true;
            }
        });
    }


    /**
     * 显示隐藏菜单
     */
    private void showMenuContainer() {
        int menuBallCount = SpUtils.getInt(getApplicationContext(), SpUtils.KEY_MENU_BALL_COUNT, 0);

        if (menuBallCount>0) {
            mMenuParams.x = mParams.x;
            mMenuParams.y = mParams.y - menuWidth / 2 + dp2px(touchWidth);
            windowManager.addView(menuView, mMenuParams);
            isMenuShow = true;
            menuView.post(new Runnable() {
                @Override
                public void run() {
                    showMenuBallAnim(menuContainer.getChildAt(0), 0);
                    showMenuBallAnim(menuContainer.getChildAt(1), 1);
                    showMenuBallAnim(menuContainer.getChildAt(2), 2);
                    showMenuBallAnim(menuContainer.getChildAt(3), 3);
                    showMenuBallAnim(menuContainer.getChildAt(4), 4);
                }
            });
        }
    }

    /**
     * 针对不同的位置的菜单按钮进行不同的动画弹出方式
     *
     * @param view
     * @param index
     */
    private void showMenuBallAnim(View view, int index) {
        ballMenuAnimSet = new AnimatorSet();
        int transXFrom = 0;
        int transYFrom = 0;
        int transXTo = 0;
        int transYTo = 0;

        int count = menuContainer.getChildCount();

        if (direction == Configs.Position.LEFT.getValue()) {
            transXFrom = 0;
            transYFrom = 0;
            double radius = -(Math.PI / 2) + index * Math.PI / (count - 1);
            transXTo = transXFrom + dp2px((float) (80 * Math.cos(radius)));
            transYTo = transYFrom + dp2px((float) (80 * Math.sin(radius)));
        } else if (direction == Configs.Position.RIGHT.getValue()) {
            transXFrom = menuWidth;
            transYFrom = 0;
            double radius = -(Math.PI / 2) + index * Math.PI / (count - 1);
            transXTo = transXFrom - dp2px(40) - dp2px((float) (80 * Math.cos(radius)));
            transYTo = transYFrom + dp2px((float) (80 * Math.sin(radius)));
        }

        transXAnimShow = ObjectAnimator.ofFloat(view, "translationX", transXFrom, transXTo);
        transYAnimShow = ObjectAnimator.ofFloat(view, "translationY", transYFrom, transYTo);

        ballMenuAnimSet.play(transXAnimShow).with(transYAnimShow);
        ballMenuAnimSet.setDuration(200);
        ballMenuAnimSet.start();

    }

    /**
     * 隐藏菜单
     */
    private void hideMenuContainer(final int index, final Configs.OnAnimEndListener onAnimEndListener) {
        menuView.post(new Runnable() {
            @Override
            public void run() {
                if (index != -1) {
                    hideMenuBallAnim(ivTouchBall, -1, false, null);
                }
                hideMenuBallAnim(menuContainer.getChildAt(0), 0, index == 0, null);
                hideMenuBallAnim(menuContainer.getChildAt(1), 1, index == 1, null);
                hideMenuBallAnim(menuContainer.getChildAt(2), 2, index == 2, null);
                hideMenuBallAnim(menuContainer.getChildAt(3), 3, index == 3, null);
                hideMenuBallAnim(menuContainer.getChildAt(4), 4, index == 4, onAnimEndListener);
            }
        });

    }

    /**
     * 根据不同位置的菜单按钮进行动画执行
     *
     * @param view
     * @param index
     */
    private void hideMenuBallAnim(final View view, int index, boolean isSelected, final Configs.OnAnimEndListener onAnimEndListener) {

        ballMenuAnimSet = new AnimatorSet();
        float transXFrom = 0;
        float transYFrom = 0;
        float transXTo = 0;
        float transYTo = 0;

        float scaleXFrom = 0;
        float scaleYFrom = 0;
        float scaleXTo = 0;
        float scaleYTo = 0;
        float alphFrom = 0;
        float alphTo = 0;

        if (isSelected) {
            scaleXFrom = 1f;
            scaleYFrom = 1f;
            scaleXTo = 1.5f;
            scaleYTo = 1.5f;
        } else {
            scaleXFrom = 1f;
            scaleYFrom = 1f;
            scaleXTo = 0f;
            scaleYTo = 0f;
        }
        alphFrom = 1f;
        alphTo = 0.2f;


        menuBallScaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", scaleXFrom, scaleXTo);
        menuBallScaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", scaleYFrom, scaleYTo);
        menuBallAlphAnim = ObjectAnimator.ofFloat(view, "alpha", alphFrom, alphTo);

        final int count = menuContainer.getChildCount();

        transXTo = 0;
        transYTo = 0;

        if (direction == Configs.Position.LEFT.getValue()) {
            double radius = -(Math.PI / 2) + index * Math.PI / (count - 1);
            transXFrom = 0 + dp2px((float) (80 * Math.cos(radius)));
            transYFrom = 0 + dp2px((float) (80 * Math.sin(radius)));
            transXTo = 0;
            transYTo = 0;
        } else if (direction == Configs.Position.RIGHT.getValue()) {
            double radius = -(Math.PI / 2) + index * Math.PI / (count - 1);
            transXFrom = menuWidth - dp2px(40) - dp2px((float) (80 * Math.cos(radius)));
            transYFrom = menuWidth + dp2px((float) (80 * Math.sin(radius)));
            transXTo = menuWidth;
            transYTo = 0;
        }


        final int curIndex = index;
        ballMenuAnimSet.play(menuBallScaleXAnim).with(menuBallScaleYAnim).with(menuBallAlphAnim);
        ballMenuAnimSet.setDuration(200);
        final float finalTransXFrom = transXFrom;
        final float finalTransYFrom = transYFrom;
        final float finalTransXTo = transXTo;
        final float finalTransYTo = transYTo;
        final float finalScaleXFrom = scaleXFrom;
        final float finalScaleYFrom = scaleYFrom;
        final float finalScaleXTo = scaleXTo;
        final float finalScaleYTo = scaleYTo;
        final float finalAlphTo = alphTo;
        final float finalAlphFrom = alphFrom;
        ballMenuAnimSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                ObjectAnimator.ofFloat(view, "scaleX", finalScaleXTo, finalScaleXFrom).start();
                ObjectAnimator.ofFloat(view, "scaleY", finalScaleYTo, finalScaleYFrom).start();
                ObjectAnimator.ofFloat(view, "alpha", finalAlphTo, finalAlphFrom).start();

                if (curIndex != -1) {
                    ObjectAnimator.ofFloat(view, "translationX", finalTransXFrom, finalTransXTo).start();
                    ObjectAnimator.ofFloat(view, "translationY", finalTransYFrom, finalTransYTo).start();
                    if (curIndex == (count - 1)) {
                        windowManager.removeView(menuView);
                        isMenuShow = false;
                    }
                } else {
                    windowManager.removeView(touchView);
                    isMenuShow = false;
                }
                if (onAnimEndListener != null) {
                    onAnimEndListener.onAnimEnd();
                }

            }
        });
        ballMenuAnimSet.start();

    }


    /**
     * 显示点击效果
     */
    private void showTouchBall() {
        ivTouchBall.setAlpha(1f);
//        ivTouchBall.setImageResource(R.drawable.ball_0);
        handler.removeCallbacks(pressRunnable);
        handler.postDelayed(pressRunnable, 1000);
    }

    /**
     * 隐藏
     */
    private void hideTouchBall() {
        ivTouchBall.setAlpha(touchAlpha / 255f);

//        ivTouchBall.setImageResource(R.drawable.vector_drawable_ball);
    }

    /**
     * 刷新悬浮球位置
     *
     * @param e2
     */
    private void refreshMovePlace(MotionEvent e2) {
        dy = e2.getRawY() - lastY;
        mParams.y += dy;
        mMenuParams.y += dy;
        windowManager.updateViewLayout(touchView, mParams);
        lastY = e2.getRawY();
    }

    private void setMoveDownXY(MotionEvent e) {
        lastY = e.getRawY();
        lastX = e.getRawX();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //如果二级菜单打开
        if (isMenuDetailShow) {
//            containerMenuDetailApps.post(new Runnable() {
//                @Override
//                public void run() {
//                    initEvent();
//                }
//            });
            windowManager.updateViewLayout(menuDetailView, mMenuDetailParams);
        } else if (isMenuShow) {//如果一级菜单打开
//            menuContainer.post(new Runnable() {
//                @Override
//                public void run() {
//                    initEvent();
//                }
//            });
            windowManager.updateViewLayout(menuView, mMenuParams);
        } else {//如果没有菜单打开
//            llTouchContainer.post(new Runnable() {
//                @Override
//                public void run() {
//                    initEvent();
//                }
//            });
            windowManager.updateViewLayout(touchView, mParams);
        }
        initTouchUI();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 工具 dip 2 px
     *
     * @param dp
     * @return
     */
    private int dp2px(float dp) {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (canMove) {
                showAcionUpAnim();
            }
        }
        return ballDetector.onTouchEvent(e);
    }

    /**
     * 如果悬浮球被拖拽到非屏幕边缘 那么放手的时候会有一个回弹的动画 现在未使用
     */
    private void showAcionUpAnim() {
        float startF = 0f;
        float endF = 0f;
        boolean isLeft = false;
        if (mParams.x <= (rightBorder + leftBorder) / 2) {
            startF = mParams.x;
            endF = 0;
            isLeft = true;
        } else if (mParams.x > (rightBorder + leftBorder) / 2) {
            startF = rightBorder - mParams.x;
            endF = 0;
            isLeft = false;
        }

        final boolean isFinalLeft = isLeft;
        ValueAnimator valueAnim = ValueAnimator.ofFloat(startF, endF);
        valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animX = (float) animation.getAnimatedValue();
                if (isFinalLeft) {
                    mParams.x = (int) animX;
                    windowManager.updateViewLayout(touchView, mParams);
                } else {
                    mParams.x = rightBorder - (int) animX;
                    windowManager.updateViewLayout(touchView, mParams);
                }
            }
        });
        valueAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                canMove = false;
            }
        });
        valueAnim.setInterpolator(new BounceInterpolator());
        valueAnim.setDuration(500);
        valueAnim.start();
    }

    /**
     * 展示悬浮球的显示动画
     *
     * @param view       对象
     * @param duration   动画持续时间
     * @param startDelay 动画延迟时间
     */
    private void showTouchBallEnterAnim(View view, int duration, int startDelay) {
        touchBallScaleXAnim = ObjectAnimator.ofFloat(view, "scaleX", 0, 1f);
        touchBallScaleYAnim = ObjectAnimator.ofFloat(view, "scaleY", 0, 1f);
        AnimatorSet ballAnimSet = new AnimatorSet();
        ballAnimSet.play(touchBallScaleXAnim).with(touchBallScaleYAnim);
        ballAnimSet.setDuration(duration);
        ballAnimSet.setStartDelay(startDelay);
        ballAnimSet.start();
        showTouchBall();
    }

    /**
     * 隐藏二级菜单显示悬浮球
     */
    private void hideMenuDetailContainer() {
        menuDetailView.post(new Runnable() {
            @Override
            public void run() {
                if (isMenuDetailShow) {
                    //移除二级菜单
                    windowManager.removeView(menuDetailView);
                    //加入悬浮球
                    windowManager.addView(touchView, mParams);
                    //显示悬浮球加入动画
                    showTouchBallEnterAnim(ivTouchBall, HIDE_MENU_DETAIL_FAST, 0);
                    isMenuDetailShow = false;
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            windowManager.removeView(touchView);
            windowManager.removeView(menuContainer);
            windowManager.removeView(menuDetailView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showLockScreenView() {
        if (!isMenuDetailShow) {
            lockScreen();//锁屏
        }
    }

    private void showPaySelectView() {
        if (!isMenuDetailShow) {
            //显示快捷支付二级菜单
            Log.i(TAG, "onAnimEnd: 显示快捷支付二级菜单");
            showMenuDetailPay();
        }
    }

    private void showAppsSelectView() {
        if (!isMenuDetailShow) {
            //显示快捷APP二级菜单
            Log.i(TAG, "onAnimEnd: 显示快捷APP二级菜单");
            showMenuDetailApp();
        }
    }

    private void showVoiceAdjustView() {
        if (!isMenuDetailShow) {
            //显示声音二级菜单
            Log.i(TAG, "onAnimEnd: 显示声音二级菜单");
            showMenuDetailVoice();
        }
    }

    private void switchTouchPos() {
        if (!isMenuDetailShow) {
            //切换位置
            Log.i(TAG, "onAnimEnd: 切换位置");
            //切换容器位置
            if (direction == Configs.Position.LEFT.getValue()) {
                direction = Configs.Position.RIGHT.getValue();
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, direction);
                mParams.x = screenWidth;
                mMenuParams.x = screenWidth;
                mMenuDetailParams.x = screenWidth;
            } else if (direction == Configs.Position.RIGHT.getValue()) {
                direction = Configs.Position.LEFT.getValue();
                SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, direction);
                mParams.x = 0;
                mMenuParams.x = 0;
                mMenuDetailParams.x = 0;
            }

            //切换菜单按钮位置布局
            if (direction == Configs.Position.LEFT.getValue()) {
                setMenuBallLeftLayoutParams(menuContainer.getChildAt(0));
                setMenuBallLeftLayoutParams(menuContainer.getChildAt(1));
                setMenuBallLeftLayoutParams(menuContainer.getChildAt(2));
                setMenuBallLeftLayoutParams(menuContainer.getChildAt(3));
                setMenuBallLeftLayoutParams(menuContainer.getChildAt(4));
            } else if (direction == Configs.Position.RIGHT.getValue()) {
                setMenuBallRightLayoutParams(menuContainer.getChildAt(0));
                setMenuBallRightLayoutParams(menuContainer.getChildAt(1));
                setMenuBallRightLayoutParams(menuContainer.getChildAt(2));
                setMenuBallRightLayoutParams(menuContainer.getChildAt(3));
                setMenuBallRightLayoutParams(menuContainer.getChildAt(4));
            }
            //更新布局
            windowManager.updateViewLayout(touchView, mParams);
        }
    }

    /**
     * 根据行为作出保存的对应的举动
     *
     * @param opType
     */
    private void goOpEvent(String opType) {
        int funcType = SpUtils.getInt(getApplicationContext(), opType, FuncConfigs.Func.BACK.getValue());
        if (funcType == FuncConfigs.Func.BACK.getValue()) {//返回键
            enterBack();//返回
        } else if (funcType == FuncConfigs.Func.HOME.getValue()) {//Home键
            enterHome();
        } else if (funcType == FuncConfigs.Func.RECENT.getValue()) {//任务键
            enterRecents();
        } else if (funcType == FuncConfigs.Func.NOTIFICATION.getValue()) {//通知栏
            enterNotification();
        } else if (funcType == FuncConfigs.Func.TRUN_POS.getValue()) {//切换位置
            switchTouchPos();
        } else if (funcType == FuncConfigs.Func.VOICE_MENU.getValue()) {//声音菜单
            showMenuDetailVoice();
        } else if (funcType == FuncConfigs.Func.PAY_MENU.getValue()) {//支付菜单
            showMenuDetailPay();
        } else if (funcType == FuncConfigs.Func.APP_MENU.getValue()) {//app菜单
            showMenuDetailApp();
        } else if (funcType == FuncConfigs.Func.MENU.getValue()) {//app菜单
            showMenuContainer();
        } else if (funcType == FuncConfigs.Func.PREVIOUS_APP.getValue()) {//app菜单
            jump2LastApp();
        } else if (funcType == FuncConfigs.Func.LOCK_SCREEN.getValue()) {//app菜单
            lockScreen();
        }
    }

    /**
     * 根据行为作出保存的对应的举动
     *
     * @param opType
     */
    private void goMenuBallEvent(String opType, int index) {
        final int funcType = SpUtils.getInt(getApplicationContext(), opType, FuncConfigs.Func.BACK.getValue());
        int touchIndex = 0;
        if (funcType == FuncConfigs.Func.VOICE_MENU.getValue() || funcType == FuncConfigs.Func.PAY_MENU.getValue()
                || funcType == FuncConfigs.Func.APP_MENU.getValue()) {
            touchIndex = index;
        } else {
            touchIndex = -1;
        }
        //隐藏菜单按钮动画
        hideMenuContainer(touchIndex, new Configs.OnAnimEndListener() {
            @Override
            public void onAnimEnd() {
                if (funcType == FuncConfigs.Func.BACK.getValue()) {//返回键
                    enterBack();//返回
                } else if (funcType == FuncConfigs.Func.HOME.getValue()) {//Home键
                    enterHome();
                } else if (funcType == FuncConfigs.Func.RECENT.getValue()) {//任务键
                    enterRecents();
                } else if (funcType == FuncConfigs.Func.NOTIFICATION.getValue()) {//通知栏
                    enterNotification();
                } else if (funcType == FuncConfigs.Func.TRUN_POS.getValue()) {//切换位置
                    switchTouchPos();
                } else if (funcType == FuncConfigs.Func.VOICE_MENU.getValue()) {//声音菜单
                    showMenuDetailVoice();
                } else if (funcType == FuncConfigs.Func.PAY_MENU.getValue()) {//支付菜单
                    showMenuDetailPay();
                } else if (funcType == FuncConfigs.Func.APP_MENU.getValue()) {//app菜单
                    showMenuDetailApp();
                } else if (funcType == FuncConfigs.Func.MENU.getValue()) {//app菜单
                    showMenuContainer();
                } else if (funcType == FuncConfigs.Func.PREVIOUS_APP.getValue()) {//app菜单
                    jump2LastApp();
                } else if (funcType == FuncConfigs.Func.LOCK_SCREEN.getValue()) {//app菜单
                    lockScreen();
                }
            }
        });
        initMenuDetailVoiceEvent();

    }
}