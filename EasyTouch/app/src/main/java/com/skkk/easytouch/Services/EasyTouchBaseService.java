package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.SpUtils;

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

    protected WindowManager windowManager;//窗口管理器
    protected AudioManager audioManager;//音量管理器
    protected ComponentName mAdminName;//组件名称
    protected DevicePolicyManager mDPM;//设备安全管理器
    protected Vibrator vibrator;//震动管理器

    protected int vibrateLevel = Configs.DEFAULT_VIBRATE_LEVEL;//震动等级
    protected int direction=Configs.TOUCH_UI_DIRECTION_LEFT;//左右位置

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
}
