package com.skkk.easytouch.Bean;

import android.graphics.drawable.Drawable;

/**
 * 创建于 2017/12/3
 * 作者 admin
 */
/*
* 
* 描    述：快捷方式
* 作    者：ksheng
* 时    间：2017/12/3$ 20:32$.
*/
public class ShortCutInfoBean {
    private String pkgName;
    private String activityName;
    private int flag;
    private String shortCutName;
    private Drawable icon;

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

    public String getPkgName() {
        return pkgName;
    }

    public String getShortCutName() {
        return shortCutName;
    }

    public void setShortCutName(String shortCutName) {
        this.shortCutName = shortCutName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
