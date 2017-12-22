package com.skkk.easytouch.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;

/**
 * 创建于 2017/9/18
 * 作者 admin
 */
/*
* 
* 描    述：弹出对话框样式
* 作    者：ksheng
* 时    间：2017/9/18$ 21:33$.
*/
public class DialogUtils {

    public static AlertDialog showDialog(Context context, @DrawableRes int iconRes, String title, String message
            , String positiveTitle, DialogInterface.OnClickListener positiveClickListener,
                                         String negativeTitle, DialogInterface.OnClickListener negativeClickListener){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setIcon(iconRes);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveTitle,positiveClickListener);
        builder.setNegativeButton(negativeTitle,negativeClickListener);
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }

}
