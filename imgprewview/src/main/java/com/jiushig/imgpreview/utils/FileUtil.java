package com.jiushig.imgpreview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * @param path
     * @param bmp
     */
    public static boolean saveImageToGallery(Context context, String path, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory() + "/" + path);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return true;
    }


    /**
     * 保存图片
     *
     * @param context
     * @param path
     * @param fileSource
     */
    public static boolean saveImageToGallery(Context context, String path, File fileSource) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory() + "/" + path);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);

        try {
            FileOutputStream fosto = new FileOutputStream(file);
            InputStream fosfrom = new FileInputStream(fileSource);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return true;
    }
}
