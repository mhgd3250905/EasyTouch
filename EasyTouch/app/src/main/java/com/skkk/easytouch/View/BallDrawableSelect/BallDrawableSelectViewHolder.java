package com.skkk.easytouch.View.BallDrawableSelect;

import android.view.View;
import android.widget.ImageView;

import com.skkk.easytouch.R;
import com.skkk.easytouch.View.BaseViewHolder;

/**
 * 创建于 2017/12/22
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/12/22$ 23:40$.
*/
public class BallDrawableSelectViewHolder extends BaseViewHolder {
    ImageView ivDrawableSelect;
    public BallDrawableSelectViewHolder(View itemView) {
        super(itemView);
        ivDrawableSelect= (ImageView) itemView.findViewById(R.id.iv_item_drawable_select);
    }
}
