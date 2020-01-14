package com.lake.banner.net;

public interface HttpManager {
     <T> void Get(HttpParam param, HttpCallback.RequestHttpCallback<T> callback);

     <T> void Post(HttpParam param, HttpCallback.RequestHttpCallback<T> callback);

     <T> void Put(HttpParam param, HttpCallback.RequestHttpCallback<T> callback);
}
