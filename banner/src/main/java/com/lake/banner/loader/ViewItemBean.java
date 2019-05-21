package com.lake.banner.loader;

import com.lake.banner.BannerConfig;
import com.lake.banner.R;


/**
 * 轮播自定义子项目内容
 */
public class ViewItemBean {
    private Object url;//子视图地址
    private int Time;//子视图显示时间

    public ViewItemBean(Object url, int time) {
        this.url = url;
        Time = time;
    }

    public ViewItemBean(Object url) {
        this.url = url;
        Time = BannerConfig.TIME;
    }

    public ViewItemBean() {
        this.url = R.mipmap.defaultvideobg;
        Time = 5;
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
                "url='" + url + '\'' +
                ", Time=" + Time +
                '}';
    }
}
