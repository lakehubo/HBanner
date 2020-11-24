package com.lake.hbanner;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;

/**
 *
 * @author lake
 * create by 11/20/20 11:00 AM
 */
public class VideoSubView extends ShowView {
    private final VideoView videoView;

    public VideoSubView(Context context, Uri path) {
        super(context);
        videoView = new VideoView(context);
        videoView.setVideoURI(path);
    }

    @Override
    public void onShowStart(HBanner hBanner, int position) {
        hBanner.pause(0);
        if (videoView.canSeekBackward()) {
            videoView.seekTo(0);
        }
        videoView.start();
        videoView.setOnCompletionListener(m -> {
            hBanner.showNext(true);
        });
    }

    @Override
    public void onShowFinish() {
        videoView.pause();
    }

    /**
     * 当在onShowStart中使用pause(0)后，该duration会无效化，然后可以灵活的
     * 使用videoView的OnCompletionListener回调中使用showNext切换到下一张显示，
     * 这样会播放完整的视频。
     *
     * @return
     */
    @Override
    public long duration() {
        return 5000;
    }

    @Override
    public View getView() {
        return videoView;
    }
}
