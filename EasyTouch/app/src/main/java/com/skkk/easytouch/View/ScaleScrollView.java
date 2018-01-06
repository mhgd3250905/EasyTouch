package com.skkk.easytouch.View;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

/**
 * 类似小米列表的拖拽效果
 * Created by shengk on 2017/12/18.
 */

public class ScaleScrollView extends NestedScrollView {
    private static final String TAG = "ScaleRecyclerView";
    private float lastX;
    private float lastY;

    private boolean isScale=false;
    private float mScale=1.0f;
    private float mScaleRatio=0.3f;

    public ScaleScrollView(Context context) {
        super(context);
    }

    public ScaleScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleScrollView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x= ev.getX();
        float y=ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX=x;
                lastY=y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (y-lastY>0){
                    if (getScrollY() == 0){
                        float distance=y-lastY;
                        mScale=1+distance*mScaleRatio/getHeight();
                        setPivotY(0f);
                        setPivotX(getWidth()/2);
                        ViewCompat.setScaleY(this,mScale);
                        isScale=true;
                    }
                }
//                else {
//                    int count=getChildCount();
//                    if (getScrollY() + getHeight() - getPaddingTop()-getPaddingBottom() == getChildAt(0).getHeight()){
//                        float distance=y-lastY;
//                        mScale=1-mScaleRatio*distance/getHeight();
//                        setPivotX(getWidth()/2);
//                        setPivotY(getHeight());
//                        ViewCompat.setScaleY(this,mScale);
//                        isScale=true;
//                    }
//                }
                break;
            case MotionEvent.ACTION_UP:
                if (isScale){
                    ObjectAnimator animator=ObjectAnimator.ofFloat(this,"scaleY",mScale,1.0f);
                    animator.setInterpolator(new DecelerateInterpolator());
                    animator.setDuration(200);
                    animator.start();
                    isScale=false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}