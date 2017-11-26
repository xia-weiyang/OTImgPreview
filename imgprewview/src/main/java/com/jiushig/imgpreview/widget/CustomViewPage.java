package com.jiushig.imgpreview.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


/**
 * Created by Guowang on 2017/2/7.
 * 自定义ViewPage
 */

public class CustomViewPage extends ViewPager implements PinchImageView.OuterTouchOverListener {

    private int imageStatus = PinchImageView.IMG_STATUS_INIT;

    private final String TAG = CustomViewPage.class.getSimpleName();

    private float downX, downY;

    public CustomViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initImageStatus() {
        this.imageStatus = PinchImageView.IMG_STATUS_INIT;
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
    }

    /**
     * 根据需要拦截Touch事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getX();
            downY = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float moveX = event.getX() - downX;
            float moveY = event.getY() - downY;
            if (Math.abs(moveX) > Math.abs(moveY)) {
                switch (imageStatus) {
                    case PinchImageView.IMG_STATUS_ENLARGE_EDGE_LEFT:
//                         除7是为了判断 滑动的是不是一条横向的直线
                        if (moveX > 0 && Math.abs(moveX) / 7 > Math.abs(moveY) && Math.abs(moveX) < (getWidth() / 4))
                            return true;
                        else
                            return false;
                    case PinchImageView.IMG_STATUS_ENLARGE_EDGE_RIGHT:
                        if (moveX < 0 && Math.abs(moveX) / 7 > Math.abs(moveY) && Math.abs(moveX) < (getWidth() / 4))
                            return true;
                        else
                            return false;
                    case PinchImageView.IMG_STATUS_ENLARGE:
                        return false;
                    default:
                        break;
                }
            }
        }
        return super.onInterceptTouchEvent(event);
    }


    @Override
    public void OuterTouchOver(PinchImageView pinchImageView) {
        Log.d(TAG, "img status:" + pinchImageView.getCurrentImgStatus());
        this.imageStatus = pinchImageView.getCurrentImgStatus();
    }


}
