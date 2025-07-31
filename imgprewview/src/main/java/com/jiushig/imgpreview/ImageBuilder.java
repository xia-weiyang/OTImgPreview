package com.jiushig.imgpreview;

import android.app.Activity;
import android.widget.Toast;

import com.jiushig.imgpreview.ui.ImageActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zk on 2017/11/18.
 */

public class ImageBuilder {

    private Activity activity;

    public static final int MODEL_SAVE = 0x1;   // 保存
    public static final int MODEL_SAVE_BTN = 0x3;   // 保存并显示按钮

    private String[] urls;
    private String currentUrl;

    private int model;
    private boolean isLandscape = false;
    private boolean paintWhiteBgForPng = false;
    private HashMap<String, String> headers;

    public ImageBuilder(Activity activity) {
        this.activity = activity;
    }

    public ImageBuilder setUrls(String[] urls) {
        this.urls = urls;
        return this;
    }

    public ImageBuilder setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
        return this;
    }

    public ImageBuilder setModel(int model) {
        this.model = model;
        return this;
    }

    public ImageBuilder setLandscape(boolean landscape) {
        this.isLandscape = landscape;
        return this;
    }

    public ImageBuilder setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
        return this;
    }

    /**
     * 为png图片添加白色背景
     *
     * @param paintWhiteBgForPng 是否添加
     * @return
     */
    public ImageBuilder setPaintWhiteBgForPng(boolean paintWhiteBgForPng) {
        this.paintWhiteBgForPng = paintWhiteBgForPng;
        return this;
    }

    public void start() {
        if (urls == null || urls.length <= 0) {
            Toast.makeText(activity, R.string.img_empty, Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUrl == null || currentUrl.isEmpty()) {
            currentUrl = urls[0];
        }
        ImageActivity.start(activity, urls, currentUrl, model, headers, isLandscape, paintWhiteBgForPng);
    }
}
