package com.lake.banner.loader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 *图片代理实现
 */
public class ImageLoader implements ViewLoaderInterface<ImageView> {

    @Override
    public ImageView createView(Context context) {
        return new ImageView(context);
    }

    @Override
    public void onPrepared(Context context, Object path, ImageView imageView) {
        try {
            if (path instanceof Integer) {
                imageView.setImageResource((int) path);
            } else {
                imageView.setImageBitmap(BitmapFactory.decodeFile((String) path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(ImageView imageView) {
        System.gc();
    }
}
