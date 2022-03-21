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
import android.widget.ImageView;
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
    private final long playOffset;
    //当该view是处于被同步的banner中时候需要设置为true
    private final boolean isSubChange;
    private String tag = null;

    private VideoSubView(File file, Builder builder) {
        super(builder.context);
        this.context = builder.context;
        this.viewType = builder.type;
        this.isSubChange = builder.isSub;
        this.playOffset = builder.playOffset;
        videoFile = file;
        updateHandler = new UpdateHandler(context.getMainLooper());
        initVideoView(context);
        initVideoByFile(file.getAbsolutePath());
    }

    private VideoSubView(String httpPath, Builder builder) {
        super(builder.context);
        this.context = builder.context;
        this.viewType = builder.type;
        this.isSubChange = builder.isSub;
        this.playOffset = builder.playOffset;
        updateHandler = new UpdateHandler(context.getMainLooper());
        initVideoView(context);
        File cacheFile = getCacheFile(httpPath);
        if (cacheFile.exists()) {
            initVideoByFile(cacheFile.getAbsolutePath());
        } else {
            cacheVideo(httpPath);
        }
    }

    private void initVideoByFile(String path) {
        videoView.setVideoPath(path);

        updateDuration(path);
        initCacheBmp(path, true);
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
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                videoView.postDelayed(() -> {
                    videoView.setBackgroundColor(Color.TRANSPARENT);
                }, playOffset);
            }
            return true;
        });
    }

    private void initCacheBmp(final String path, boolean sync) {
        if (!sync) {
            HttpThreadPool.getInstance().post(() -> {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(path);
                Bitmap thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                updateHandler.sendMessage(updateHandler.obtainMessage(VIDEO_THUMB_COMPLETE, thumbnail));
            });
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            Bitmap thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            updateHandler.sendMessage(updateHandler.obtainMessage(VIDEO_THUMB_COMPLETE, thumbnail));
        }
    }


    private void updateDuration(String path) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        duration = Long.parseLong(mmr.extractMetadata(METADATA_KEY_DURATION));
    }

    @Override
    public void onShowStart(final HBanner hBanner, int position) {
        //记录当前播放模式
        super.onShowStart(hBanner, position);
        LogUtils.e("lake", "onShowStart: " + position + ",auto=" + auto);
        hBanner.pause(0);
        videoView.resume();
        videoView.start();

        videoView.setOnCompletionListener(m -> {
            videoView.stopPlayback();
            if (!isSubChange && auto) {//非同步且是自动播放模式会在视频结束时自动播放下一项
                hBanner.showNext(true);
            }
        });
    }

    @Override
    public boolean onShowFinish() {
        LogUtils.e("lake", "onShowFinish: ");
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        if (defaultDrawable != null)
            videoView.setBackground(defaultDrawable);
        //保障自动播放模式不受滑动影响
        return super.onShowFinish();
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
                    updateDuration(videoFile.getAbsolutePath());
                    initCacheBmp(videoFile.getAbsolutePath(), false);
                    Objects.requireNonNull(videoView);
                    videoView.setVideoPath(videoFile.getAbsolutePath());
                    break;
                case VIDEO_THUMB_COMPLETE:
                    Objects.requireNonNull(mImageCache);
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (viewType == VideoViewType.FULL)
                        mImageCache.setScaleType(ImageView.ScaleType.FIT_XY);
                    else
                        mImageCache.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
        private long playOffset;
        private String tag;

        public Builder(Context context) {
            this.context = context;
            type = VideoViewType.CENTER;
            isSub = false;
            urlStr = null;
            file = null;
            playOffset = 0;
        }

        public Builder gravity(VideoViewType type) {
            this.type = type;
            return this;
        }

        public Builder isSub(boolean sub) {
            this.isSub = sub;
            return this;
        }

        public Builder playOffset(long offset) {
            this.playOffset = offset;
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

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public VideoSubView build() {
            if (urlStr == null && file == null)
                throw new IllegalArgumentException("the VideoSubView must be have the file or url param!");
            if (file != null) {
                return new VideoSubView(file, this);
            } else {
                return new VideoSubView(urlStr, this);
            }
        }
    }
}
