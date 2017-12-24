package com.skkk.easytouch.View.BallDrawableSelect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BallDrawableSelectActivity extends AppCompatActivity {

    @Bind(R.id.tb_ball_drawable_select)
    Toolbar tbBallDrawableSelect;
    @Bind(R.id.rv_ball_drawable_select)
    RecyclerView rvBallDrawableSelect;

    private GridLayoutManager layoutManager;
    private BallDrawableSelectAdapter adapter;
    private List<String> mDataList;
    private final int COLUMN_COUNT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball_drawable_select);
        ButterKnife.bind(this);
        initUI();
        initEvent();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tbBallDrawableSelect.setTitle("选择悬浮球背景");
        tbBallDrawableSelect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbBallDrawableSelect.setNavigationIcon(R.drawable.ic_arrow_back_white);

        layoutManager = new GridLayoutManager(BallDrawableSelectActivity.this, COLUMN_COUNT);
        mDataList = getDrawableList();
        mDataList.add(0,"ic_add_gray");
        adapter = new BallDrawableSelectAdapter(BallDrawableSelectActivity.this, mDataList);
        rvBallDrawableSelect.setLayoutManager(layoutManager);
        rvBallDrawableSelect.setAdapter(adapter);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        adapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                SpUtils.saveString(getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL, mDataList.get(pos));
                finish();
            }
        });
    }

    /**
     * 获取所有图片的名字
     *
     * @return
     */
    public List<String> getDrawableList() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            names.add("ball_" + i);
        }
        return names;
    }
}
