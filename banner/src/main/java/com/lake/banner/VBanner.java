package com.lake.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 纯视频轮播控件
 */
public class VBanner extends FrameLayout implements ViewPager.OnPageChangeListener {
    public static final String TAG = VBanner.class.getSimpleName();
    private Context context;

    private WeakHandler handler = new WeakHandler();
    private int mLayoutResId = R.layout.vbanner;

    public VBanner(Context context) {
        this(context, null);
    }

    public VBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(mLayoutResId, this, true);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
