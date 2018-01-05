package com.skkk.easytouch.View.FunctionSelect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skkk.easytouch.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FunctionSelectActivity extends AppCompatActivity {

    private static final String TAG = "FunctionSelectActivity";
    @Bind(R.id.tb_function_select)
    Toolbar tbFunctionSelect;
    @Bind(R.id.tl_function_select)
    TabLayout tlFunctionSelect;
    @Bind(R.id.vp_function_select)
    ViewPager vpFunctionSelect;

    private ArrayList<Fragment> fragmentList;
    private String[] TITLE = new String[]{"悬浮条", "悬浮球"};
    private ViewPagerAdapter adapter;
    private Fragment ballfragment;
    private Fragment linearfragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_select);
        ButterKnife.bind(this);
        initData();
        initUI();
    }

    /**
     * 初始化Data
     */
    private void initData() {
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tbFunctionSelect.setTitle("功能选择");
        tbFunctionSelect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbFunctionSelect.setNavigationIcon(R.drawable.ic_arrow_back_white);

        fragmentList = new ArrayList<Fragment>();
        linearfragment = FunctionBallFragment.newInstance("","");
        ballfragment = FunctionBallFragment.newInstance("","");
        fragmentList.add(linearfragment);
        fragmentList.add(ballfragment);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, TITLE);
        vpFunctionSelect.setAdapter(adapter);

        //实例化TabPageIndicator然后设置ViewPager与之关联
        tlFunctionSelect.setupWithViewPager(vpFunctionSelect);

        //如果我们要对ViewPager设置监听，用indicator设置就行了
        tlFunctionSelect.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpFunctionSelect.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        linearfragment.onActivityResult(requestCode,resultCode,data);
        ballfragment.onActivityResult(requestCode,resultCode,data);
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragments;
        private String[] TITLE;

        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] TITLE) {
            super(fm);
            this.fragments = fragments;
            this.TITLE = TITLE;
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

}
