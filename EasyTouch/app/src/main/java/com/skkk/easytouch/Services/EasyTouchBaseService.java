package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.ShotScreenUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.AppSelect.AppSelectActivity;
import com.skkk.easytouch.View.SoftInputListenerView;

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


    protected int menuDetailWidthMax = 320;
    protected int menuDetailWidthMin = 220;

    protected int menuDetailHeightMax = 160;
    protected int menuDetailHeightMin = 80;

    protected float wholeMenuWidth = 340f;
    private WindowManager.LayoutParams softInputLp;
    protected SoftInputListenerView softInputListenerView;

    protected int lastParamsY;//软件盘弹出前的高度位置
    protected boolean hasConfigurationChanged = false;

    protected int flagSoftInputChangeHeight = 300;

    protected int screenWidth;
    protected int screenHeight;

    protected int leftBorder;
    protected int rightBorder;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private static Intent mResultData = null;

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

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        leftBorder = 0;
        rightBorder = screenWidth;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            flagSoftInputChangeHeight = Math.max(screenWidth, screenHeight) / 3;
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            flagSoftInputChangeHeight = Math.min(screenWidth, screenHeight) / 3;
        }

        initUI();
    }


    private void initUI() {
        //设置输入法监听的悬浮view
        softInputLp = new WindowManager.LayoutParams();
        softInputLp.width = 0;
        softInputLp.x = 0;
        softInputLp.height = WindowManager.LayoutParams.MATCH_PARENT;
        softInputLp.type = WindowManager.LayoutParams.TYPE_PHONE;
        softInputLp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
        softInputLp.format = PixelFormat.TRANSPARENT;
        softInputLp.gravity = Gravity.LEFT | Gravity.TOP;
        softInputListenerView = new SoftInputListenerView(this);
        softInputListenerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        windowManager.addView(softInputListenerView, softInputLp);

        //设置剪贴板监听
//        initClipBoard();
    }

    /**
     * 设置剪贴板监听
     */
    private void initClipBoard() {
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        // 获取剪贴板的剪贴数据集
        ClipData clipData = clipboard.getPrimaryClip();

        if (clipData != null && clipData.getItemCount() > 0) {
            // 从数据集中获取（粘贴）第一条文本数据
            CharSequence text = clipData.getItemAt(0).getText();
            System.out.println("text: " + text);
        }
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

    /**
     * 截屏
     */
    protected void shotScreen() {
        if (ShotScreenUtils.checkServiceIsRun()) {
            Toast.makeText(this, "开始截屏", Toast.LENGTH_SHORT).show();
            ShotScreenUtils.getInstance().startScreenShot();
        } else {
            Toast.makeText(this, "请确认截屏权限是否开启", Toast.LENGTH_SHORT).show();
        }
    }
}

