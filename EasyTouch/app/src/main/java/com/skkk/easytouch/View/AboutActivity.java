package com.skkk.easytouch.View;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.skkk.easytouch.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.tb_about)
    Toolbar tbAbout;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.settings_item_version)
    SettingItemView settingsItemVersion;
    @Bind(R.id.settings_item_email)
    SettingItemView settingsItemEmail;
    @Bind(R.id.settings_item_qq)
    SettingItemView settingsItemQq;
    @Bind(R.id.settings_item_github)
    SettingItemView settingsItemGithub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        tbAbout.setTitle("其他");
        tbAbout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tbAbout.setNavigationIcon(R.drawable.ic_arrow_back_white);
        settingsItemVersion.setValue(getAppVersionName(this));
    }

    @OnClick({R.id.settings_item_email, R.id.settings_item_qq, R.id.settings_item_github})
    public void onViewClicked(View view) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        ClipData mClipData =null;
        String type="";
        switch (view.getId()) {
            case R.id.settings_item_email:
                mClipData =ClipData.newPlainText("Label", "294851575@qq.com");
                type="邮箱地址";
                break;
            case R.id.settings_item_qq:
                mClipData =ClipData.newPlainText("Label", "694193470");
                type="QQ群号";
                break;
            case R.id.settings_item_github:
                mClipData =ClipData.newPlainText("Label", "https://github.com/mhgd3250905");
                type="Github地址";
               break;
        }
        cm.setPrimaryClip(mClipData);
        Toast.makeText(this, "成功复制"+type, Toast.LENGTH_LONG).show();

    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
//            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
