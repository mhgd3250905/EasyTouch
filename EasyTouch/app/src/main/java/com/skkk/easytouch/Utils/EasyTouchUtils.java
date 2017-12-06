package com.skkk.easytouch.Utils;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * 创建于 2017/12/6
 * 作者 admin
 */
/*
* 
* 描    述：悬浮球各种操作的功能集合类
* 作    者：ksheng
* 时    间：2017/12/6$ 23:31$.
*/
public class EasyTouchUtils {
    private volatile static  EasyTouchUtils instance;
    private static Context context;
    private Vibrator vibrator;


    public EasyTouchUtils(Context context) {
        this.context = context;
        vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);

    }

    public static EasyTouchUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (PackageUtils.class) {
                if (instance == null) {
                    return new EasyTouchUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 展示震动
     * @param vibrateLevel 震动级别
     */
    public void showVibrate(int vibrateLevel){
        vibrator.vibrate(vibrateLevel);
    }

    

    /**
     * 模拟全局按键
     *
     * @param service
     */
    public static void monitorSystemAction(AccessibilityService service, int action) {
        if (Build.VERSION.SDK_INT < 16) {
            Toast.makeText(service, "Android 4.1及以上系统才支持此功能，请升级后重试", Toast.LENGTH_SHORT).show();
        } else {
            service.performGlobalAction(action);
        }
    }


}
