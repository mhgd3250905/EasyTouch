package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.IntentUtils;
import com.skkk.easytouch.Utils.SpUtils;

import static com.skkk.easytouch.Configs.DEFAULT_ALPHA;
import static com.skkk.easytouch.Configs.DEFAULT_THEME;
import static com.skkk.easytouch.Configs.DEFAULT_TOUCH_HEIGHT;
import static com.skkk.easytouch.Configs.DEFAULT_TOUCH_WIDTH;
import static com.skkk.easytouch.Configs.DEFAULT_VIBRATE_LEVEL;
import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_LEFT;
import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_RIGHT;


public class EasyTouchService extends Service implements View.OnTouchListener {
    private static final String TAG = "EasyTouchService";
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private View touchView;
    private float lastX;
    private float lastY;
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    private boolean isMove;
    private Vibrator vibrator;
    private ImageView ivTouchBottom;
    private ImageView ivTouchMid;
    private ImageView ivTouchTop;
    private LinearLayout llTouchContainer;
    private GestureDetector midDetector;
    private GestureDetector topDetector;

    private GestureDetector bottomDetector;
    private float dx;
    private float dy;

    private int touchWidth = Configs.DEFAULT_TOUCH_WIDTH;//悬浮条的宽度 单位dp
    private int touchHeight = Configs.DEFAULT_TOUCH_HEIGHT;//悬浮条的高度 单位dp
    private int vibrateLevel = Configs.DEFAULT_VIBRATE_LEVEL;//震动等级
    private
    @DrawableRes
    int topDrawable = R.drawable.shape_react_corners_top;//上方触摸块背景
    private
    @DrawableRes
    int midDrawable = R.drawable.shape_react_corners_mid;//中部触摸块背景
    private
    @DrawableRes
    int bottomDrawable = R.drawable.shape_react_corners_bottom;//下方触摸块背景
    private int topColor;
    private int midColor;
    private int bottomColor;
    private int colorAlpha;
    private int screenWidth;
    private int screenHeight;
    private int leftBorder;
    private int rightBorder;
    private int direction;
    private int directionX;

    private int minTouchSlop = 10;    //触摸滑动最小触发距离
    private AudioManager audioManager;  //音量管理器
    private LinearLayout llMenuContainer;
    private SeekBar sbSystemAudio, sbMediaAudio, sbAlarmAudio;
    private boolean isTopMenuShow=false;//Top菜单是否打开
    private boolean isBottomMenuShow=false;//Bottom菜单是否打开

    private ImageView ivAudioSystem;
    private ImageView ivAudioMedia;
    private ImageView ivAudioAlarm;
    private TextView tvAudioMode;
    private Switch switchMode;
    private LinearLayout llMenuBottomContainer;
    private ImageView ivAlipayScan;
    private ImageView ivAlipayPay;
    private ImageView ivWeixinScan;


    @Override
    public void onCreate() {
        super.onCreate();


        //设置界面窗口管理器
        if (windowManager == null) {
            windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }


        //设置音量管理器
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //设置设置管理器
        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        //设置震动管理器
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //设置屏幕尺寸
        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        //设置左右边界
        leftBorder = 0;
        rightBorder = screenWidth;

        //设置布局管理器
        mParams = new WindowManager.LayoutParams();
        mParams.packageName = getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

        mParams.format = PixelFormat.RGBA_8888;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;

        //设置左右方向
        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        if (direction == TOUCH_UI_DIRECTION_LEFT) {
            directionX = leftBorder;
        } else {
            directionX = rightBorder;
        }

        //设置悬浮窗的位置
        mParams.x = directionX;
        mParams.y = screenHeight - dp2px(getApplicationContext(), 200f);

        //初始化悬浮窗的控件内容
        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch, null);
        llTouchContainer = (LinearLayout) touchView.findViewById(R.id.ll_touch_container);
        ivTouchTop = (ImageView) touchView.findViewById(R.id.iv_touch_top);
        ivTouchMid = (ImageView) touchView.findViewById(R.id.iv_touch_mid);
        ivTouchBottom = (ImageView) touchView.findViewById(R.id.iv_touch_bottom);

        llMenuContainer = (LinearLayout) touchView.findViewById(R.id.ll_menu_container);
        sbSystemAudio = (SeekBar) touchView.findViewById(R.id.sb_system_audio);
        sbMediaAudio = (SeekBar) touchView.findViewById(R.id.sb_media_audio);
        sbAlarmAudio = (SeekBar) touchView.findViewById(R.id.sb_alarm_audio);
        ivAudioSystem = (ImageView) touchView.findViewById(R.id.iv_audio_system);
        ivAudioMedia = (ImageView) touchView.findViewById(R.id.iv_audio_media);
        ivAudioAlarm = (ImageView) touchView.findViewById(R.id.iv_audio_alarm);
        switchMode = (Switch) touchView.findViewById(R.id.switch_mode);
        tvAudioMode = (TextView) touchView.findViewById(R.id.tv_audio_mode);

        llMenuBottomContainer = (LinearLayout) touchView.findViewById(R.id.ll_menu_bottom_container);
        ivAlipayScan = (ImageView) touchView.findViewById(R.id.iv_scan_alipay);
        ivAlipayPay = (ImageView) touchView.findViewById(R.id.iv_pay_alipay);
        ivWeixinScan = (ImageView) touchView.findViewById(R.id.iv_scan_weixin);

        windowManager.addView(touchView, mParams);
    }

    /**
     * 设置触摸块UI
     */
    private void initTouchUI() {
        //设置宽高
        touchWidth = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH, DEFAULT_TOUCH_WIDTH);
        touchHeight = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_HEIGHT, DEFAULT_TOUCH_HEIGHT);
        vibrateLevel = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_VIBRATE_LEVEL, DEFAULT_VIBRATE_LEVEL);

        topDrawable = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_DRAWABLE, R.drawable.shape_react_corners_top);
        midDrawable = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_MID_DRAWABLE, R.drawable.shape_react_corners_mid);
        bottomDrawable = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_BOTTOM_DRAWABLE, R.drawable.shape_react_corners_bottom);

        topColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_TOP_COLOR, R.color.colorRecent);
        midColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_MID_COLOR, R.color.colorHome);
        bottomColor = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_BOTTOM_COLOR, R.color.colorBack);

        colorAlpha = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_COLOR_ALPHA, DEFAULT_ALPHA);

        int theme = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_THEME, DEFAULT_THEME);

        if (theme == Configs.TOUCH_UI_THEME_0) {
            topDrawable = R.drawable.shape_react_corners_top;
            midDrawable = R.drawable.shape_react_corners_mid;
            bottomDrawable = R.drawable.shape_react_corners_bottom;
        } else if (theme == Configs.TOUCH_UI_THEME_1) {
            topDrawable = R.drawable.shape_react_top;
            midDrawable = R.drawable.shape_react_mid;
            bottomDrawable = R.drawable.shape_react_bottom;
        }

        ViewGroup.LayoutParams containerLp = llTouchContainer.getLayoutParams();
        containerLp.width = dp2px(getApplicationContext(), touchWidth);
        containerLp.height = dp2px(getApplicationContext(), touchHeight);
        llTouchContainer.setLayoutParams(containerLp);

        ivTouchTop.setImageResource(topDrawable);
        ivTouchMid.setImageResource(midDrawable);
        ivTouchBottom.setImageResource(bottomDrawable);


        setImageViewDrawableColor(ivTouchTop, topDrawable, topColor, colorAlpha);
        setImageViewDrawableColor(ivTouchMid, midDrawable, midColor, colorAlpha);
        setImageViewDrawableColor(ivTouchBottom, bottomDrawable, bottomColor, colorAlpha);

        initEvent();

        windowManager.updateViewLayout(touchView, mParams);
    }

    /**
     * 设置自定义颜色的drawable
     *
     * @param iv
     * @param drawableRes
     * @param color
     */
    private void setImageViewDrawableColor(ImageView iv, @DrawableRes int drawableRes, int color, int alpha) {
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(drawableRes, getTheme());
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int argb = Color.argb(alpha, red, green, blue);

        drawable.setColor(argb);
        iv.setImageDrawable(drawable);
    }

    /**
     * 设置事件
     */
    private void initEvent() {
        ivTouchTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        ivTouchMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivTouchBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivTouchTop.setOnTouchListener(this);
        ivTouchMid.setOnTouchListener(this);
        ivTouchBottom.setOnTouchListener(this);

        ivTouchTop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                setTopMenuEvent();
                return true;
            }
        });

        //设置top按键事件触发
        topDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown() called with: e = [" + e + "]");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.d(TAG, "onShowPress() called with: e = [" + e + "]");

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp() called with: e = [" + e + "]");
                //震动30毫秒
                if (isTopMenuShow) {
                    vibrator.vibrate(vibrateLevel);
                    isTopMenuShow = false;
                    hideMenuContainer();
                } else {
                    vibrator.vibrate(vibrateLevel);
                    recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress() called with: e = [" + e + "]");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getY() - e1.getY() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) / 3 > Math.abs(e1.getX() - e2.getX())) {
                    //下滑
                    vibrator.vibrate(vibrateLevel);
                    recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                } else if (e1.getY() - e2.getY() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) / 3 > Math.abs(e1.getX() - e2.getX())) {
                    //上滑
                    setTopMenuEvent();
                }
                return false;
            }
        });
        //设置mid按键事件触发
        midDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                //记录move down坐标
                setMoveDownXY(e);
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //震动30毫秒
                vibrator.vibrate(vibrateLevel);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_HOME);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //刷新悬浮条位置
                refreshMovePlace(e2);
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e2.getX() - e1.getX() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) < (Math.abs(e1.getX() - e2.getX()) / 3)) {
                    if (direction == TOUCH_UI_DIRECTION_LEFT) {
                        direction = TOUCH_UI_DIRECTION_RIGHT;
                        SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_RIGHT);
                        mParams.x = rightBorder;
                        windowManager.updateViewLayout(touchView, mParams);
                    }
                } else if (e1.getX() - e2.getX() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) < (Math.abs(e1.getX() - e2.getX()) / 3)) {
                    if (direction == TOUCH_UI_DIRECTION_RIGHT) {
                        direction = TOUCH_UI_DIRECTION_LEFT;
                        SpUtils.saveInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
                        mParams.x = leftBorder;
                        windowManager.updateViewLayout(touchView, mParams);
                    }
                }
                return false;
            }
        });
        //设置bottom按键事件触发
        bottomDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (isBottomMenuShow){
                    vibrator.vibrate(vibrateLevel);
                    isBottomMenuShow=false;
                    hideBottomMenuContainer();
                }else {
                    //震动30毫秒
                    vibrator.vibrate(vibrateLevel);
                    recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() - e2.getY() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) / 3 > Math.abs(e1.getX() - e2.getX())) {
                    //上滑
                    //锁屏
                    if (mDPM.isAdminActive(mAdminName)) {
                        //震动30毫秒
                        vibrator.vibrate(vibrateLevel);
                        mDPM.lockNow();
                    }
                }else if (e2.getY() - e1.getY() > minTouchSlop && Math.abs(e1.getY() - e2.getY()) / 3 > Math.abs(e1.getX() - e2.getX())) {
                    //下滑
                    //呼出菜单
                    setBottomMenuEvent();
                }
                return false;
            }
        });

        topDetector.setIsLongpressEnabled(false);

    }

    /**
     * 设置Top按键上滑事件：音量调节
     */
    private void setTopMenuEvent() {
        if (!isTopMenuShow) {
            isTopMenuShow = true;
            showMenuContainer();//显示菜单

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
            windowManager.updateViewLayout(touchView, mParams);
        }
    }


    /**
     * 设置Bottom按键下滑事件：音量调节
     */
    private void setBottomMenuEvent() {
        if (!isBottomMenuShow) {
            isBottomMenuShow = true;
            showBottomMenuContainer();//显示菜单

            //设置静音模式切换监听
            llMenuBottomContainer.post(new Runnable() {
                @Override
                public void run() {
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
            });
            windowManager.updateViewLayout(touchView, mParams);
        }
    }

    /**
     * 显示隐藏菜单
     */
    private void showMenuContainer() {
        mParams.width = dp2px(getApplicationContext(), touchHeight);
        llMenuContainer.setVisibility(View.VISIBLE);
        windowManager.updateViewLayout(touchView, mParams);
    }

    private void hideMenuContainer() {
        mParams.width = dp2px(getApplicationContext(), touchWidth);
        llMenuContainer.setVisibility(View.GONE);
        windowManager.updateViewLayout(touchView, mParams);
    }

    /**
     * 显示隐藏菜单
     */
    private void showBottomMenuContainer() {
        mParams.width = dp2px(getApplicationContext(), touchHeight);
        llMenuBottomContainer.setVisibility(View.VISIBLE);
        windowManager.updateViewLayout(touchView, mParams);
    }

    private void hideBottomMenuContainer() {
        mParams.width = dp2px(getApplicationContext(), touchWidth);
        llMenuBottomContainer.setVisibility(View.GONE);
        windowManager.updateViewLayout(touchView, mParams);
    }

    private void refreshMovePlace(MotionEvent e2) {
        dx = e2.getRawX() - lastX;
        dy = e2.getRawY() - lastY;
        mParams.y += dy;
        windowManager.updateViewLayout(touchView, mParams);
        lastX = e2.getRawX();
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

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {
        if (v.getId() == R.id.iv_touch_top) {
            return topDetector.onTouchEvent(e);
        } else if (v.getId() == R.id.iv_touch_mid) {
            return midDetector.onTouchEvent(e);
        } else if (v.getId() == R.id.iv_touch_bottom) {
            return bottomDetector.onTouchEvent(e);
        }
        return false;
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
