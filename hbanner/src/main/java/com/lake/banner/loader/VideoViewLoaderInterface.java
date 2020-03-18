package com.lake.banner.loader;

import android.content.Context;
import android.widget.VideoView;

/**
 * 视频加载代理接口
 */
public interface VideoViewLoaderInterface extends ViewLoaderInterface<VideoView>{

    void displayView(Context context, VideoView view);

    void onStop(VideoView view);

    void onResume(VideoView view);
}
