package com.lake.banner.loader;

import android.content.Context;
import android.graphics.Color;
import android.widget.VideoView;

public class LocalVideoLoader extends VideoLoafer {
    @Override
    public void displayView(Context context, Object path, VideoView view) {
        try {
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setVideoPath((String) path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
