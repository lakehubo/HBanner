package com.lake.banner.loader;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.VideoView;

import com.lake.banner.uitls.LogUtils;
import com.lake.banner.uitls.MD5Util;
import com.lake.banner.view.CustomVideoView;
import java.io.File;

/**
 * 视频代理实现
 */
public class VideoLoader implements VideoViewLoaderInterface {
    public VideoLoader() {
    }

    @Override
    public VideoView createView(Context context,int gravity) {
        //全屏拉伸 CustomVideoView(context)；
        CustomVideoView customVideoView = new CustomVideoView(context);
        customVideoView.setGravityType(gravity);
        return customVideoView;
    }

    @Override
    public void onPrepared(Context context, Object path, VideoView videoView,String cachePath) {
        try {
            videoView.setOnPreparedListener(mp->{
                LogUtils.e("auto", "videoView onPrepared");
            });
            videoView.setOnErrorListener((MediaPlayer mp, int what, int extra) -> {
                //视频读取失败！
                LogUtils.e("auto", "videoView error="+what);
                return true;
            });
            if (path instanceof String) {
                videoView.setVideoPath((String) path);
            } else if (path instanceof Uri) {
                String pStr = path.toString();
                String type = pStr.substring(pStr.lastIndexOf("."));
                File file = new File(cachePath + File.separator + MD5Util.md5(path.toString()) + type);
                if (file.exists()) {
                    LogUtils.e("lake", "onPrepared: isCache");
                    videoView.setVideoURI(Uri.parse(file.getPath()));
                } else {
                    LogUtils.e("lake", "onPrepared: noCache");
                    videoView.setVideoURI((Uri) path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayView(Context context, VideoView videoView) {
        LogUtils.e("auto", "displayView: ");
        videoView.seekTo(0);
        videoView.start();
    }

    @Override
    public void onResume(VideoView view) {
        LogUtils.e("test", "onResume: ");
        view.start();
    }

    @Override
    public void onStop(VideoView view) {
        LogUtils.e("auto", "onStop: ");
        view.pause();
    }

    @Override
    public void onDestroy(VideoView videoView) {
        LogUtils.e("test", "onDestroy: ");
        videoView.stopPlayback();
        System.gc();
    }
}
