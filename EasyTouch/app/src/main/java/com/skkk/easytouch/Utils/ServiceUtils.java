package com.skkk.easytouch.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 创建于 2017/10/27
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/10/27$ 22:31$.
*/
public class ServiceUtils {
    /**
     * 判断服务是否后台运行
     *
     * @param mContext Context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(200);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            Log.i(TAG, "isServiceRun: -->"+serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }
}
