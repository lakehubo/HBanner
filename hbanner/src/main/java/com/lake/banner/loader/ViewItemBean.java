package com.lake.banner.loader;

import android.text.TextUtils;

import com.lake.banner.BannerConfig;
import com.lake.banner.R;

/**
 * 轮播自定义子项目内容
 */
public class ViewItemBean {
    protected int type;//播放类型
    protected String title;//标题
    protected Object url;//子视图地址
    protected int Time;//子视图显示时间

    public ViewItemBean(int type, Object url, int time) {
        this.type = type;
        this.url = url;
        Time = time;
    }

    public ViewItemBean(int type, String title, Object url, int time) {
        this.type = type;
        this.title = title;
        this.url = url;
        Time = time;
    }

    //拥有默认时间的视图
    public ViewItemBean(int type, Object url) {
        this.url = url;
        Time = BannerConfig.TIME;
    }

    //用于视频图片交替时候的过度对象
    public ViewItemBean() {
        this.type = BannerConfig.IMAGE;
        this.url = R.mipmap.defaultvideobg;
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
