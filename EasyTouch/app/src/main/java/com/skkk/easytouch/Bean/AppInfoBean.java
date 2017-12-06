package com.skkk.easytouch.Bean;

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
    private String activityName;
    private int flag;
    private String shortCutName;


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

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getShortCutName() {
        return shortCutName;
    }

    public void setShortCutName(String shortCutName) {
        this.shortCutName = shortCutName;
    }
}
