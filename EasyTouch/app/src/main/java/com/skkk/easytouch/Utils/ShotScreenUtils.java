package com.skkk.easytouch.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.os.AsyncTaskCompat;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 * 创建于 2018/1/27
 * 作者 admin
 */
/*
* 
* 描    述：
* 作    者：ksheng
* 时    间：2018/1/27$ 14:23$.
*/
public class ShotScreenUtils {

    public interface OnShotScreenListener {
        void startShotScreen();

        void finishShotScreen(Uri uri);

        void failedShotScreen();
    }

    private OnShotScreenListener onShotScreenListener;

    private static final String TAG = "ShotScreenUtils";

    private ImageReader mImageReader;
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;
    private Intent resultDate;//录屏允许数据
    private Context context;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;


    private ShotScreenUtils() {
    }

    public static ShotScreenUtils getInstance() {
        return ShotScreenUtilsHolder.sInstance;
    }

    private static class ShotScreenUtilsHolder {
        private static final ShotScreenUtils sInstance = new ShotScreenUtils();
    }


    /**
     * 开始截屏
     */
    public void startScreenShot() {
        if (onShotScreenListener != null) {
            onShotScreenListener.startShotScreen();
        }

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                //start virtual
                startVirtual();
            }
        }, 5);

        handler1.postDelayed(new Runnable() {
            public void run() {
                //capture the screen
                startCapture();
            }
        }, 30);
    }


    private void createImageReader() {
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);
    }

    private void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    private void setUpMediaProjection() {
        if (resultDate == null) {
            Log.e(TAG, "setUpMediaProjection: 获取权限返回数据发生异常！");
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, resultDate);
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                screenWidth, screenHeight, screenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            startScreenShot();
        } else {
            SaveTask mSaveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    private class SaveTask extends AsyncTask<Image, Void, Uri> {

        @Override
        protected Uri doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                return saveImageGallery(context, bitmap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);
            //预览图片
            if (uri != null) {
                if (onShotScreenListener != null) {
                    onShotScreenListener.finishShotScreen(uri);
                }
            }
        }
    }


    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }


    private Uri saveImageGallery(Context context, Bitmap bitmap) {
        String diranme = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + "ScreenShots";
        File fileDir = new File(diranme);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        long mImageTime = System.currentTimeMillis();
        long dateSeconds = mImageTime / 1000;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);

        String mImageFileName = "Screenshot_"
                + year + "-"
                + month + "-"
                + day + "-" + hour + "-" + minute + "-" + second + "-" + millisecond + "_" + context.getPackageName() + ".png";
        String mImageFilePath = diranme + File.separator + mImageFileName;

        int mImageWidth = bitmap.getWidth();
        int mImageHeight = bitmap.getHeight();

        ContentValues values = new ContentValues();
        ContentResolver resolver = context.getContentResolver();
        values.put(MediaStore.Images.ImageColumns.DATA, mImageFilePath);
        values.put(MediaStore.Images.ImageColumns.TITLE, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, mImageFileName);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, mImageTime);
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.DATE_MODIFIED, dateSeconds);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.ImageColumns.WIDTH, mImageWidth);
        values.put(MediaStore.Images.ImageColumns.HEIGHT, mImageHeight);
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream out = resolver.openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        values.clear();
        values.put(MediaStore.Images.ImageColumns.SIZE, new File(mImageFilePath).length());
        resolver.update(uri, values, null, null);

        Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(mImageFilePath));
        media.setData(contentUri);
        context.sendBroadcast(media);
        return contentUri;
    }

    /**
     * 设置尺寸
     *
     * @param screenWidth
     * @param screenHeight
     * @return
     */
    public ShotScreenUtils setShotSize(int screenWidth, int screenHeight, int screenDensity) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.screenDensity = screenDensity;
        return this;
    }

    /**
     * 设置上下文
     *
     * @param context
     * @return
     */
    public ShotScreenUtils setContext(Context context) {
        this.context = context;
        return this;
    }

    /**
     * 设置权限允许返回数据
     *
     * @param resultData
     * @return
     */
    public ShotScreenUtils setResultData(Intent resultData) {
        this.resultDate = resultData;
        createImageReader();
        return this;
    }

    /**
     * 设置截屏监听
     *
     * @param onShotScreenListener
     */
    public void setOnShotScreenListener(OnShotScreenListener onShotScreenListener) {
        this.onShotScreenListener = onShotScreenListener;
    }

    /**
     * 释放
     */
    public void release(){
        tearDownMediaProjection();
        stopVirtual();
    }
}
