package com.lake.banner.loader;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * 通用view加载代理接口
 *
 * @param <T>
 */
public interface ViewLoaderInterface<T extends View> extends Serializable {

    T createView(Context context);

    void onPrepared(Context context, Object path, T view);

    void onDestroy(T view);
}
