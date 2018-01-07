package com.skkk.easytouch.View.FunctionSelect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skkk.easytouch.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FunctionDetailSelectActivity extends AppCompatActivity {

    private static final String TAG = "ShapeSettingActivity";
    @Bind(R.id.tb_function_detail_select)
    Toolbar tbFunctionDetailSelect;


    //    private ArrayList<Fragment> fragmentList;
//    private String[] TITLE = new String[]{"基础操作"};
//    private ViewPagerAdapter adapter;
    private String opType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function_detail_select);
        ButterKnife.bind(this);
        initData();
        initUI();
    }

    /**
     * 初始化Data
     */
    private void initData() {
        opType = getIntent().getStringExtra(FuncConfigs.KEY_FUNC_OP);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tbFunctionDetailSelect.setTitle("功能选择");
        tbFunctionDetailSelect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbFunctionDetailSelect.setNavigationIcon(R.drawable.ic_arrow_back_white);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_function_detail_base, FunctionDetailBaseFragment.newInstance(opType, ""))
                .commit();
//        fragmentList = new ArrayList<Fragment>();
//        fragmentList.add(FunctionDetailBaseFragment.newInstance(opType, ""));
////        fragmentList.add(FunctionDetailAppFragment.newInstance(opType, ""));
////        fragmentList.add(FunctionDetailShortCutFragment.newInstance(opType, ""));
//
//        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, TITLE);
//        vpFunctionDetailSelect.setAdapter(adapter);
//
//        //实例化TabPageIndicator然后设置ViewPager与之关联
//        tlFunctionDetailSelect.setupWithViewPager(vpFunctionDetailSelect);
//
//        //如果我们要对ViewPager设置监听，用indicator设置就行了
//        tlFunctionDetailSelect.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                vpFunctionDetailSelect.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }


//    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
//        private List<Fragment> fragments;
//        private String[] TITLE;
//
//        public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] TITLE) {
//            super(fm);
//            this.fragments = fragments;
//            this.TITLE = TITLE;
//        }
//
//
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return TITLE.length;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return TITLE[position];
//        }
//
//    }

}
