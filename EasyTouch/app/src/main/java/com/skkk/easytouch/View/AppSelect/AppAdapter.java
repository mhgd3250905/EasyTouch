package com.skkk.easytouch.View.AppSelect;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.skkk.easytouch.Bean.AppInfoBean;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.View.BaseAdapter;

import java.util.List;

/**
 * 创建于 2017/12/3
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/12/3$ 23:32$.
*/
public class AppAdapter extends BaseAdapter<AppInfoBean, AppViewHolder> {

    public AppAdapter(Context context, List<AppInfoBean> mDataList) {
        super(context, mDataList);
    }

    @Override
    protected AppViewHolder getCostumViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(context).inflate(R.layout.item_app, parent, false));
    }

    @Override
    protected void setViewHolder(AppViewHolder holder, int position) {
        PackageManager packageManager = context.getPackageManager();
        AppInfoBean bean = mDataList.get(position);
        holder.ivItemIcon.setImageDrawable(PackageUtils.getInstance(context).getShortCutIcon(bean));
        //        // 拿到包名
        //        String pkg = info.activityInfo.packageName;
        //// 拿到运行的Cls名
        //        String cls = info.activityInfo.name;
        //// 拿到应用程序的信息
        //        ApplicationInfo appInfo = info.activityInfo.applicationInfo;
        //// 拿到应用程序的图标
        //        Drawable icon = getAppIcon(info);
        //// 拿到应用名
        //        String appName=info.loadLabel(packageManager).toString();
        holder.tvItemAppName.setText(bean.getName());
    }
}
