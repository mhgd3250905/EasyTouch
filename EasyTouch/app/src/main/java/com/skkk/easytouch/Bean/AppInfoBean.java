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
    private String activityName;
    private String name;
    private int flag;

    public AppInfoBean( String pkgName,String activityName,String name, int flag) {
        this.pkgName = pkgName;
        this.activityName = activityName;
        this.name=name;
        this.flag = flag;
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

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
