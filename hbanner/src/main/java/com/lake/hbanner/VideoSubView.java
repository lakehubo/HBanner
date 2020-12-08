package com.lake.hbanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.lake.banner.net.HttpCallback;
import com.lake.banner.net.HttpThreadPool;
import com.lake.banner.uitls.LogUtils;

import java.io.File;
import java.util.Objects;

import static android.media.MediaMetadataRetriever.METADATA_KEY_DURATION;

/**
 * @author lake
 * create by 11/20/20 11:00 AM
 */
public class VideoSubView extends ShowView {
    private static final int VIDEO_CACHE_COMPLETE = 902;
    private static final int VIDEO_THUMB_COMPLETE = 903;
    private VideoView videoView;
    private BitmapDrawable defaultDrawable;
    FrameLayout layout;
    private long duration = 5000;
    private volatile File videoFile = null;
    private final UpdateHandler updateHandler;
    private final Context context;
    private final VideoViewType viewType;
    //当该view是处于被同步的banner中时候需要设置为true
    private final boolean isSubChange;

    private VideoSubView(Context context, File file, VideoViewType viewType, boolean isSubChange) {
        super(context);
        this.context = context;
        this.viewType = viewType;
        this.isSubChange = isSubChange;
        updateHandler = new UpdateHandler(context.getMainLooper());
        initVideoView(context);
        videoFile = file;
        initVideoByFile(file);
    }

    private VideoSubView(Context context, String httpPath, VideoViewType viewType, boolean isSubChange) {
        super(context);
        this.context = context;
        this.viewType = viewType;
        this.isSubChange = isSubChange;
        updateHandler = new UpdateHandler(context.getMainLooper());
        initVideoView(context);
        File cacheFile = getCacheFile(httpPath);
        if (cacheFile.exists()) {
            initVideoByFile(cacheFile);
        } else {
            cacheVideo(httpPath);
        }
    }

    private void initVideoByFile(File file) {
        videoView.setVideoPath(file.getAbsolutePath());

        updateDuration(file);
        initCacheBmp(file, true);
    }

    private void initVideoView(Context context) {
        videoView = new VideoView(context) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (viewType == VideoViewType.FULL)
                    setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
                else
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };

        //建立父布局包裹videoView
        layout = new FrameLayout(context);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        layout.addView(videoView, params);

        videoView.setOnInfoListener((MediaPlayer mp, int what, int extra) -> {
            //播放第一帧
            LogUtils.e("video", "VideoSubView: " + what);
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                videoView.setBackgroundColor(Color.TRANSPARENT);
            return true;
        });
    }

    private void initCacheBmp(final File file, boolean sync) {
        if (!sync) {
            HttpThreadPool.getInstance().post(() -> {
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                        file.getAbsolutePath(),
                        MediaStore.Images.Thumbnails.MINI_KIND);
                updateHandler.sendMessage(updateHandler.obtainMessage(VIDEO_THUMB_COMPLETE, thumbnail));
            });
        } else {
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
                    file.getAbsolutePath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);
            updateHandler.sendMessage(updateHandler.obtainMessage(VIDEO_THUMB_COMPLETE, thumbnail));
        }
    }

    private void updateDuration(File file) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getAbsolutePath());
        duration = Long.parseLong(mmr.extractMetadata(METADATA_KEY_DURATION));
    }

    @Override
    public void onShowStart(final HBanner hBanner, int position) {
        LogUtils.e("video", "onShowStart: " + position);
        hBanner.pause(0);
        videoView.resume();
        videoView.start();

        //当手动滑动时，视频还未结束，会导致轮播停止,是因为视频直接被stop没有执行shownext
        videoView.setOnCompletionListener(m -> {
            videoView.stopPlayback();
            if (!isSubChange) {
                hBanner.showNext(true);//可能当前不是自动模式，这里会强制开启自动模式
            }
        });
    }

    @Override
    public void onShowFinish() {
        LogUtils.e("video", "onShowFinish: ");
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        if (defaultDrawable != null)
            videoView.setBackground(defaultDrawable);
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
        return duration;
    }

    @Override
    public View getView() {
        return layout;
    }

    public VideoView getVideoView() {
        return videoView;
    }

    private class UpdateHandler extends Handler {

        public UpdateHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case VIDEO_CACHE_COMPLETE:
                    updateDuration(videoFile);
                    initCacheBmp(videoFile, false);
                    Objects.requireNonNull(videoView);
                    videoView.setVideoPath(videoFile.getAbsolutePath());
                    break;
                case VIDEO_THUMB_COMPLETE:
                    Objects.requireNonNull(mImageCache);
                    Bitmap bitmap = (Bitmap) msg.obj;
                    mImageCache.setImageBitmap(bitmap);
                    defaultDrawable = new BitmapDrawable(context.getResources(), bitmap);
                    videoView.setBackground(defaultDrawable);
                    break;
                default:
                    break;
            }
        }
    }

    private void cacheVideo(String url) {
        cacheFile(url, new HttpCallback() {
            @Override
            public void success(File result) {
                LogUtils.i("VideoSubView", "video file download complete:" + result.getAbsolutePath());
                videoFile = result;
                updateHandler.sendEmptyMessage(VIDEO_CACHE_COMPLETE);
            }

            @Override
            public void failed(String Msg) {
                LogUtils.e("VideoSubView", Msg);
            }

            @Override
            public void progress(float progress, float count) {
                LogUtils.i("HBanner", String.valueOf(progress) + "/" + String.valueOf(count));
            }
        });
    }

    public final static class Builder {
        private VideoViewType type;
        private boolean isSub;
        private String urlStr;
        private File file;
        private Context context;


        public Builder(Context context) {
            this.context = context;
            type = VideoViewType.CENTER;
            isSub = false;
            urlStr = null;
            file = null;
        }

        public Builder gravity(VideoViewType type) {
            this.type = type;
            return this;
        }

        public Builder isSub(boolean sub) {
            this.isSub = sub;
            return this;
        }

        public Builder file(File file) {
            if (!file.exists())
                throw new IllegalArgumentException("the file is not exists!");
            this.file = file;
            return this;
        }

        public Builder url(String url) {
            this.urlStr = url;
            return this;
        }

        public VideoSubView build() {
            if (urlStr == null && file == null)
                throw new IllegalArgumentException("the VideoSubView must be have the file or url param!");
            if (file != null) {
                return new VideoSubView(context, file, type, isSub);
            } else {
                return new VideoSubView(context, urlStr, type, isSub);
            }
        }
    }
}
