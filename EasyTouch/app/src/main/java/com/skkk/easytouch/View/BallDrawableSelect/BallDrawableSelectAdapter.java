package com.skkk.easytouch.View.BallDrawableSelect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.PackageUtils;
import com.skkk.easytouch.View.BaseAdapter;

import java.util.List;

/**
 * 创建于 2017/12/22
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/12/22$ 23:38$.
*/
public class BallDrawableSelectAdapter extends BaseAdapter<String,BallDrawableSelectViewHolder> {

    public BallDrawableSelectAdapter(Context context, List<String> mDataList) {
        super(context, mDataList);
    }

    @Override
    protected BallDrawableSelectViewHolder getCostumViewHolder(ViewGroup parent, int viewType) {
        return new BallDrawableSelectViewHolder(LayoutInflater.from(context).inflate(R.layout.item_drawable_select,parent,false));
    }

    @Override
    protected void setViewHolder(BallDrawableSelectViewHolder holder, int position) {
        holder.ivDrawableSelect.setImageResource(PackageUtils.getResource(context,mDataList.get(position)));
    }


}
