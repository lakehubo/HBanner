package com.lake.banner.net;


import java.io.File;

public interface HttpCallback {
    void success(File result);

    void failed(String Msg);

    void progress(float progress, float count);
}
