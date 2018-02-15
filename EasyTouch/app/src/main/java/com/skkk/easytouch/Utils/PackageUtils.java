package com.skkk.easytouch.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.skkk.easytouch.Bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建于 2017/12/3
 * 作者 admin
 */
/*
* 
* 描    述：包管理工具
* 作    者：ksheng
* 时    间：2017/12/3$ 19:30$.
*/
public class PackageUtils {
    private static volatile PackageUtils instance;
    private static Context context;
    private Drawable iconDrawable;

    public PackageUtils(Context context) {
        this.context = context;
    }

    public static PackageUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (PackageUtils.class) {
                if (instance == null) {
                    return new PackageUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取所有的快捷方式的入口信息
     *
     * @return
     */
    public List<AppInfoBean> getAllShortCuts() {
        //获取到所有快捷方式
        Intent shortcutsIntent = new Intent(Intent.ACTION_CREATE_SHORTCUT);
        List<ResolveInfo> shortcuts = context.getPackageManager().queryIntentActivities(
                shortcutsIntent, 0);
        List<AppInfoBean> appInfoBeanList=new ArrayList<>();
        for (int i = 0; i < shortcuts.size(); i++) {
            PackageInfo pkg = null;
            String appName="";
            try {
                pkg = context.getApplicationContext().
                        getPackageManager().getPackageInfo(shortcuts.get(i).activityInfo.packageName, 0);
                appName = pkg.applicationInfo.loadLabel(context.getApplicationContext().getPackageManager()).toString();
            } catch (PackageManager.NameNotFoundException e) {

            }
            appInfoBeanList.add(new AppInfoBean(shortcuts.get(i).activityInfo.packageName,
                    shortcuts.get(i).activityInfo.name,
                    appName,
                    shortcuts.get(i).activityInfo.flags));
        }
        return appInfoBeanList;
//        List<ShortCutInfoBean> infoList=new ArrayList<>();
//        ShortCutInfoBean infoBean;
//        for (int i = 0; i < shortcuts.size(); i++) {
//            infoBean=new ShortCutInfoBean();
//            //启动方式如下
//            ActivityInfo ai=shortcuts.get(i).activityInfo;
//            infoBean.setPkgName(ai.applicationInfo.packageName);
//            infoBean.setActivityName(ai.name);
//            infoBean.setFlag(ai.flags);
//            infoBean.setShortCutName(ai.applicationInfo.name);
//            infoBean.setIcon(getShortCutIcon(shortcuts.get(i)));
//            infoList.add(infoBean);
//        }
//        return infoList;
    }


    /**
     * 获取指定快捷方式的图标
     *
     * @param appInfoBean
     * @return
     */
    public Drawable getShortCutIcon(AppInfoBean appInfoBean) {
        //获取对应icon方法如下
        //启动方式如下
        Drawable iconDrawable = null;
        PackageManager pm = context.getPackageManager();

        String pkgName = appInfoBean.getPkgName();
        String activityName = appInfoBean.getActivityName();
        int flag = appInfoBean.getFlag();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(pkgName, activityName);
        intent.addFlags(flag);
        try {
            iconDrawable = pm.getActivityIcon(intent);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return iconDrawable;
    }


    /**
     * 启动指定的curShort
     *
     * @param resolveInfo
     */
    public void startShortCutActivity(ResolveInfo resolveInfo) {
        //启动方式如下
        ActivityInfo ai = resolveInfo.activityInfo;
        String pkgName = ai.applicationInfo.packageName;
        String activityName = ai.name;
        int flag = ai.flags;
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(pkgName, activityName);
        intent.addFlags(flag);
        context.startActivity(intent);
    }

    /**
     * 打开指定APP
     *
     * @param appInfoBean
     */
    public void startAppActivity(AppInfoBean appInfoBean) {
        //该应用的包名和主Activity
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appInfoBean.getPkgName());
        context.startActivity(intent);
    }

    /**
     * 获取系统中所有应用信息， 并将应用软件信息保存到list列表中。
     */
    public List<AppInfoBean> getAllApps() {
        PackageManager packageManager = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);
        List<AppInfoBean> appInfoBeanList=new ArrayList<>();
        for (int i = 0; i < resolveInfos.size(); i++) {
            PackageInfo pkg = null;
            String appName="";
            try {
                pkg = context.getApplicationContext().
                        getPackageManager().getPackageInfo(resolveInfos.get(i).activityInfo.packageName, 0);
                appName = pkg.applicationInfo.loadLabel(context.getApplicationContext().getPackageManager()).toString();
            } catch (PackageManager.NameNotFoundException e) {

            }
            appInfoBeanList.add(new AppInfoBean(resolveInfos.get(i).activityInfo.packageName,
                    resolveInfos.get(i).activityInfo.name,
                    appName,
                    resolveInfos.get(i).activityInfo.flags));
        }
        return appInfoBeanList;
    }

    private Drawable getAppIcon(ResolveInfo resolveInfo) {
        Drawable icon = null;
        PackageManager pm = context.getPackageManager();
        ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
        // 拿到应用程序的图标
        icon = appInfo.loadIcon(pm);
        return icon;
    }

    public static int getResource(Context context, String imageName) {
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    /**
     * 判断是否存在某一个应用
     * @param context 上下文
     * @param pkgName 包名
     * @return 是否存在
     */
    public static boolean checkAppExist(Context context,String pkgName) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            if (packageInfo.packageName.equals(pkgName)){
                return true;
            }
        }
        return false;
    }



}