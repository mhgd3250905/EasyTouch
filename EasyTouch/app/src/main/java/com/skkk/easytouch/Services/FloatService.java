package com.skkk.easytouch.Services;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * 创建于 2017/10/20
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/10/20$ 18:52$.
*/
public class FloatService extends AccessibilityService {
    private static AccessibilityService service;

    public FloatService() {
        service = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    public static AccessibilityService getService() {
        if (service == null) {
            return null;
        }
        return service;
    }
}
