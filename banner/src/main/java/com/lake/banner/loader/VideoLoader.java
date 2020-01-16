package com.lake.banner.loader;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.VideoView;

import com.lake.banner.uitls.Constants;
import com.lake.banner.uitls.MD5Util;

import java.io.File;

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
            videoView.setOnErrorListener((MediaPlayer mp, int what, int extra) -> {
                //视频读取失败！
                return true;
            });
            if (path instanceof String) {
                videoView.setVideoPath((String) path);
            } else if (path instanceof Uri) {
                String pStr = path.toString();
                String type = pStr.substring(pStr.lastIndexOf("."));
                File file = new File(Constants.DEFAULT_DOWNLOAD_DIR + File.separator + MD5Util.md5(path.toString()) + type);
                if (file.exists()) {
                    Log.e("lake", "onPrepared: isCache");
                    videoView.setVideoPath(file.getAbsolutePath());
                } else {
                    Log.e("lake", "onPrepared: noCache");
                    videoView.setVideoURI((Uri) path);
                }
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
