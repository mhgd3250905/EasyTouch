package com.skkk.easytouch.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.skkk.easytouch.Configs;
import com.skkk.easytouch.MainActivity;

import java.io.File;

/**
 * 创建于 2017/11/13
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2017/11/13$ 22:25$.
*/
public class IntentUtils {
    //酷市场 -- 酷安网
    public static final String PACKAGE_COOL_MARKET = "com.coolapk.market";


    /**
     * 跳转到微信
     */
    public static void toWeChatScan(Context context) {
        try {
            //利用Intent打开微信
            Uri uri = Uri.parse("weixin://");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(context, "无法跳转到微信，请检查您是否安装了微信！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到支付宝扫描界面
     */
    public static void toAliPayScan(Context context) {
        try {
            //利用Intent打开支付宝
            //支付宝跳过开启动画打开扫码和付款码的url scheme分别是alipayqr://platformapi/startapp?saId=10000007和
            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=10000007");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(context, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转刀片支付宝库款界面
     */
    public static void toAliPayCode(Context context) {
        try {
            //利用Intent打开支付宝
            //支付宝跳过开启动画打开扫码和付款码的url scheme分别是alipayqr://platformapi/startapp?saId=10000007和
            //alipayqr://platformapi/startapp?saId=20000056
            Uri uri = Uri.parse("alipayqr://platformapi/startapp?saId=20000056");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        } catch (Exception e) {
            //若无法正常跳转，在此进行错误处理
            Toast.makeText(context, "无法跳转到支付宝，请检查您是否安装了支付宝！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转到微信页面
     *
     * @param context
     */
    public static void toWeChatScanDirect(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /*****
     * 进行截图
     *
     * @param context Activity上下文对象
     * @param uri     Uri :如果是Android7.0传入null，如果不是那么传入图片Uri
     * @param size    大小
     */
    public static void startPhotoZoom(Activity context, Uri uri, int size) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        //获取被裁剪图片路径
        File inputFile = new File(Configs.SAVED_IMAGE_DIR_PATH+Configs.SAVED_IMAGE_NAME);
        File outputFile = inputFile;//设置图片输出文件，这里设置为覆盖输入图片路径（在拍照裁剪情况下）

        if (Build.VERSION.SDK_INT < 24) {//如果Android版本低于24
            intent.setDataAndType(uri, "image/*");

        } else {//如果是Android7.0
            if (uri != null) {//如果是相册传入
                inputFile = FileUtils.getFileByUri(context, uri);
            }
            intent.addFlags(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
            intent.setDataAndType(FileUtils.getImageContentUri(context, inputFile), "image/*");
        }

        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);

        intent.putExtra("circleCrop",true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        //设置裁剪完毕输出图片路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputFile));
        intent.putExtra("return-data", false);//设置为不返回数据

        //将保存用户图片的Uri保存
        SpUtils.saveString(context,Configs.KEY_TOUCH_UI_BACKGROUND_BALL_CUSTOM, Uri.fromFile(outputFile).toString());
        context.startActivityForResult(intent, Configs.RESULT_PHOTO_REQUEST_CUT);
    }


    /**
     * 进入相机界面
     */
    public static void takePhoto(Activity context,String cameraPath) {
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            String out_file_path = Configs.SAVED_IMAGE_DIR_PATH;
            File dir = new File(out_file_path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 把文件地址转换成Uri格式
            Uri uri;
            if (Build.VERSION.SDK_INT < 24) {
                // 从文件中创建uri
                uri = Uri.fromFile(new File(cameraPath));
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, cameraPath);
                uri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", new File(cameraPath));
            }
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            context.startActivityForResult(intent, Configs.RESULT_PHOTO_REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(context.getApplicationContext(), "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 进入相册选取图片界面
     */
    public static void takeGallery(Activity context) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        context.startActivityForResult(intent, Configs.RESULT_PHOTO_REQUEST_GALLERY);
    }

    /**
     * 跳转到应用市场
     */
    public static void jump2AppMarket(Activity activity) {
        if (PackageUtils.checkAppExist(activity,PACKAGE_COOL_MARKET)) {
            //跳转到应用市场
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + activity.getPackageName()));
            intent.setPackage(PACKAGE_COOL_MARKET);
            activity.startActivity(intent);
        }else {
            //https://www.coolapk.com/apk/com.skkk.easytouch
            String coolMarketUrl="https://www.coolapk.com/apk/com.skkk.easytouch";
            Uri uri = Uri.parse(coolMarketUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        }
    }
}
