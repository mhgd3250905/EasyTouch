package com.skkk.easytouch.View.AppSelect;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.skkk.easytouch.R;
import com.skkk.easytouch.View.BaseViewHolder;

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
public class AppViewHolder extends BaseViewHolder {
    ImageView ivItemIcon;
    TextView tvItemAppName;
    public AppViewHolder(View itemView) {
        super(itemView);
        ivItemIcon= (ImageView) itemView.findViewById(R.id.iv_item_icon);
        tvItemAppName= (TextView) itemView.findViewById(R.id.tv_item_app_name);
    }
}
