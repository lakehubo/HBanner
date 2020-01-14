package com.lake.banner.net;

/**
 * 网络请求类封装
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
    public <T> void Post(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.POST);
        httpRequest(param, callback);
    }

    @Override
    public <T> void Get(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.GET);
        httpRequest(param, callback);
    }

    @Override
    public <T> void Put(HttpParam param, HttpCallback.RequestHttpCallback<T> callback) {
        param.setType(HttpParam.PUT);
        httpRequest(param, callback);
    }
}
