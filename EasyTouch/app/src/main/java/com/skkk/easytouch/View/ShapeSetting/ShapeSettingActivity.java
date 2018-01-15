package com.skkk.easytouch.View.ShapeSetting;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skkk.easytouch.MyApplication;
import com.skkk.easytouch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShapeSettingActivity extends AppCompatActivity {

    private static final String TAG = "ShapeSettingActivity";
    @Bind(R.id.tb_shape_settings)
    Toolbar tbShapeSettings;
    @Bind(R.id.tl_setting_shape)
    TabLayout tlSettingShape;
    @Bind(R.id.vp_setting_shape)
    ViewPager vpSettingShape;
    private ArrayList<Fragment> fragmentList;
    private String[] TITLE=new String[]{"悬浮条","悬浮球"};
    private ViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_setting);
        ButterKnife.bind(this);
        initUI();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tbShapeSettings.setTitle("外观设置");
        tbShapeSettings.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbShapeSettings.setNavigationIcon(R.drawable.ic_arrow_back_white);

        fragmentList =new ArrayList<Fragment>();
        fragmentList.add(TouchLinearShapeFragment.newInstance("",""));
        fragmentList.add(TouchBallShapeFragment.newInstance("",""));

        adapter =new ViewPagerAdapter(getSupportFragmentManager(),fragmentList,TITLE);
        vpSettingShape.setAdapter(adapter);

        //实例化TabPageIndicator然后设置ViewPager与之关联
        tlSettingShape.setupWithViewPager(vpSettingShape);

        //如果我们要对ViewPager设置监听，用indicator设置就行了
        tlSettingShape.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpSettingShape.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vpSettingShape.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position==0){
                    MyApplication.setIsSettingShape(true);
                }else {
                    MyApplication.setIsSettingShape(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }



    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;
        private String[] TITLE;

        public ViewPagerAdapter(android.support.v4.app.FragmentManager fm,List<Fragment> fragments,String[] TITLE) {
            super(fm);
            this.fragments=fragments;
            this.TITLE=TITLE;
        }


        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return TITLE.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position];
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        //当且仅当当前页面是Linear的时候就设置悬浮条点击事件是设置颜色
        if (vpSettingShape.getCurrentItem()==0){
            MyApplication.setIsSettingShape(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当俩开Linear界面的时候还原悬浮条点击事件
        MyApplication.setIsSettingShape(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
