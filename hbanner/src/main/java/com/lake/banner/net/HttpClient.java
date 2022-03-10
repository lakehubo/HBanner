package com.lake.banner.net;

/**
 * 网络请求类封装
 * @author lake
 */
public class HttpClient extends HttpEvent implements HttpManager {
    private volatile static HttpClient httpClient = null;
    private static final Object lock = new Object();
    private HttpClient() {}

    //单例模式
    public static HttpManager getInstance() {
        if (httpClient == null) {
            synchronized (lock) {
                if (httpClient == null) {
                    httpClient = new HttpClient();
                }
            }
        }
        return httpClient;
    }

    @Override
    public void Get(HttpParam param, HttpCallback callback) {
        httpRequest(param, callback);
    }
}
