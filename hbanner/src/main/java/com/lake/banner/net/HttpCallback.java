package com.lake.banner.net;


import java.io.File;

/**
 * 网络接口回调
 */
public interface HttpCallback {
    /**
     * 成功
     * @param result
     */
    void success(File result);

    /**
     * 失败
     * @param Msg
     */
    void failed(String Msg);

    /**
     * 进度
     * @param progress
     * @param count
     */
    void progress(float progress, float count);
}
