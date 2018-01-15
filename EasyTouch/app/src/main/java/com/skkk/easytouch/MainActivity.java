package com.skkk.easytouch;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
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
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import com.skkk.easytouch.Receiver.AdminManageReceiver;
import com.skkk.easytouch.Services.EasyTouchBallService;
import com.skkk.easytouch.Services.EasyTouchLinearService;
import com.skkk.easytouch.Services.FloatService;
import com.skkk.easytouch.Utils.DialogUtils;
import com.skkk.easytouch.Utils.ServiceUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.AboutActivity;
import com.skkk.easytouch.View.FunctionSelect.FuncConfigs;
import com.skkk.easytouch.View.FunctionSelect.FunctionSelectActivity;
import com.skkk.easytouch.View.ScaleScrollView;
import com.skkk.easytouch.View.SettingItemView;
import com.skkk.easytouch.View.ShapeSetting.ShapeSettingActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.M;
import static com.skkk.easytouch.R.id.settings_item_about;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.settings_item_float)
    SettingItemView settingsItemFloat;
    @Bind(R.id.settings_item_assist)
    SettingItemView settingsItemAssist;
    @Bind(R.id.settings_item_lock)
    SettingItemView settingsItemLock;
    @Bind(R.id.settings_item_shape)
    SettingItemView settingsItemShape;
    @Bind(R.id.settings_item_func)
    SettingItemView settingsItemFunc;
    @Bind(R.id.btn_touch_line)
    TextView btnTouchLine;
    @Bind(R.id.btn_touch_ball)
    TextView btnTouchBall;
    @Bind(R.id.content_main)
    ScaleScrollView contentMain;
    @Bind(settings_item_about)
    SettingItemView settingsItemAbout;


    private ArrayList<String> needRequestPermissions = new ArrayList<>();

    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    private ComponentName mAdminName;
    private DevicePolicyManager mDPM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initFirstRunData();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                && !notificationManager.isNotificationPolicyAccessGranted()) {
//            Intent intent = new Intent(
//                    Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//            startActivity(intent);
//        }
    }

    /**
     * 判断是否为第一次运行
     * 如果是第一次运行那么就需要设置初始化的功能以及菜单选项
     */
    private void initFirstRunData() {
        boolean isFirstRun = SpUtils.getBoolean(getApplicationContext(), SpUtils.KEY_APP_IS_FIRST_RYN, true);
        if (isFirstRun) {
            //设置第一次进入时候的悬浮球、悬浮条、二级菜单功能
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_CLICK, FuncConfigs.Func.BACK.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_LONG_CLICK, FuncConfigs.Func.MENU.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_FLING_UP, FuncConfigs.Func.HOME.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_FLING_LEFT, FuncConfigs.Func.PREVIOUS_APP.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_FLING_BOTTOM, FuncConfigs.Func.NOTIFICATION.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_FLING_RIGHT, FuncConfigs.Func.RECENT.getValue());

            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_TOP_CLICK, FuncConfigs.Func.RECENT.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_TOP_FLING_UP, FuncConfigs.Func.PREVIOUS_APP.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_TOP_FLING_BOTTOM, FuncConfigs.Func.NOTIFICATION.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MID_CLICK, FuncConfigs.Func.HOME.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_BOTTOM_CLICK, FuncConfigs.Func.BACK.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_BOTTOM_FLING_UP, FuncConfigs.Func.MENU.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_BOTTOM_FLING_BOTTOM, FuncConfigs.Func.LOCK_SCREEN.getValue());

            SpUtils.saveInt(getApplicationContext(), SpUtils.KEY_MENU_BALL_COUNT, 4);
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MENU_BALL + 0, FuncConfigs.Func.TRUN_POS.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MENU_BALL + 1, FuncConfigs.Func.VOICE_MENU.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MENU_BALL + 2, FuncConfigs.Func.APP_MENU.getValue());
            SpUtils.saveInt(getApplicationContext(), FuncConfigs.VALUE_FUNC_OP_MENU_BALL + 3, FuncConfigs.Func.LOCK_SCREEN.getValue());

            SpUtils.saveBoolean(getApplicationContext(), SpUtils.KEY_APP_IS_FIRST_RYN, false);
        }
    }


    /**
     * 设置UI
     */
    private void initUI() {
        /**
         * 判断是否有无障碍权限
         */
        if (!isAccessibilityServiceRunning("FloatService")) {
            settingsItemAssist.setWarning("未开启，操作功能无法使用");
        } else {
            settingsItemAssist.setValue("已开启");
        }

        /**
         * 判断是否有悬浮窗权限
         */
        if (Build.VERSION.SDK_INT >= M) {
            if (!Settings.canDrawOverlays(this)) {
                settingsItemFloat.setWarning("未开启，无法显示悬浮内容");
            } else {
                settingsItemFloat.setValue("已开启");
            }
        }

        /**
         * 判断是否有锁屏权限
         */
        //如果设备管理器尚未激活，这里会启动一个激活设备管理器的Intent,具体的表现就是第一次打开程序时，手机会弹出激活设备管理器的提示，激活即可。
        mAdminName = new ComponentName(this, AdminManageReceiver.class);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!mDPM.isAdminActive(mAdminName)) {
            settingsItemLock.setWarning("未开启，锁屏功能无法使用");
        } else {
            settingsItemLock.setValue("已开启");
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

        settingsItemAssist.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showDialog(MainActivity.this, R.drawable.ic_warning, "提醒", "为了保证EasyTouch的正常使用，您需要开启无障碍权限！",
                        "前往设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
                            }
                        }, "算了", null).show();
            }
        });


        settingsItemFloat.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        settingsItemLock.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdminManagement(mAdminName);
            }
        });


        //设置形状
        settingsItemShape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShapeSettingActivity.class));
            }
        });

        //设置功能
        settingsItemFunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FunctionSelectActivity.class));
            }
        });

        btnTouchLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, Configs.RESULT_PERMISS_REQUEST_FLOAT_LINEAR);
                        return;
                    } else {
                        if (ServiceUtils.isServiceRun(getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
                            stopService(new Intent(MainActivity.this, EasyTouchBallService.class));
                        }
                        startService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                        startService(new Intent(MainActivity.this, FloatService.class));
                    }
                } else {
                    if (ServiceUtils.isServiceRun(getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
                        stopService(new Intent(MainActivity.this, EasyTouchBallService.class));
                    }
                    startService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                    startService(new Intent(MainActivity.this, FloatService.class));

                }


            }
        });

        btnTouchBall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= M) {
                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, Configs.RESULT_PERMISS_REQUEST_FLOAT_BALL);
                        return;
                    } else {
                        if (ServiceUtils.isServiceRun(getApplicationContext(),Configs.NAME_SERVICE_TOUCH_LINEAR)) {
                            stopService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                        }
                        startService(new Intent(MainActivity.this, EasyTouchBallService.class));
                        startService(new Intent(MainActivity.this, FloatService.class));
                    }
                } else {
                    if (ServiceUtils.isServiceRun(getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
                        stopService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                    }
                    startService(new Intent(MainActivity.this, EasyTouchBallService.class));
                    startService(new Intent(MainActivity.this, FloatService.class));
                }

            }
        });

        settingsItemAbout.setSettingItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Configs.RESULT_PERMISS_REQUEST_FLOAT_LINEAR) {
            if (Build.VERSION.SDK_INT >= M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, Configs.RESULT_PERMISS_REQUEST_FLOAT_LINEAR);
                } else {
                    if (ServiceUtils.isServiceRun(getApplicationContext(), Configs.NAME_SERVICE_TOUCH_BALL)) {
                        stopService(new Intent(MainActivity.this, EasyTouchBallService.class));
                    }
                    startService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                    startService(new Intent(MainActivity.this, FloatService.class));
                }
            }
        } else if (requestCode == Configs.RESULT_PERMISS_REQUEST_FLOAT_BALL) {
            if (Build.VERSION.SDK_INT >= M) {
                if (!Settings.canDrawOverlays(MainActivity.this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, Configs.RESULT_PERMISS_REQUEST_FLOAT_BALL);
                } else {
                    if (ServiceUtils.isServiceRun(getApplicationContext(), Configs.NAME_SERVICE_TOUCH_LINEAR)) {
                        stopService(new Intent(MainActivity.this, EasyTouchLinearService.class));
                    }
                    startService(new Intent(MainActivity.this, EasyTouchBallService.class));
                    startService(new Intent(MainActivity.this, FloatService.class));
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
        initEvent();

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
//            Log.i(TAG, "installService.id-->" + enableService.getId());
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