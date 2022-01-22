package com.jiushig.imgpreview.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.jiushig.imgpreview.ImageBuilder;
import com.jiushig.imgpreview.R;
import com.jiushig.imgpreview.adapter.ViewPageAdapter;
import com.jiushig.imgpreview.utils.FileUtil;
import com.jiushig.imgpreview.utils.IntentMap;
import com.jiushig.imgpreview.utils.Permission;
import com.jiushig.imgpreview.widget.CustomViewPage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Guowang on 2017/2/6.
 * 加载多张图片
 */

public class ImageActivity extends AppCompatActivity {

    private static final String URLS = "urls";
    private static final String CURRENT_URL = "current_url";
    private static final String CURRENT_MODEL = "current_model";
    private static final String TAG = "ImageActivity";
    private static final String IS_LANDSCAPE = "is_landscape";

    private CustomViewPage viewPager;
    private ArrayList<View> views;
    private File currentFile;
    private TextView textIndex;
    private int currentIndex = 0;
    private HashMap<Integer, File> fileMap = new HashMap<>();
    private View btnSave;

    private int currentModel;
    private boolean isLandscape = false;

    public static final int REQUEST_CODE = 1356;

    private SharedPreferences preferences;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            resetPinchImageView(msg.what);
            // 重置放大缩小
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        isLandscape = getIntent().getBooleanExtra(IS_LANDSCAPE, false);
        setRequestedOrientation(isLandscape ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        preferences = getSharedPreferences("otimg", Context.MODE_PRIVATE);

        String[] urls = (String[]) IntentMap.get(getIntent().getStringExtra(URLS));
        String currentUrl = (String) IntentMap.get(getIntent().getStringExtra(CURRENT_URL));
        if (currentUrl == null) return;
        if (urls == null) return;

        currentModel = getIntent().getIntExtra(CURRENT_MODEL, 0);

        views = getViews(urls);

        final int currentItem = getCurrentItem(urls, currentUrl);

        viewPager = (CustomViewPage) findViewById(R.id.viewPager);
        textIndex = findViewById(R.id.text_index);
        textIndex.setVisibility(views.size() > 1 ? View.VISIBLE : View.GONE);
        if (textIndex.getVisibility() == View.VISIBLE) {
            textIndex.setText(String.format("%s/%s", currentItem + 1, views.size()));
        }

        viewPager.setAdapter(new ViewPageAdapter(this, views));

        viewPager.setCurrentItem(currentItem);
        currentIndex = currentItem;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                textIndex.setText(String.format("%s/%s", position + 1, views.size()));
                viewPager.initImageStatus();
                handler.sendEmptyMessageDelayed(position, 500);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        btnSave = findViewById(R.id.btn_save);
        if ((currentModel & ImageBuilder.MODEL_SAVE_BTN) == ImageBuilder.MODEL_SAVE_BTN) {
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentFile = fileMap.get(currentIndex);
                    saveImg();
                }
            });
        } else {
            btnSave.setVisibility(View.GONE);
        }


        // 不再显示提示信息
        //showTip();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImg();
            } else {
                Toast.makeText(this, R.string.storage_permission_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 提示信息
     */
    private void showTip() {
        if ((currentModel & ImageBuilder.MODEL_SAVE) == ImageBuilder.MODEL_SAVE) {
            if (preferences.getBoolean(String.valueOf(ImageBuilder.MODEL_SAVE), true)) {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.tip_img_save)
                        .setPositiveButton(R.string.i_know, (dialog, which) ->
                                preferences.edit().putBoolean(String.valueOf(ImageBuilder.MODEL_SAVE), false).apply()
                        )
                        .show();
            }
        }

    }

    /**
     * 重置position前后的PinchImageView状态
     *
     * @param position
     */
//    private void resetPinchImageView(int position) {
//        if (position + 1 < views.size()) {
//            getPinchImageView(views.get(position + 1)).resetScaleAndCenter();
//        }
//        if (position - 1 >= 0) {
//            getPinchImageView(views.get(position - 1)).resetScaleAndCenter();
//        }
//    }

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
            int index = 0;
            for (String url : urls) {
                View view = layoutInflater.inflate(R.layout.pinch_image, null);
                loadImage(view, url, index);
                final PhotoView img = view.findViewById(R.id.image);
                img.setOnClickListener(view1 -> finish());
                views.add(view);
                index++;
            }
        }
        return views;
    }

    /**
     * 通过url加载图片
     *
     * @param view
     * @param url
     */
    private void loadImage(View view, String url, int index) {
        final PhotoView img = (PhotoView) view.findViewById(R.id.image);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//        img.setMinScale(1.0F);//最小显示比例
//        img.setMaxScale(10.0F);//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）

        RequestBuilder<File> load = null;
        if (url != null && url.startsWith("data") && url.contains("base64")) {
            try {
                url = url.split(",")[1];
                load = Glide.with(this).downloadOnly().load(Base64.decode(url, Base64.DEFAULT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            load = Glide.with(this).downloadOnly().load(url);
        }

        if (load != null) {
            String finalUrl = url;
            load.listener(new RequestListener<File>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                    runOnUiThread(() -> {
                        Toast.makeText(ImageActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    });
                    return false;
                }

                @Override
                public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        img.setVisibility(View.VISIBLE);
                        try {
                            Glide.with(ImageActivity.this)
                                    .load(resource)
                                    .into(img);
                        } catch (Exception e) {
                            Log.e(TAG, "图片加载失败", e);
                        }
                        fileMap.put(index, resource);
                        img.setOnLongClickListener(v -> {
                            String[] strs = getItems();
                            if (strs == null)
                                return false;

                            new AlertDialog.Builder(ImageActivity.this)
                                    .setItems(strs, (DialogInterface dialog, int which) -> {
                                        if (getString(R.string.img_save).equals(strs[which])) {
                                            ImageActivity.this.currentFile = resource;
                                            saveImg();
                                        }
                                    }).show();
                            return false;
                        });


                    });
                    return false;
                }
            }).submit();
        }

//        Glide.with(this)
//                .load(url)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        progressBar.setVisibility(View.GONE);
//                        img.setVisibility(View.VISIBLE);
//                        //img.addOuterTouchOverListener(viewPager);
//                        img.setOnClickListener(v -> finish());
//                        img.setOnLongClickListener(v -> {
//                            String[] strs = getItems();
//                            if (strs == null)
//                                return false;
//
//                            new AlertDialog.Builder(ImageActivity.this)
//                                    .setItems(strs, (DialogInterface dialog, int which) -> {
//                                        if (getString(R.string.img_save).equals(strs[which])) {
//                                            currentSaveImg = img;
//                                            saveImg(savePath);
//                                        } else if (getString(R.string.img_delete).equals(strs[which])) {
//                                            deleteUrls += url + ",";
//                                            views.remove(view);
//                                            viewPager.getAdapter().notifyDataSetChanged();
//                                        }
//                                    }).show();
//                            return false;
//                        });
//                        return false;
//
//                    }
//                })
//                .into(img);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private String[] getItems() {
        String str = "";
        if ((currentModel & ImageBuilder.MODEL_SAVE) == ImageBuilder.MODEL_SAVE)
            str += getString(R.string.img_save) + ",";

        if ("".equals(str))
            return null;

        return str.split(",");
    }

    private void saveImg() {
        if (currentFile == null)
            return;

        if (!Permission.storage(this))
            return;

        try {
            boolean result = FileUtil.saveImageToGallery(this, currentFile);
            Toast.makeText(this, result ? getText(R.string.img_save_success) : getText(R.string.img_save_fail), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(this, getText(R.string.img_save_fail), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 启动当前界面
     * <p>
     * 如果有删除 则会在onActivityResult 中返回已删除的url数组
     *
     * @param activity
     * @param urls     图片地址数组
     * @param url      当前要展示的图片地址
     * @param isLandscape  是否横屏
     */
    public static void start(Activity activity, String[] urls, String url, int model,
                             boolean isLandscape) {
        IntentMap.clear();
        Intent intent = new Intent();
        intent.putExtra(URLS, IntentMap.set(urls));
        intent.putExtra(CURRENT_URL, IntentMap.set(url));
        intent.putExtra(CURRENT_MODEL, model);
        intent.putExtra(IS_LANDSCAPE, isLandscape);
        intent.setClass(activity, ImageActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
