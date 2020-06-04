package com.lake.banner.loader;

import android.text.TextUtils;

import androidx.annotation.IntDef;

import com.lake.banner.BannerConfig;
import com.lake.banner.R;
import com.lake.banner.VideoGravityType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 轮播自定义子项目内容
 */
public class ViewItemBean {
    protected int type;//播放类型
    protected String title;//标题
    protected Object url;//子视图地址
    protected int Time;//子视图显示时间

    @IntDef({BannerConfig.IMAGE, BannerConfig.VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    private @interface itemType {
    }

    public ViewItemBean(@itemType int type, Object url, int time) {
        this(type,url);
        Time = time;
    }

    public ViewItemBean(@itemType int type, String title, Object url, int time) {
        this(type,url,time);
        this.title = title;
    }

    //拥有默认时间的视图
    public ViewItemBean(@itemType int type, Object url) {
        this.type = type;
        this.url = url;
        Time = BannerConfig.TIME;
    }

    //用于视频图片交替时候的过度对象
    public ViewItemBean() {
        this.type = BannerConfig.IMAGE;
        this.url = R.mipmap.defalteimage;
        Time = 5;
    }

    public String getTitle() {
        if(TextUtils.isEmpty(title))
            return "";
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getUrl() {
        return url;
    }

    public void setUrl(Object url) {
        this.url = url;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    @Override
    public String toString() {
        return "ViewItemBean{" +
                "type=" + type +
                ", url=" + url +
                ", Time=" + Time +
                '}';
    }
}
