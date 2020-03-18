package com.lake.banner.loader;

import android.view.View;

public class ViewItem extends ViewItemBean {
    View view;

    public ViewItem(View view) {
        this.view = view;
    }

    public ViewItem(View view, ViewItemBean bean) {
        this.view = view;
        setViewItemBean(bean);
    }

    public void setViewItemBean(ViewItemBean bean) {
        this.type = bean.getType();
        this.title = bean.getTitle();
        this.url = bean.getUrl();
        this.Time = bean.getTime();
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "view=" + view +
                ", type=" + type +
                ", url=" + url +
                ", Time=" + Time +
                '}';
    }
}
