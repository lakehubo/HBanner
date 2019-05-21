package com.lake.banner.loader;

import android.content.Context;
import android.widget.ImageView;


public abstract class ImageLoader implements ViewLoaderInterface<ImageView> {

    @Override
    public ImageView createView(Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }

}
