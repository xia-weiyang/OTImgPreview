package com.jiushig.imgpreview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Guowang on 2016/12/25.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();


    /**
     * 保存图片
     *
     * @param context
     * @param bmp
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        try {
            File appDir = new File(context.getCacheDir() + "/saveImg");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return false;
    }


    /**
     * 保存图片
     *
     * @param context
     * @param fileSource
     */
    public static boolean saveImageToGallery(Context context, File fileSource) {
        // 首先保存图片
        try {
            File appDir = new File(context.getCacheDir() + "/saveImg");
            if (!appDir.exists()) {
                appDir.mkdirs();
            }

            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);

            FileOutputStream fosto = new FileOutputStream(file);
            InputStream fosfrom = new FileInputStream(fileSource);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();

            // 其次把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);

            // 最后通知图库更新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) { // 判断SDK版本是不是4.4或者高于4.4
                String[] paths = new String[]{file.getAbsolutePath()};
                MediaScannerConnection.scanFile(context, paths, null, null);
            }
            return true;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

    }
}
