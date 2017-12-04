package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skkk.easytouch.Bean.AppInfoBean;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.IntentUtils;
import com.skkk.easytouch.Utils.SpUtils;

import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_LEFT;


public class EasyTouchBallService extends Service implements View.OnTouchListener {
    private static final String TAG = "EasyTouchBallService";
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private WindowManager.LayoutParams mMenuParams;
    private WindowManager.LayoutParams mMenuDetailParams;
    private View touchView;
    private float lastX;
    private float lastY;
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    private boolean isMove;
    private Vibrator vibrator;

    private RelativeLayout llTouchContainer;
    private GestureDetector ballDetector;
    private float dx;
    private float dy;

    private int touchWidth = Configs.DEFAULT_TOUCH_WIDTH;//悬浮条的宽度 单位dp
    private int touchHeight = Configs.DEFAULT_TOUCH_HEIGHT;//悬浮条的高度 单位dp
    private int vibrateLevel = Configs.DEFAULT_VIBRATE_LEVEL;//震动等级

    private int screenWidth;
    private int screenHeight;
    private int leftBorder;
    private int rightBorder;
    private int direction;
    private int directionX;
    private ImageView ivTouchBall;
    private LinearLayout llMenuContainer;
    private boolean canMove = false;
    private AnimatorSet set;
    private ObjectAnimator scaleXAnim;
    private ObjectAnimator scaleYAnim;
    private Handler handler = new Handler();
    private Runnable longClickRunnable;
    private boolean isRepeat = false;

    private Runnable pressRunnable;
    private RelativeLayout menuContainer;
    private boolean isMenuShow = false;
    private boolean isMenuDetailShow = false;
    private View menuView;
    private View menuDetailView;
    private ImageView ivMenuBall1, ivMenuBall2, ivMenuBall3;
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
    private LinearLayout containerMenuDetailVoice;
    private SeekBar sbAlarmAudio;
    private Switch switchMode;
    private AudioManager audioManager;
    private LinearLayout containerMenuDetailPay;
    private LinearLayout containerMenuDetailApps;
    private RelativeLayout containerAppsMenuDetailBack;

    private ImageView ivAlipayScan;
    private ImageView ivAlipayPay;
    private ImageView ivWeixinScan;
    private ImageView ivMenuDetailBack;
    private RelativeLayout containerMenuDetailBack;
    private ObjectAnimator hideMenuDetailAnim;


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
        mParams.y = screenHeight - dp2px(200f);

        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        if (direction == TOUCH_UI_DIRECTION_LEFT) {
            directionX = leftBorder;
        } else {
            directionX = rightBorder;
        }

        mMenuParams.x = directionX;
        mMenuParams.y = screenHeight - dp2px(200f);

        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball, null);
        llTouchContainer = (RelativeLayout) touchView.findViewById(R.id.ll_touch_container);
        ivTouchBall = (ImageView) touchView.findViewById(R.id.ivTouchBall);
        llMenuContainer = (LinearLayout) touchView.findViewById(R.id.ll_menu_container);

        menuView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball_menu, null);
        menuContainer = (RelativeLayout) menuView.findViewById(R.id.container_menu_ball);
        ivMenuBall1 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_1);
        ivMenuBall2 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_2);
        ivMenuBall3 = (ImageView) menuView.findViewById(R.id.iv_menu_ball_3);

        menuDetailView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball_menu_detail, null);

        containerMenuDetailVoice = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_voice);
        sbSystemAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_system_audio);
        sbMediaAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_media_audio);
        sbAlarmAudio = (SeekBar) menuDetailView.findViewById(R.id.sb_alarm_audio);
        ivAudioSystem = (ImageView) menuDetailView.findViewById(R.id.iv_audio_system);
        ivAudioMedia = (ImageView) menuDetailView.findViewById(R.id.iv_audio_media);
        ivAudioAlarm = (ImageView) menuDetailView.findViewById(R.id.iv_audio_alarm);
        switchMode = (Switch) menuDetailView.findViewById(R.id.switch_mode);
        tvAudioMode = (TextView) menuDetailView.findViewById(R.id.tv_audio_mode);

        containerMenuDetailPay = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_pay);
        ivAlipayScan = (ImageView) menuDetailView.findViewById(R.id.iv_scan_alipay);
        ivAlipayPay = (ImageView) menuDetailView.findViewById(R.id.iv_pay_alipay);
        ivWeixinScan = (ImageView) menuDetailView.findViewById(R.id.iv_scan_weixin);

        containerMenuDetailApps = (LinearLayout) menuDetailView.findViewById(R.id.container_ball_menu_detail_apps);
        containerAppsMenuDetailBack = (RelativeLayout) menuDetailView.findViewById(R.id.containerAppsMenuDetailBack);

        ivMenuDetailBack = (ImageView) menuDetailView.findViewById(R.id.iv_menu_detail_back);
        containerMenuDetailBack = (RelativeLayout) menuDetailView.findViewById(R.id.containerMenuDetailBack);

        llMenuContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        windowManager.addView(touchView, mParams);
    }

    /**
     * 设置触摸块UI
     */
    private void initTouchUI() {
        initEvent();
        windowManager.updateViewLayout(touchView, mParams);
    }


    /**
     * 设置事件
     */
    private void initEvent() {
        initTouchBallEvent();
        initMenuEvent();
        initMenuDetailVoiceEvent();
    }

    /**
     * 设置声音设置详情菜单事件
     */
    private void initMenuDetailVoiceEvent() {
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

        /*
        * 设置支付事件监听
        * */
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

        containerMenuDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuDetailEnterAnim(menuDetailView, new Configs.OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        hideMenuDetailContainer();
                    }
                }, false);
            }
        });

        containerAppsMenuDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuDetailEnterAnim(menuDetailView, new Configs.OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        hideMenuDetailContainer();
                    }
                }, true);
            }
        });

        initMenuAppDetailsEvent();
    }

    /**
     * 设置APP点击事件
     */
    private void initMenuAppDetailsEvent() {
        String shortCut1Str = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_SHORT_CUT_APPS_ + 1, "");
        String shortCut2Str = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_SHORT_CUT_APPS_ + 2, "");
        String shortCut3Str = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_SHORT_CUT_APPS_ + 3, "");
        String shortCut4Str = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_SHORT_CUT_APPS_ + 4, "");
        String shortCut5Str = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_SHORT_CUT_APPS_ + 5, "");
        if (!shortCut1Str.isEmpty()){
            AppInfoBean models = new Gson().fromJson(shortCut1Str, AppInfoBean.class);

        }
    }

    private void hideMenuDetailContainer() {
        menuDetailView.post(new Runnable() {
            @Override
            public void run() {
                if (isMenuDetailShow) {
                    windowManager.removeView(menuDetailView);
                    windowManager.addView(touchView, mParams);
                    isMenuDetailShow = false;
                }
            }
        });
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
                    vibrator.vibrate(vibrateLevel);
                    showTouchBall();
                    recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (canMove) {
                    //设定为只有上下滑动的时候才可以进行位置调整
                    showTouchBall();
                    refreshMovePlace(e2);
                    if (isMenuShow) {
                        isMenuShow=false;
                        hideMenuContainer(Configs.Position.NONE, null);
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
                        //震动30毫秒
                        vibrator.vibrate(vibrateLevel);
                        recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
                    }
                } else if (e1.getX() - e2.getX() > 10 && Math.abs(e1.getY() - e2.getY()) < (Math.abs(e1.getX() - e2.getX()) / 2)) {
                    if (!canMove) {//左滑
                        //震动30毫秒
                        vibrator.vibrate(vibrateLevel);
                        recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_HOME);
                    }
                } else if (e1.getY() - e2.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > (Math.abs(e1.getX() - e2.getX()) * 2)) {
                    //上滑
                    if (!canMove) {
                        //震动30毫秒
                        if (mDPM.isAdminActive(mAdminName)) {
                            //震动30毫秒
                            vibrator.vibrate(vibrateLevel);
                            mDPM.lockNow();
                        }
                    }
                } else if (e2.getY() - e1.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > (Math.abs(e1.getX() - e2.getX()) * 2)) {
                    //下滑
                    if (!canMove) {
                        //震动30毫秒
                        vibrator.vibrate(vibrateLevel);
                        recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                    }
                }


                return false;
            }
        });

        ballDetector.setIsLongpressEnabled(false);
    }

    /**
     * 设置一级菜单事件
     */
    private void initMenuEvent() {
        menuContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideMenuContainer(Configs.Position.NONE, null);
                return false;
            }
        });

        ivMenuBall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuContainer(Configs.Position.TOP, new Configs.OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        if (!isMenuDetailShow) {
                            showMenuDetailVoice();
                        }
                    }
                });

            }
        });
        ivMenuBall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuContainer(Configs.Position.MID, new Configs.OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        if (!isMenuDetailShow) {
                            showMenuDetailApp();
                        }
                    }
                });
            }
        });

        ivMenuBall3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenuContainer(Configs.Position.BOTTOM, new Configs.OnAnimEndListener() {
                    @Override
                    public void onAnimEnd() {
                        if (!isMenuDetailShow) {
                            showMenuDetailPay();
                        }
                    }
                });
            }
        });
    }

    /**
     * 显示详细菜单-声音设置
     */
    private void showMenuDetailVoice() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(80);
        containerMenuDetailVoice.setVisibility(View.VISIBLE);
        containerMenuDetailApps.setVisibility(View.GONE);
        containerMenuDetailPay.setVisibility(View.GONE);
        windowManager.addView(menuDetailView, mMenuDetailParams);
        containerMenuDetailVoice.post(new Runnable() {
            @Override
            public void run() {
                //显示二级菜单
                enterMenuDetailAnim(menuDetailView);
                isMenuDetailShow = true;
            }
        });
    }

    /**
     * 二级菜单的进入动画
     *
     * @param containerMenuDetail
     */
    private void enterMenuDetailAnim(View containerMenuDetail) {
        ObjectAnimator.ofFloat(containerMenuDetail, "translationX", dp2px(-300f), 0).start();
    }

    /**
     * 二级菜单的退出动画
     *
     * @param containerMenuDetail
     */
    private void hideMenuDetailEnterAnim(View containerMenuDetail, final Configs.OnAnimEndListener onAnimEndListener, boolean isAppsMenu) {
        int transX = dp2px(-200f);
        if (isAppsMenu) {
            transX = dp2px(-300f);
        }
        hideMenuDetailAnim = ObjectAnimator.ofFloat(containerMenuDetail, "translationX", 0, transX);
        hideMenuDetailAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (onAnimEndListener != null) {
                    onAnimEndListener.onAnimEnd();
                }
            }
        });
        hideMenuDetailAnim.start();
    }

    /**
     * 显示详细菜单-支付设置
     */
    private void showMenuDetailPay() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(80);
        containerMenuDetailVoice.setVisibility(View.GONE);
        containerMenuDetailApps.setVisibility(View.GONE);
        containerMenuDetailPay.setVisibility(View.VISIBLE);
        windowManager.addView(menuDetailView, mMenuDetailParams);
        containerMenuDetailVoice.post(new Runnable() {
            @Override
            public void run() {
                //显示二级菜单
                enterMenuDetailAnim(menuDetailView);
                isMenuDetailShow = true;
            }
        });
    }

    /**
     * 显示详细菜单-App设置
     */
    private void showMenuDetailApp() {
        mMenuDetailParams.x = mParams.x;
        mMenuDetailParams.y = mParams.y - dp2px(80);
        containerMenuDetailVoice.setVisibility(View.GONE);
        containerMenuDetailApps.setVisibility(View.VISIBLE);
        containerMenuDetailPay.setVisibility(View.GONE);
        windowManager.addView(menuDetailView, mMenuDetailParams);
        containerMenuDetailVoice.post(new Runnable() {
            @Override
            public void run() {
                //显示二级菜单
                enterMenuDetailAnim(menuDetailView);
                isMenuDetailShow = true;
            }
        });
    }


    /**
     * 显示隐藏菜单
     */
    private void showMenuContainer() {
        mMenuParams.x = mParams.x;
        mMenuParams.y = mParams.y - dp2px(80);
        windowManager.addView(menuView, mMenuParams);
        isMenuShow = true;
        menuView.post(new Runnable() {
            @Override
            public void run() {
                shouMenuBallAnim(ivMenuBall1, Configs.Position.TOP);
                shouMenuBallAnim(ivMenuBall2, Configs.Position.MID);
                shouMenuBallAnim(ivMenuBall3, Configs.Position.BOTTOM);
            }
        });
    }

    /**
     * 针对不同的位置的菜单按钮进行不同的动画弹出方式
     *
     * @param view
     * @param position
     */
    private void shouMenuBallAnim(View view, Configs.Position position) {
        ballMenuAnimSet = new AnimatorSet();
        int transXFrom = 0;
        int transYFrom = 0;
        int transXTo = 0;
        int transYTo = 0;
        switch (position) {
            case TOP://上方菜单按钮
                transXFrom = 0;
                transYFrom = 0;
                transXTo = dp2px((float) (100 * Math.cos(Math.PI / 4)));
                transYTo = dp2px((float) (100 * Math.sin(-Math.PI / 4)));

                break;
            case MID://中部菜单按钮
                transXFrom = 0;
                transYFrom = 0;
                transXTo = dp2px((float) (100 * Math.cos(0)));
                transYTo = dp2px((float) (100 * Math.sin(0)));
                break;
            case BOTTOM://下方菜单按钮
                transXFrom = 0;
                transYFrom = 0;
                transXTo = dp2px((float) (100 * Math.cos(Math.PI / 4)));
                transYTo = dp2px((float) (100 * Math.sin(Math.PI / 4)));
                break;
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
    private void hideMenuContainer(final Configs.Position selectPos, final Configs.OnAnimEndListener onAnimEndListener) {
        menuView.post(new Runnable() {
            @Override
            public void run() {
                if (!selectPos.equals(Configs.Position.NONE)) {
                    hideMenuBallAnim(ivTouchBall, Configs.Position.NONE, false, null);
                }
                hideMenuBallAnim(ivMenuBall1, Configs.Position.TOP, selectPos.equals(Configs.Position.TOP), null);
                hideMenuBallAnim(ivMenuBall2, Configs.Position.MID, selectPos.equals(Configs.Position.MID), null);
                hideMenuBallAnim(ivMenuBall3, Configs.Position.BOTTOM, selectPos.equals(Configs.Position.BOTTOM), onAnimEndListener);
            }
        });

    }

    /**
     * 根据不同位置的菜单按钮进行动画执行
     *
     * @param view
     * @param position
     */
    private void hideMenuBallAnim(final View view, Configs.Position position, boolean isSelected, final Configs.OnAnimEndListener onAnimEndListener) {

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


        switch (position) {
            case TOP://上方菜单按钮
                transXFrom = dp2px((float) (100 * Math.cos(Math.PI / 4)));
                transYFrom = dp2px((float) (100 * Math.sin(-Math.PI / 4)));
                transXTo = 0;
                transYTo = 0;

                break;
            case MID://中部菜单按钮
                transXFrom = dp2px((float) (100 * Math.cos(0)));
                transYFrom = dp2px((float) (100 * Math.sin(0)));
                transXTo = 0;
                transYTo = 0;
                break;
            case BOTTOM://下方菜单按钮
                transXFrom = dp2px((float) (100 * Math.cos(Math.PI / 4)));
                transYFrom = dp2px((float) (100 * Math.sin(Math.PI / 4)));
                transXTo = 0;
                transYTo = 0;
                break;
        }


        final Configs.Position curPos = position;
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

                if (!curPos.equals(Configs.Position.NONE)) {
                    ObjectAnimator.ofFloat(view, "translationX", finalTransXFrom, finalTransXTo).start();
                    ObjectAnimator.ofFloat(view, "translationY", finalTransYFrom, finalTransYTo).start();
                    if (curPos.equals(Configs.Position.BOTTOM)) {
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
        ivTouchBall.setImageResource(R.drawable.vector_drawable_ball_press);
        handler.removeCallbacks(pressRunnable);
        handler.postDelayed(pressRunnable, 1000);
    }

    /**
     * 隐藏
     */
    private void hideTouchBall() {
        ivTouchBall.setImageResource(R.drawable.vector_drawable_ball);
    }

    /**
     * 刷新悬浮球位置
     *
     * @param e2
     */
    private void refreshMovePlace(MotionEvent e2) {
//        dx = e2.getRawX() - lastX;
        dy = e2.getRawY() - lastY;
        mParams.y += dy;
//        mParams.x += dx;
        mMenuParams.y += dy;
//        mMenuParams.x += dx;
        windowManager.updateViewLayout(touchView, mParams);
//        windowManager.updateViewLayout(menuView, mMenuParams);
//        lastX = e2.getRawX();
        lastY = e2.getRawY();
    }

    private void setMoveDownXY(MotionEvent e) {
        lastY = e.getRawY();
        lastX = e.getRawX();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        llTouchContainer.post(new Runnable() {
            @Override
            public void run() {
                initTouchUI();
            }
        });
        windowManager.updateViewLayout(touchView, mParams);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 模拟全局按键
     *
     * @param service
     */
    public static void recentApps(AccessibilityService service, int action) {
        if (Build.VERSION.SDK_INT < 16) {
            Toast.makeText(service, "Android 4.1及以上系统才支持此功能，请升级后重试", Toast.LENGTH_SHORT).show();
        } else {
            service.performGlobalAction(action);
        }
    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            windowManager.removeView(touchView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
