package com.lake.hbanner;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.lake.banner.R;
import com.lake.banner.net.HttpCallback;
import com.lake.banner.uitls.LogUtils;

import android.widget.ImageView.ScaleType;

import java.io.File;

/**
 * @author lake
 * create by 11/17/20 4:55 PM
 */
public class ImageSubView extends ShowView {
    ImageView imageView;
    final long duration;
    /**
     * 图片网络地址
     */
    private String httpPath = null;
    /**
     * 图片res资源id
     */
    private int mResource = 0;
    /**
     * 图片文件
     */
    private File imgFile = null;
    private static final int IMG_CACHE_COMPLETE = 901;
    private final UpdateHandler updateHandler;
    /**
     * 协同标记
     */
    private String tag = null;

    private ImageSubView(@DrawableRes int resId, Builder builder) {
        this(builder);
        this.mResource = resId;
        imageView.setImageResource(resId);
        mImageCache.setImageResource(resId);
    }

    private ImageSubView(String httpPath, Builder builder) {
        this(builder);
        this.httpPath = httpPath;
        File cacheFile = getCacheFile(httpPath);
        if (cacheFile.exists()) {
            initByFile(cacheFile);
        } else {
            cacheImg(httpPath);
            imageView.setImageResource(R.mipmap.defalteimage);
            mImageCache.setImageResource(R.mipmap.defalteimage);
        }
    }

    private ImageSubView(File imgFile, Builder builder) {
        this(builder);
        this.imgFile = imgFile;
        initByFile(imgFile);
    }

    private ImageSubView(Builder builder) {
        super(builder.context);
        this.duration = builder.duration;
        updateHandler = new UpdateHandler(builder.context.getMainLooper());
        imageView = new ImageView(builder.context);
        imageView.setScaleType(builder.scaleType);
        mImageCache.setScaleType(builder.scaleType);
    }

    private void initByFile(File file) {
        imageView.setImageURI(Uri.fromFile(file));
        mImageCache.setImageURI(Uri.fromFile(file));
    }

    @Override
    public long duration() {
        return duration;
    }

    @Override
    public View getView() {
        return imageView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private void cacheImg(final String url) {
        cacheFile(url, new HttpCallback() {
            @Override
            public void success(File result) {
                LogUtils.i("HBanner", "download complete:" + result.getAbsolutePath());
                imgFile = result;
                updateHandler.sendEmptyMessage(IMG_CACHE_COMPLETE);
            }

            @Override
            public void failed(String Msg) {
                LogUtils.e("HBanner", Msg);
            }

            @Override
            public void progress(float progress, float count) {
                LogUtils.i("HBanner", String.valueOf(progress) + "/" + String.valueOf(count));
            }
        });
    }

    private class UpdateHandler extends Handler {

        public UpdateHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case IMG_CACHE_COMPLETE:
                    imageView.setImageURI(Uri.fromFile(imgFile));
                    mImageCache.setImageURI(Uri.fromFile(imgFile));
                    break;
                default:
                    break;
            }
        }
    }

    public final static class Builder {
        private Context context;
        private String urlStr;
        private ScaleType scaleType;
        private File file;
        private int resId;
        private long duration;
        private String tag;

        public Builder(Context context) {
            this.context = context;
            urlStr = null;
            scaleType = ScaleType.FIT_CENTER;
            file = null;
            duration = 5000;
        }

        public Builder url(String url) {
            this.urlStr = url;
            return this;
        }

        public Builder gravity(ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Builder file(File file) {
            if (!file.exists())
                throw new IllegalArgumentException("the file is not exists!");
            this.file = file;
            return this;
        }

        public Builder resId(@DrawableRes int id) {
            this.resId = id;
            return this;
        }

        public Builder duration(long show) {
            this.duration = show;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public ImageSubView build() {
            if (urlStr == null && file == null && resId == 0)
                throw new IllegalArgumentException("image must be have one drawable source!");
            if (file != null) {
                return new ImageSubView(file, this);
            } else if (urlStr != null) {
                return new ImageSubView(urlStr, this);
            } else {
                return new ImageSubView(resId, this);
            }
        }
    }
}
