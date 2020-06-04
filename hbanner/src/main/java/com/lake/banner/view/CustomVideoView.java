package com.lake.banner.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;
import com.lake.banner.VideoGravityType;
import com.lake.banner.uitls.LogUtils;

public class CustomVideoView extends VideoView {
    private static final String TAG = CustomVideoView.class.getSimpleName();
    private int parentWidth = 0;
    private int parentHeight = 0;
    private int gravity = VideoGravityType.CENTER;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(gravity== VideoGravityType.FULL_SCREEN){
            int width = getDefaultSize(getWidth(), widthMeasureSpec);
            int height = getDefaultSize(getHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);
        }else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        ViewGroup mViewGroup = (ViewGroup) getParent();
        if (null != mViewGroup) {
            parentWidth = mViewGroup.getWidth();
            parentHeight = mViewGroup.getHeight();
            LogUtils.e(TAG, "onMeasure: " + parentWidth + "," + parentHeight);
        }
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        int h = b - t;
        int w = r - l;
        LogUtils.e(TAG, "layout: h="+h+",w="+w);
        switch (gravity) {
            case VideoGravityType.CENTER:
                //center模式
                if (parentHeight > h) {
                    int offsetH = parentHeight - h;
                    t += offsetH / 2;
                    b += offsetH / 2;
                }
                if (parentWidth > w) {
                    int offsetW = parentWidth - w;
                    l += offsetW / 2;
                    r += offsetW / 2;
                }
                break;
            case VideoGravityType.CENTER_HORIZONTAL:
                //垂直居中
                if (parentHeight > h) {
                    int offsetH = parentHeight - h;
                    t += offsetH / 2;
                    b += offsetH / 2;
                }
                break;
        }
        LogUtils.e(TAG, "layout: " + l + "," + t + "," + r + "," + b);
        super.layout(l, t, r, b);
    }

    public void setGravityType(int bannerGravity) {
        if (gravity != bannerGravity) {
            gravity = bannerGravity;

            requestLayout();
            invalidate();
        }
    }
}
