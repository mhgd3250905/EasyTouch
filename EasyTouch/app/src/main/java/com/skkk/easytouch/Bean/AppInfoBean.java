package com.skkk.easytouch.Bean;

import android.graphics.drawable.Drawable;

/**
 * 创建于 2017/12/3
 * 作者 admin
 */
/*
* 
* 描    述：安装应用信息
* 作    者：ksheng
* 时    间：2017/12/3$ 20:34$.
*/
public class AppInfoBean {
    private String pkgName;
    private String appName;
    private String className;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
