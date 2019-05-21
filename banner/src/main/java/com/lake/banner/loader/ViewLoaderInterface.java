package com.lake.banner.loader;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

/**
 * 通用view加载器
 * @param <T>
 */
public interface ViewLoaderInterface<T extends View> extends Serializable {

    void displayView(Context context, Object path, T view);

    T createView(Context context);
}
