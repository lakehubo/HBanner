package com.lake.banner.loader;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.widget.VideoView;

/**
 * 视频代理实现
 */
public class VideoLoader implements VideoViewLoaderInterface {

    @Override
    public VideoView createView(Context context) {
        return new VideoView(context);
    }

    @Override
    public void onPrepared(Context context, Object path, VideoView videoView) {
        try {
            videoView.setBackgroundColor(Color.TRANSPARENT);
            if (path instanceof String) {
                videoView.setVideoPath((String) path);
            } else if (path instanceof Uri) {
                videoView.setVideoURI((Uri) path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayView(Context context, VideoView videoView) {
        videoView.seekTo(0);
        videoView.start();
    }

    @Override
    public void onStop(VideoView view) {
        view.pause();
    }

    @Override
    public void onDestroy(VideoView videoView) {
        videoView.stopPlayback();
        System.gc();
    }
}
