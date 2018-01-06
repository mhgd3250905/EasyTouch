package com.skkk.easytouch.Utils;

import android.content.Context;

/**
 * 创建于 2018/1/6
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2018/1/6$ 16:46$.
*/
public class DpUtils {
    /**
     * 工具 dip 2 px
     *
     * @param dp
     * @return
     */
    public static int dp2px(Context context,float dp) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.1f);
    }
}
