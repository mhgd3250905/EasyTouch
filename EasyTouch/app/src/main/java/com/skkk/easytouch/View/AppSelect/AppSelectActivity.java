package com.skkk.easytouch.View.AppSelect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AppSelectActivity extends AppCompatActivity {

    @Bind(R.id.fl_app_select)
    FrameLayout flAppSelect;
    @Bind(R.id.tb_app_select)
    Toolbar tbAppSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);
        ButterKnife.bind(this);

        tbAppSelect.setTitle("快捷应用选择");
        tbAppSelect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbAppSelect.setNavigationIcon(R.drawable.ic_arrow_back_white);

        int appIndex = getIntent().getIntExtra(Configs.KEY_BALL_MENU_SELECT_APP_INDEX, 0);
        int appType = getIntent().getIntExtra(Configs.KEY_APP_TYPE, 0);
        int touchType = getIntent().getIntExtra(Configs.KEY_TOUCH_TYPE, 0);

        AppFragment appFragment = AppFragment.newInstance(appIndex, appType, touchType);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_app_select, appFragment)
                .commit();
    }
}
