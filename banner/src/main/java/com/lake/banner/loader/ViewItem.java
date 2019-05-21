package com.lake.banner.loader;

import android.view.View;

public class ViewItem {
    View view;
    int Time;

    public ViewItem(View view, int time) {
        this.view = view;
        Time = time;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "view=" + view +
                ", Time=" + Time +
                '}';
    }
}
