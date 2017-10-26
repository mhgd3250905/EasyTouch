package com.skkk.easytouch.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 创建于 2017/9/28
 * 作者 admin
 */
/*
* 
* 描    述：权限检测类
* 作    者：ksheng
* 时    间：2017/9/28$ 21:59$.
*/
public class PermissionsUtils {

    // 判断权限集合
    public static boolean lacksPermissions(Context context,String... permissions) {
        boolean lack=false;
        for (String permission : permissions) {
            if (lacksPermission(context,permission)) {
                lack=true;
            }
        }
        return lack;
    }

    // 判断是否缺少权限
    public static boolean lacksPermission(Context context,String permission) {
        return ContextCompat.checkSelfPermission(context.getApplicationContext(), permission)
                == PackageManager.PERMISSION_DENIED;
    }

}
