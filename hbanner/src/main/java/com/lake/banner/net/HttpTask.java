package com.lake.banner.net;

import java.util.concurrent.Callable;

public class HttpTask {
    private Callable<String> callable;
    private String tag;

    public HttpTask(String tag, Callable<String> callable) {
        this.callable = callable;
        this.tag = tag;
    }

    public Callable<String> getCallable() {
        return callable;
    }

    public void setCallable(Callable<String> callable) {
        this.callable = callable;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
