package com.lake.banner.loader;

import android.content.Context;
import android.widget.ImageView;

public class LocalImageLoader extends ImageLoader {
    @Override
    public void displayView(Context context, Object path, ImageView view) {
        try {
            view.setImageResource((int) path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
