package com.skkk.easytouch;

import android.app.Application;

/**
 * 创建于 2018/1/12
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2018/1/12$ 23:50$.
*/
public class MyApplication extends Application {
    private static boolean isSettingShape = false;//外观设置是否打开
    private static Configs.TouchType touchType= Configs.TouchType.NONE;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static boolean isSettingShape() {
        return isSettingShape;
    }

    public static void setIsSettingShape(boolean isSettingShape) {
        MyApplication.isSettingShape = isSettingShape;
    }

    public static Configs.TouchType getTouchType() {
        return touchType;
    }

    public static void setTouchType(Configs.TouchType touchType) {
        MyApplication.touchType = touchType;
    }
}
