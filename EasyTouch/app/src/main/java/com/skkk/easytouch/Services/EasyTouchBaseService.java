package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.skkk.easytouch.Bean.AppInfoBean;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.AppSelect.AppSelectActivity;

import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_LEFT;

/**
 * 创建于 2017/12/10
 * 作者 admin
 */
/*
* 
* 描    述：悬浮窗父类
* 作    者：ksheng
* 时    间：2017/12/10$ 15:08$.
*/
public class EasyTouchBaseService extends Service {

    private static final String TAG = "EasyTouchBaseService";

    protected WindowManager windowManager;//窗口管理器
    protected AudioManager audioManager;//音量管理器
    protected ComponentName mAdminName;//组件名称
    protected DevicePolicyManager mDPM;//设备安全管理器
    protected Vibrator vibrator;//震动管理器

    protected int vibrateLevel = Configs.DEFAULT_VIBRATE_LEVEL;//震动等级
    protected int direction = Configs.TOUCH_UI_DIRECTION_LEFT;//左右位置

    private WindowManager.LayoutParams mWholeMenuParams;
    private View wholeMenuView;
    private RelativeLayout containerWholeMenu;
    private GridLayout containerWholeMenuApps;
    private RelativeLayout containerWholeMenuBg;

    protected int menuDetailWidthMax = 320;
    protected int menuDetailWidthMin = 220;

    protected int menuDetailHeightMax = 160;
    protected int menuDetailHeightMin = 80;

    protected float wholeMenuWidth = 340f;


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
        initWholeMenu();
    }

    private void initWholeMenu() {
        //设置二级菜单的LP
        mWholeMenuParams = new WindowManager.LayoutParams();
        mWholeMenuParams.packageName = getPackageName();
        mWholeMenuParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWholeMenuParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWholeMenuParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        mWholeMenuParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mWholeMenuParams.format = PixelFormat.RGBA_8888;
        mWholeMenuParams.gravity = Gravity.LEFT | Gravity.TOP;

        wholeMenuView = View.inflate(getApplicationContext(), R.layout.layout_whole_menu, null);
        containerWholeMenu = (RelativeLayout) wholeMenuView.findViewById(R.id.container_whole_menu);
        containerWholeMenuBg = (RelativeLayout) wholeMenuView.findViewById(R.id.container_whole_menu_bg);
        containerWholeMenuApps = (GridLayout) wholeMenuView.findViewById(R.id.container_whole_menu_apps);

        initWholeMenuUI();
        initWholeMenuEvent();
    }

    /**
     * 初始化菜单UI
     */
    private void initWholeMenuUI() {
//        WallpaperManager wallpaperManager=WallpaperManager.getInstance(getApplicationContext());
//        Drawable drawable = wallpaperManager.getDrawable();
//        containerWholeMenuBg.setBackground(drawable);
    }

    /**
     * 初始化菜单事件
     */
    private void initWholeMenuEvent() {
        wholeMenuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(wholeMenuView);
            }
        });


        for (int i = 0; i < 10; i++) {
            ImageView ivApp = (ImageView) containerWholeMenuApps.getChildAt(i);
            String shortCutStr = SpUtils.getString(getApplicationContext(), Configs.KEY_BALL_MENU_TOP_APPS_ + i, "");
            final int finalIndex = i;
            if (TextUtils.isEmpty(shortCutStr)) {
                ivApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSelectAppActivity(finalIndex, Configs.AppType.APP.getValue(), Configs.TouchType.BALL);
                    }
                });

            } else {
                final AppInfoBean appInfo = new Gson().fromJson(shortCutStr, AppInfoBean.class);
                if (appInfo != null) {
                    ivApp.setImageDrawable(PackageUtils.getInstance(getApplicationContext()).getShortCutIcon(appInfo));
                    ivApp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PackageUtils.getInstance(getApplicationContext()).startAppActivity(appInfo);
                        }
                    });
                    ivApp.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            startSelectAppActivity(finalIndex, Configs.AppType.APP.getValue(), Configs.TouchType.BALL);
                            return true;
                        }
                    });
                }
            }
        }
    }


    /**
     * 显示全部菜单
     */
    protected void showWholeMenu() {
        windowManager.addView(wholeMenuView, mWholeMenuParams);
        containerWholeMenu.post(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator enterMenuDetailAnim = null;
                if (direction == Configs.Position.LEFT.getValue()) {
                    enterMenuDetailAnim = ObjectAnimator.ofFloat(containerWholeMenu, "translationX", dp2px(-wholeMenuWidth), 0);
                } else if (direction == Configs.Position.RIGHT.getValue()) {
                    enterMenuDetailAnim = ObjectAnimator.ofFloat(containerWholeMenu, "translationX", dp2px(wholeMenuWidth), 0);
                }
                if (enterMenuDetailAnim != null) {
                    enterMenuDetailAnim.start();
                }
            }
        });
//        initWholeMenuBg();
    }

    /**
     * 设置全部菜单的背景
     */
    private void initWholeMenuBg() {
        ValueAnimator animBgAlpha = ValueAnimator.ofFloat(0f, 1f);
        animBgAlpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                Log.i(TAG, "onAnimationUpdate:" + alpha);
                containerWholeMenuBg.setAlpha(alpha);
            }
        });
        animBgAlpha.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                containerWholeMenuBg.setAlpha(1f);
            }
        });
        animBgAlpha.setDuration(800);
        animBgAlpha.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 按下返回键
     */
    protected void enterBack() {
        vibrator.vibrate(vibrateLevel);
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);

    }

    /**
     * 按下HOME键
     */
    protected void enterHome() {
        vibrator.vibrate(vibrateLevel);
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_HOME);

    }

    /**
     * 按下RECENTS键
     */
    protected void enterRecents() {
        vibrator.vibrate(vibrateLevel);
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);

    }

    /**
     * 按下RECENTS键
     */
    protected void enterNotification() {
        vibrator.vibrate(vibrateLevel);
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
    }

    /**
     * 按下RECENTS键
     */
    protected void lockScreen() {
        if (mDPM.isAdminActive(mAdminName)) {
            //震动30毫秒
            vibrator.vibrate(vibrateLevel);
            mDPM.lockNow();
        }
    }

    /**
     * 跳转到上一个应用
     */
    protected void jump2LastApp() {
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vibrator.vibrate(vibrateLevel);
        monitorSystemAction(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
    }

    /**
     * 模拟全局按键
     *
     * @param service
     */
    public static void monitorSystemAction(AccessibilityService service, int action) {
        if (Build.VERSION.SDK_INT < 16) {
            Toast.makeText(service, "Android 4.1及以上系统才支持此功能，请升级后重试", Toast.LENGTH_SHORT).show();
        } else {
            service.performGlobalAction(action);
        }
    }

    /**
     * 打开App选择界面
     *
     * @param finalIndex
     * @param value
     */
    protected void startSelectAppActivity(int finalIndex, int value, Configs.TouchType touchType) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), AppSelectActivity.class);
        intent.putExtra(Configs.KEY_APP_TYPE, value);
        intent.putExtra(Configs.KEY_BALL_MENU_SELECT_APP_INDEX, finalIndex);
        intent.putExtra(Configs.KEY_TOUCH_TYPE, touchType.getValue());
        startActivity(intent);
        stopSelf();
    }

    /**
     * 工具 dip 2 px
     *
     * @param dp
     * @return
     */
    protected int dp2px(float dp) {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }
}
