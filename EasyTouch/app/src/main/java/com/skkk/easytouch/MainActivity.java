package com.skkk.easytouch;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
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

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    Button btnTouchBall;
    Button btnTouchReact;
    LinearLayout contentMain;

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnTouchBall = (Button) findViewById(R.id.btn_touch_ball);
        btnTouchReact = (Button) findViewById(R.id.btn_touch_react);
        contentMain = (LinearLayout) findViewById(R.id.content_main);
        setSupportActionBar(toolbar);


        /**
         * 判断是否有无障碍权限
         */
        if (!isAccessibilityServiceRunning("FloatService"))
            DialogUtils.showDialog(this, R.drawable.ic_warning, "提醒", "为了保证EasyTouch的正常使用，您需要开启无障碍权限！",
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

        initPermissions();
        initEvent();

        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        //如果设备管理器尚未激活，这里会启动一个激活设备管理器的Intent,具体的表现就是第一次打开程序时，手机会弹出激活设备管理器的提示，激活即可。
        if (!mDPM.isAdminActive(mAdminName)) {
            showAdminManagement(mAdminName);
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

    /**
     * 检测权限
     */
    private void initPermissions() {
        if (PermissionsUtils.lacksPermissions(MainActivity.this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS);
        }
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

    // 请求权限兼容低版本
    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(String... permissions) {
        needRequestPermissions.clear();
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (PermissionsUtils.lacksPermission(this, PERMISSIONS[i])) {
                needRequestPermissions.add(PERMISSIONS[i]);
            }
        }
        String[] permissionArr = new String[needRequestPermissions.size()];
        needRequestPermissions.toArray(permissionArr);
        requestPermissions(permissionArr, PERMISSION_REQUEST_CODE);
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {

        } else {
            DialogUtils.showDialog(MainActivity.this, R.drawable.ic_warning,
                    "提醒", "当前应用缺少必要权限，\n请点击\"设置\"-\"权限\"打开所需要的权限。",
                    "设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
                            startActivity(intent);
                        }
                    }, "算了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();

                        }
                    }).show();
        }

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
        startActivityForResult(intent,1);
    }
}
