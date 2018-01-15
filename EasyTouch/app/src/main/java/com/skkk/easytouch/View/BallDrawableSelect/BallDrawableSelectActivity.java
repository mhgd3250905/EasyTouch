package com.skkk.easytouch.View.BallDrawableSelect;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.R;
import com.skkk.easytouch.Utils.DialogUtils;
import com.skkk.easytouch.Utils.IntentUtils;
import com.skkk.easytouch.Utils.PermissionsUtils;
import com.skkk.easytouch.Utils.SpUtils;
import com.skkk.easytouch.View.BaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.os.Build.VERSION_CODES.M;

public class BallDrawableSelectActivity extends AppCompatActivity {
    private static final String TAG = "BallDrawableSelectActiv";
    @Bind(R.id.tb_ball_drawable_select)
    Toolbar tbBallDrawableSelect;
    @Bind(R.id.rv_ball_drawable_select)
    RecyclerView rvBallDrawableSelect;

    private GridLayoutManager layoutManager;
    private BallDrawableSelectAdapter adapter;
    private List<String> mDataList;
    private final int COLUMN_COUNT = 4;
    private Uri mPhotoUri;

    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案

    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数


    private ArrayList<String> needRequestPermissions = new ArrayList<>();


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
        mDataList.add(0, "ic_add_gray");
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
                if (pos == 0) {
                    if (Build.VERSION.SDK_INT >= M) {
                        //版本为6.0以上，那么进行权限检测
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
                            //如果已经具备了权限，那么可以操作
                            selectPhoto();
                        }else {
                            initPermissions();
                        }
                    } else {
                        //版本为6.0以下，直接进行操作
                        selectPhoto();//选择照片
                    }
                } else {
                    SpUtils.saveString(getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL, mDataList.get(pos));
                    finish();
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "requestCode:" + requestCode + " resultCode:" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Configs.RESULT_PHOTO_REQUEST_TAKE_PHOTO:    //拍照返回插入图片到编辑框
                    Uri imageUri = Uri.fromFile(new File(Configs.SAVED_IMAGE_DIR_PATH + Configs.SAVED_IMAGE_NAME));
                    IntentUtils.startPhotoZoom(BallDrawableSelectActivity.this,imageUri,200);
                    break;
                case Configs.RESULT_PHOTO_REQUEST_GALLERY:    //相册选取返回并插入图片到编辑框
                    if (data != null) {
                        Uri uriImageFromGallery = data.getData();
                        IntentUtils.startPhotoZoom(BallDrawableSelectActivity.this,uriImageFromGallery,200);
                    }
                    break;
                case Configs.RESULT_PHOTO_REQUEST_CUT:
                    SpUtils.saveString(getApplicationContext(), Configs.KEY_TOUCH_UI_BACKGROUND_BALL, Configs.KEY_PHOTO_CUSTOM_DRAWABLE);
                    finish();
                    break;
            }
        }
    }

    /**
     * 选择照片
     */
    public void selectPhoto() {
        //判断是否存在目录
        File fileDir=new File(Configs.SAVED_IMAGE_DIR_PATH);
        if (!fileDir.exists()){
            fileDir.mkdirs();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://拍照
                        IntentUtils.takePhoto(BallDrawableSelectActivity.this, Configs.SAVED_IMAGE_DIR_PATH + Configs.SAVED_IMAGE_NAME);
                        break;
                    case 1://相册
                        IntentUtils.takeGallery(BallDrawableSelectActivity.this);
                        break;
                }
            }
        });
        builder.show();
    }


    /**
     * 获取所有图片的名字
     *
     * @return
     */
    public List<String> getDrawableList() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            names.add("ball_" + i);
        }
        return names;
    }

    /**
     * 检测权限
     */
    @RequiresApi(api = M)
    private void initPermissions() {
        if (PermissionsUtils.lacksPermissions(BallDrawableSelectActivity.this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS);
        }
    }

    // 请求权限兼容低版本
    @TargetApi(M)
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
            DialogUtils.showDialog(BallDrawableSelectActivity.this, R.drawable.ic_warning,
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
}