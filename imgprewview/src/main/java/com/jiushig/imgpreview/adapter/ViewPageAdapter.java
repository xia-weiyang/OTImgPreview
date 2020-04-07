package com.jiushig.imgpreview.adapter;

import android.app.Activity;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;


import com.jiushig.imgpreview.widget.CustomViewPage;

import java.util.ArrayList;

/**
 * 启动引导页的viewpager适配器
 * Created by zk on 2016/3/1.
 */
public class ViewPageAdapter extends PagerAdapter {

    private ArrayList<View> views;
    private Activity activity;

    public ViewPageAdapter(Activity activity, ArrayList<View> views) {
        this.activity = activity;
        this.views = views;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        views.get(position).setTag(position);
        ((CustomViewPage) container).addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((CustomViewPage) container).removeView((View) object);
    }

    @Override
    public int getCount() {
        if (views == null || views.size() == 0) {
            activity.finish();
            return 0;
        }
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
