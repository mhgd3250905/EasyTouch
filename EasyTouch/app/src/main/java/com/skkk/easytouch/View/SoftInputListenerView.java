package com.skkk.easytouch.View;

import android.content.Context;
import android.util.Log;
import android.view.View;

/**
 * 创建于 2018/1/27
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2018/1/27$ 14:19$.
*/
public class SoftInputListenerView extends View {
    private static final String TAG = "SoftInputListenerView";
    private OnSoftInputStateChangeListener onSoftInputStateChangeListener;

    public interface OnSoftInputStateChangeListener{
        void onSoftInputSttateChange(int w, int h, int oldw, int oldh);
    }

    public SoftInputListenerView(Context context) {
        super(context);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        if (onSoftInputStateChangeListener!=null){
            onSoftInputStateChangeListener.onSoftInputSttateChange(w,h,oldw,oldh);
        }
    }

    public void setOnSoftInputStateChangeListener(OnSoftInputStateChangeListener onSoftInputStateChangeListener) {
        this.onSoftInputStateChangeListener = onSoftInputStateChangeListener;
    }
}
