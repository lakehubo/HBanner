package com.lake.banner.loader;

import android.content.Context;
import android.widget.VideoView;

public abstract class VideoLoafer implements ViewLoaderInterface<VideoView> {
    @Override
    public VideoView createView(Context context) {
        VideoView videoView = new VideoView(context);
        return videoView;
    }
}
