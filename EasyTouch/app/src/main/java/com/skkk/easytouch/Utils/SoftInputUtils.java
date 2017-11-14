package com.skkk.easytouch.Utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 创建于 2017/11/14
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/11/14$ 22:23$.
*/
public class SoftInputUtils {
    private static boolean isFirst = true;

    public interface OnGetSoftHeightListener {
        void onShowed(int height);
    }

    public interface OnSoftKeyWordShowListener {
        void hasShow(boolean isShow);
    }

    /**
     * 获取软键盘的高度 * *
     *
     * @param rootView *
     * @param listener
     */
    public static void getSoftKeyboardHeight(final View rootView, final OnGetSoftHeightListener listener) {
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener
                = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isFirst) {
                    final Rect rect = new Rect();
                    rootView.getWindowVisibleDisplayFrame(rect);
                    final int screenHeight = rootView.getRootView().getHeight();
                    final int heightDifference = screenHeight - rect.bottom;
                    //设置一个阀值来判断软键盘是否弹出
                    boolean visible = heightDifference > screenHeight / 3;
                    if (visible) {
                        isFirst = false;
                        if (listener != null) {
                            listener.onShowed(heightDifference);
                        }
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }


    /**
     * 判断软键盘是否弹出
     * * @param rootView
     *
     * @param listener 备注：在不用的时候记得移除OnGlobalLayoutListener
     */
    public static ViewTreeObserver.OnGlobalLayoutListener doMonitorSoftKeyWord(final View rootView, final OnSoftKeyWordShowListener listener) {
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                final int screenHeight = rootView.getRootView().getHeight();
                Log.e("TAG", rect.bottom + "#" + screenHeight);
                final int heightDifference = screenHeight - rect.bottom;
                boolean visible = heightDifference > screenHeight / 3;
                if (listener != null)
                    listener.hasShow(visible);
            }
        };
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        return layoutListener;
    }
}
