package com.skkk.easytouch.Services;

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
import android.support.annotation.DrawableRes;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.SpUtils;


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
    //自定义
    private final int DEFAULT_TOUCH_WIDTH=15;
    private final int DEFAULT_TOUCH_HEIGHT=240;
    private final int DEFAULT_VIBRATE_LEVEL=30;
    private int touchWidth = DEFAULT_TOUCH_WIDTH;//悬浮条的宽度 单位dp
    private int touchHeight = DEFAULT_TOUCH_HEIGHT;//悬浮条的高度 单位dp
    private int vibrateLevel = DEFAULT_VIBRATE_LEVEL;//震动等级
    private @DrawableRes int topDrawable= R.drawable.shape_react_corners_top;//上方触摸块背景
    private @DrawableRes int midDrawable=R.drawable.shape_react_corners_mid;//中部触摸块背景
    private @DrawableRes int bottomDrawable=R.drawable.shape_react_corners_bottom;//下方触摸块背景


    @Override
    public void onCreate() {
        super.onCreate();
        if (windowManager == null) {
            windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
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
        mParams.y = screenHeight - dp2px(getApplicationContext(), 200f);

        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch, null);
        llTouchContainer = (LinearLayout) touchView.findViewById(R.id.ll_touch_container);
        ivTouchTop = (ImageView) touchView.findViewById(R.id.iv_touch_top);
        ivTouchMid = (ImageView) touchView.findViewById(R.id.iv_touch_mid);
        ivTouchBottom = (ImageView) touchView.findViewById(R.id.iv_touch_bottom);

        llTouchContainer.post(new Runnable() {
            @Override
            public void run() {
                initTouchUI();

            }
        });

        //设置时间
        initEvent();

        windowManager.addView(touchView, mParams);


    }

    /**
     * 设置触摸块UI
     */
    private void initTouchUI() {
        //设置宽高
        touchWidth= SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_WIDTH,DEFAULT_TOUCH_WIDTH);
        touchHeight=SpUtils.getInt(getApplicationContext(),Configs.KEY_TOUCH_UI_HEIGHT,DEFAULT_TOUCH_HEIGHT);
        vibrateLevel=SpUtils.getInt(getApplicationContext(),Configs.KEY_TOUCH_UI_VIBRATE_LEVEL,DEFAULT_VIBRATE_LEVEL);
        topDrawable=SpUtils.getInt(getApplicationContext(),Configs.KEY_TOUCH_UI_TOP_DRAWABLE,R.drawable.shape_react_corners_top);
        midDrawable=SpUtils.getInt(getApplicationContext(),Configs.KEY_TOUCH_UI_MID_DRAWABLE,R.drawable.shape_react_corners_mid);
        bottomDrawable=SpUtils.getInt(getApplicationContext(),Configs.KEY_TOUCH_UI_BOTTOM_DRAWABLE,R.drawable.shape_react_corners_bottom);

        ViewGroup.LayoutParams containerLp = llTouchContainer.getLayoutParams();
        containerLp.width = dp2px(getApplicationContext(), touchWidth);
        containerLp.height = dp2px(getApplicationContext(), touchHeight);
        llTouchContainer.setLayoutParams(containerLp);
        windowManager.updateViewLayout(touchView, mParams);

        ivTouchTop.setImageResource(topDrawable);
        ivTouchMid.setImageResource(midDrawable);
        ivTouchBottom.setImageResource(bottomDrawable);
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
        topDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                //震动30毫秒
                vibrator.vibrate(vibrateLevel);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_RECENTS);
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
                if (e2.getY() - e1.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > Math.abs(e1.getX() - e2.getX())) {
                    vibrator.vibrate(vibrateLevel);
                    recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                }
                return false;
            }
        });
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
                return false;
            }
        });
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
                //震动30毫秒
                vibrator.vibrate(vibrateLevel);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
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
                if (e1.getY() - e2.getY() > 10 && Math.abs(e1.getY() - e2.getY()) > Math.abs(e1.getX() - e2.getX())) {
                    if (mDPM.isAdminActive(mAdminName)) {
                        //震动30毫秒
                        vibrator.vibrate(vibrateLevel);
                        mDPM.lockNow();
                    }
                }
                return false;
            }
        });
//        midDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Log.d(TAG, "onSingleTapConfirmed() called with: e = [" + e + "]");
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                return false;
//            }
//        });
//        bottomDetector.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                //震动30毫秒
//                vibrator.vibrate(30);
//                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTap(MotionEvent e) {
//                if (mDPM.isAdminActive(mAdminName)) {
//                    //震动30毫秒
//                    vibrator.vibrate(15);
//                    mDPM.lockNow();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onDoubleTapEvent(MotionEvent e) {
//                return false;
//            }
//        });
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
}
