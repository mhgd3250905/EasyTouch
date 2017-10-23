package com.skkk.easytouch;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class EasyTouchService extends Service implements View.OnTouchListener {
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private View touchView;
    private float lastX;
    private float lastY;
    private float dy;
    private float dx;
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    private boolean isMove;
    private Vibrator vibrator;


    @Override
    public void onCreate() {
        super.onCreate();
        if (windowManager == null) {
            windowManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }

        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Point size = new Point();
        windowManager.getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;

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
        mParams.x = 0;
        mParams.y = screenHeight-dp2px(getApplicationContext(),200f);

        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch,null);
        ImageView ivTouchBack= (ImageView) touchView.findViewById(R.id.ivTouchBack);
        ImageView ivTouchHome= (ImageView) touchView.findViewById(R.id.ivTouchHome);
        ImageView ivTouchRecent= (ImageView) touchView.findViewById(R.id.ivTouchRecent);
        ivTouchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动30毫秒
                vibrator.vibrate(30);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
            }
        });
        ivTouchHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动30毫秒
                vibrator.vibrate(30);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_HOME);
            }
        });
        ivTouchRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //震动30毫秒
                vibrator.vibrate(30);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
            }
        });
        ivTouchBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDPM.isAdminActive(mAdminName)) {
                    //震动30毫秒
                    vibrator.vibrate(15);
                    mDPM.lockNow();
                }
                return false;
            }
        });

        ivTouchHome.setOnTouchListener(this);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager.addView(touchView,mParams);
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
            switch (e.getAction()){
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    lastY = e.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = true;
//                    dx = e.getX()-lastX;
                    dy = e.getRawY()-lastY;
//                    mParams.x+=dx/3;
                    mParams.y+=dy;
                    windowManager.updateViewLayout(touchView,mParams);
                    lastX=e.getRawX();
                    lastY=e.getRawY();
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            return isMove;
    }
}
