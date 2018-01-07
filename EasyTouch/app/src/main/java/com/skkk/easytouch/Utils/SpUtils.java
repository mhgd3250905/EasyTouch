package com.skkk.easytouch.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * 创建于 2017/10/14
 * 作者 admin
 */
/*
* 
* 描    述：SharedPreferences 工具类
* 作    者：ksheng
* 时    间：2017/10/14$ 18:00$.
*/
public class SpUtils {
    public static final String KEY_APP_IS_FIRST_RYN="key_app_is_first_ryn";//是否为第一次运行
    public static final String KEY_MENU_BALL_COUNT="key_menu_ball_count";//二级菜单的球有几个

    /**
     * 保存String
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveString(Context context, String key, String value) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    /**
     * 获取String 默认返回值为""
     *
     * @param context
     * @param key
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 保存布尔值
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取布尔值 默认返回值false
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 保存int
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveInt(Context context, String key, int value) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 获取int 默认0
     *
     * @param context
     * @param key
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * 保存浮点数
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveFloat(Context context, String key, float value) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        sp.edit().putFloat(key, value).apply();
    }

    /**
     * 获取浮点数 默认为0f
     *
     * @param context
     * @param key
     * @return
     */
    public static float getFloat(Context context, String key, float defValue) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences("note", MODE_PRIVATE);
        return sp.getFloat(key, defValue);
    }
}
