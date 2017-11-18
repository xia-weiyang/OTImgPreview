package com.jiushig.imgpreview;

import android.app.Activity;
import android.widget.Toast;

import com.jiushig.imgpreview.ui.ImageActivity;

/**
 * Created by zk on 2017/11/18.
 */

public class ImageBuilder {

    private Activity activity;

    private String[] urls;
    private String currentUrl;

    public ImageBuilder(Activity activity) {
        this.activity = activity;
    }

    public ImageBuilder setUrls(String[] urls) {
        this.urls = urls;
        return this;
    }

    public ImageBuilder setCurrentUrl(String currentUrl){
        this.currentUrl = currentUrl;
        return this;
    }

    public void start() {
        if (urls == null || urls.length <= 0) {
            Toast.makeText(activity, "没有要打开的图片", Toast.LENGTH_LONG).show();
            return;
        }

        if (currentUrl == null || currentUrl.isEmpty()) {
            currentUrl = urls[0];
        }
        ImageActivity.start(activity, urls, currentUrl);
    }
}
