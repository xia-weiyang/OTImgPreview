package com.jiushig.imgpreview.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jiushig.imgpreview.R;
import com.jiushig.imgpreview.adapter.ViewPageAdapter;
import com.jiushig.imgpreview.widget.CustomViewPage;
import com.jiushig.imgpreview.widget.PinchImageView;

import java.util.ArrayList;

/**
 * Created by Guowang on 2017/2/6.
 * 加载多张图片
 */

public class ImageActivity extends AppCompatActivity {

    public static final String URLS = "urls";
    public static final String CURRENT_URL = "current_url";
    public static final String CURRENT_MODEL = "current_model";

    private CustomViewPage viewPager;
    private ViewPageAdapter adapter;
    private ArrayList<View> views;

    public static final int MODEL_SAVE = 0x1;   // 保存
    public static final int MODEL_DELETE = 0x2;   // 删除
    private int currentModel;

    private String deleteUrls = "";  // 删除的url

    public static final int REQUEST_CODE = 1356;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            resetPinchImageView(msg.what);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        String[] urls = getIntent().getStringArrayExtra(URLS);
        String currentUrl = getIntent().getStringExtra(CURRENT_URL);
        currentModel = getIntent().getIntExtra(CURRENT_MODEL, 0);

        views = getViews(urls);

        viewPager = (CustomViewPage) findViewById(R.id.viewPager);

        viewPager.setAdapter(adapter = new ViewPageAdapter(this, views));


        viewPager.setCurrentItem(getCurrentItem(urls, currentUrl));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.initImageStatus();
                handler.sendEmptyMessageDelayed(position, 500);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    /**
     * 重置position前后的PinchImageView状态
     *
     * @param position
     */
    private void resetPinchImageView(int position) {
        if (position + 1 < views.size()) {
            getPinchImageView(views.get(position + 1)).reset();
        }
        if (position - 1 >= 0) {
            getPinchImageView(views.get(position - 1)).reset();
        }
    }

    /**
     * 得到当前需要展示的图片的Item
     *
     * @param urls
     * @param currentUrl
     * @return
     */
    private int getCurrentItem(String[] urls, String currentUrl) {
        int current = 0;
        if (urls != null) {
            if (null != currentUrl && !currentUrl.isEmpty()) {
                for (int i = 0; i < urls.length; i++) {
                    if (currentUrl.equals(urls[i])) {
                        current = i;
                    }
                }
            }
        }
        return current;
    }

    /**
     * 得到View集合
     *
     * @param urls
     * @return
     */
    private ArrayList<View> getViews(String[] urls) {
        ArrayList<View> views = new ArrayList<>(1);
        LayoutInflater layoutInflater = LayoutInflater.from(ImageActivity.this);
        if (urls != null) {
            for (String url : urls) {
                View view = layoutInflater.inflate(R.layout.pinch_image, null);
                loadImage(view, url);
                views.add(view);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
            }
        }
        return views;
    }

    /**
     * 通过View得到PinchImageView对象
     *
     * @param view
     * @return
     */
    private PinchImageView getPinchImageView(View view) {
        return (PinchImageView) view.findViewById(R.id.image);
    }

    /**
     * 通过url加载图片
     *
     * @param view
     * @param url
     */
    private void loadImage(View view, String url) {
        final PinchImageView img = (PinchImageView) view.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        img.setVisibility(View.VISIBLE);
                        img.addOuterTouchOverListener(viewPager);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                        return false;

                    }
                })
                .into(img);
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(MODEL_DELETE + "", deleteUrls.split(","));
        setResult(RESULT_OK, intent);
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }



    /**
     * 启动当前界面
     *
     * @param activity
     * @param urls     图片地址数组
     * @param url      当前要展示的图片地址
     */
    public static void start(Activity activity, String[] urls, String url) {
        start(activity, urls, url, 0);
    }

    public static void start(Activity activity, String[] urls, String url, int model) {
        Intent intent = new Intent();
        intent.putExtra(ImageActivity.URLS, urls);
        intent.putExtra(ImageActivity.CURRENT_URL, url);
        intent.putExtra(ImageActivity.CURRENT_MODEL, model);
        intent.setClass(activity, ImageActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
