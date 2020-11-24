package com.lake.hbanner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * @author lake
 * create by 11/17/20 4:55 PM
 */
public class ImageSubView extends ShowView {
    ImageView imageView;
    int color;

    public ImageSubView(Context context, int color) {
        super(context);
        this.color = color;
        imageView = new ImageView(context);
        imageView.setBackgroundColor(color);
    }

    @Override
    public long duration() {
        return 5000;
    }

    @Override
    public View getView() {
        return imageView;
    }

    @Override
    public View getPreView() {
        mImageCache.setBackgroundColor(color);
        return mImageCache;
    }
}
