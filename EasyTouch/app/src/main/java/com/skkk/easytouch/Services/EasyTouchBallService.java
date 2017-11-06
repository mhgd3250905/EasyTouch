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
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Utils.SpUtils;

import static com.skkk.easytouch.Configs.TOUCH_UI_DIRECTION_LEFT;


public class EasyTouchBallService extends Service implements View.OnTouchListener {
    private static final String TAG = "EasyTouchBallService";
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private View touchView;
    private float lastX;
    private float lastY;
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;
    private boolean isMove;
    private Vibrator vibrator;

    private LinearLayout llTouchContainer;
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
    private boolean canMove = false;
    private AnimatorSet set;
    private ObjectAnimator scaleXAnim;
    private ObjectAnimator scaleYAnim;
    private Handler handler = new Handler();
    private Runnable longClickRunnable;
    private boolean isRepeat = false;


    @Override
    public void onCreate() {
        super.onCreate();

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

        direction = SpUtils.getInt(getApplicationContext(), Configs.KEY_TOUCH_UI_DIRECTION, TOUCH_UI_DIRECTION_LEFT);
        if (direction == TOUCH_UI_DIRECTION_LEFT) {
            directionX = leftBorder;
        } else {
            directionX = rightBorder;
        }

        mParams.x = directionX;
        mParams.y = screenHeight - dp2px(getApplicationContext(), 200f);

        touchView = View.inflate(getApplicationContext(), R.layout.layout_easy_touch_ball, null);
        llTouchContainer = (LinearLayout) touchView.findViewById(R.id.ll_touch_container);
        ivTouchBall = (ImageView) touchView.findViewById(R.id.ivTouchBall);


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
                vibrator.vibrate(vibrateLevel);
                recentApps(FloatService.getService(), AccessibilityService.GLOBAL_ACTION_BACK);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.i(TAG, "onScroll: e2-Action--->" + e2.getAction());
                if (canMove) {
                    refreshMovePlace(e2);
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

    private void showLongClickAnim() {
        isRepeat = true;
        handler.post(longClickRunnable);
    }

    private void cancelLongClickAnim() {
        isRepeat = false;
        handler.removeCallbacks(longClickRunnable);
    }

    private void refreshMovePlace(MotionEvent e2) {
        dx = e2.getRawX() - lastX;
        dy = e2.getRawY() - lastY;
        mParams.y += dy;
        mParams.x += dx;
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
        if (e.getAction() == MotionEvent.ACTION_UP) {
            if (canMove) {
                showAcionUpAnim();
            }
        }
        return ballDetector.onTouchEvent(e);
    }

    private void showAcionUpAnim() {
        float startF=0f;
        float endF=0f;
        boolean isLeft = false;
        if (mParams.x<=(rightBorder+leftBorder)/2){
            startF=mParams.x;
            endF=0;
            isLeft=true;
        }else if (mParams.x>(rightBorder+leftBorder)/2){
            startF=rightBorder-mParams.x;
            endF=0;
            isLeft=false;
        }

        final boolean isFinalLeft=isLeft;
        ValueAnimator valueAnim=ValueAnimator.ofFloat(startF,endF);
        valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animX= (float) animation.getAnimatedValue();
                if (isFinalLeft) {
                    mParams.x = (int) animX;
                    Log.i(TAG, "mParams.x-->"+mParams.x);
                    windowManager.updateViewLayout(touchView, mParams);
                }else {
                    mParams.x = rightBorder-(int)animX;
                    Log.i(TAG, "mParams.x-->"+mParams.x);
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
