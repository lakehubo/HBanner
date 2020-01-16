package com.lake.banner.net;


public interface HttpCallback {
    /**
     * 目前只支持 实体对象 string file类型的数据回调
     * @param <T>
     */
    interface RequestHttpCallback<T> extends HttpCallback {
        void success(T result);

        void failed(String Msg);
    }

    interface ProgressRequestHttpCallback<T> extends RequestHttpCallback<T> {
        void progress(float progress, float count);
    }

    interface TimeRequestHttpCallback<T> extends RequestHttpCallback<T>{
        void onTimeInfo(long time);
    }
}
