package com.skkk.easytouch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skkk.easytouch.Bean.AppInfoBean;
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
public class AppAdapter extends BaseAdapter<AppInfoBean,AppViewHolder> {

    public AppAdapter(Context context, List<AppInfoBean> mDataList) {
        super(context, mDataList);
    }

    @Override
    protected AppViewHolder getCostumViewHolder(ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(context).inflate(R.layout.item_app,parent,false));
    }

    @Override
    protected void setViewHolder(AppViewHolder holder, int position) {
        AppInfoBean bean = mDataList.get(position);
        if (bean.getIcon()!=null) {
            holder.ivItemIcon.setImageDrawable(bean.getIcon());
        }
        holder.tvItemAppName.setText(bean.getAppName());
        holder.ivItemAppAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
