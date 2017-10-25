package com.skkk.easytouch;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.settings_item_float)
    SettingItemView settingsItemFloat;
    @Bind(R.id.settings_item_assist)
    SettingItemView settingsItemAssist;
    @Bind(R.id.settings_item_theme)
    SettingItemView settingsItemTheme;
    @Bind(R.id.settings_item_shape)
    SettingItemView settingsItemShape;
    @Bind(R.id.btn_touch_ball)
    Button btnTouchBall;
    @Bind(R.id.content_main)
    LinearLayout contentMain;
    @Bind(R.id.settings_item_lock)
    SettingItemView settingsItemLock;
    private ArrayList<String> needRequestPermissions = new ArrayList<>();
    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.SYSTEM_ALERT_WINDOW
    };
    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initUI();

    }

    /**
     * 设置UI
     */
    private void initUI() {
        /**
         * 判断是否有无障碍权限
         */
        if (!isAccessibilityServiceRunning("FloatService")) {
            settingsItemAssist.setValue("未开启");
            settingsItemAssist.setSettingItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showDialog(MainActivity.this, R.drawable.ic_warning, "提醒", "为了保证EasyTouch的正常使用，您需要开启无障碍权限！",
                            "前往设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
                                }
                            }, "退出应用", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            });
        } else {
            settingsItemAssist.setValue("已开启");
            settingsItemAssist.setSettingItemClickListener(null);
        }

        if (Build.VERSION.SDK_INT >= M) {
            if (!Settings.canDrawOverlays(this)) {
                settingsItemFloat.setValue("未开启");
                settingsItemFloat.setSettingItemClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });

            } else {
                settingsItemFloat.setValue("已开启");
                settingsItemFloat.setSettingItemClickListener(null);
            }
        }

        initEvent();

        //如果设备管理器尚未激活，这里会启动一个激活设备管理器的Intent,具体的表现就是第一次打开程序时，手机会弹出激活设备管理器的提示，激活即可。
        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!mDPM.isAdminActive(mAdminName)) {
            settingsItemLock.setValue("未开启");
            settingsItemLock.setSettingItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAdminManagement(mAdminName);
                }
            });
        }else {
            settingsItemLock.setValue("已开启");
            settingsItemLock.setSettingItemClickListener(null);
        }




    }

    /**
     * 申请悬浮窗权限
     */
    private void checkAlertWindowPermission() {
        if (Build.VERSION.SDK_INT >= M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void initEvent() {
        btnTouchBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, EasyTouchService.class));
                startService(new Intent(MainActivity.this, FloatService.class));
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 判断是否包含所有的权限
     *
     * @param grantResults
     * @return
     */
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否存在置顶的无障碍服务
     *
     * @param name
     * @return
     */
    public boolean isAccessibilityServiceRunning(String name) {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enableServices
                = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo enableService : enableServices) {
            Log.i(TAG, "installService.id-->" + enableService.getId());
            if (enableService.getId().endsWith(name)) {
                return true;
            }
        }
        return false;
    }

    //激活设备管理器
    private void showAdminManagement(ComponentName mAdminName) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "activity device");
        startActivityForResult(intent, 1);
    }
}
