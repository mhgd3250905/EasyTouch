package com.skkk.easytouch.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.skkk.easytouch.R;

/**
 * 创建于 2017/10/18
 * 作者 admin
 */
/*
* 
* 描    述：设置栏位Item
* 作    者：ksheng
* 时    间：2017/10/18$ 22:36$.
*/
public class SettingSwitchItemView extends LinearLayout {
    private ViewGroup container;
    private TextView tvTitle;
    private Switch switchSettings;

    public SettingSwitchItemView(Context context) {
        super(context);
        initUI(null);
    }

    public SettingSwitchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(attrs);
    }

    public SettingSwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(attrs);
    }

    /**
     * 初始化界面
     */
    private void initUI(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_settings_radio_item,this,true);
        container= (ViewGroup) findViewById(R.id.container_settings);
        tvTitle= (TextView) findViewById(R.id.tv_settings_title);
        switchSettings= (Switch) findViewById(R.id.swicth_settings);

        if (attrs!=null) {
            TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.SettingSwitchItemView);
            String title = ta.getString(R.styleable.SettingSwitchItemView_switchTitle);
            boolean checked = ta.getBoolean(R.styleable.SettingSwitchItemView_switchChecked,false);
            ta.recycle();

            tvTitle.setText(title);
            switchSettings.setChecked(checked);
        }
    }

    /**
     * 设置条目点击事件
     * @param onClickListener
     */
    public void setSettingItemClickListener(final OnClickListener onClickListener){
        if (container!=null){
            container.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener!=null) {
                        onClickListener.onClick(v);
                    }
                }
            });
        }
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        if (tvTitle!=null){
            tvTitle.setText(title);
        }
    }

    /**
     * 设置选项是否勾选
     * @param checked
     */
    public void setSwichChecked(boolean checked){
        if (switchSettings!=null){
            switchSettings.setChecked(checked);
        }
    }

    /**
     * 设置勾选监听
     * @param onCheckedChangeListener
     */
    public void setOnSwitchCheckedChangeListener(CompoundButton.OnCheckedChangeListener onCheckedChangeListener){
        if (switchSettings!=null){
            switchSettings.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }



    /**
     * 设置Item是否可用
     * @param enable
     */
    public void setItemEnable(boolean enable){
        container.setEnabled(enable);
    }
}
