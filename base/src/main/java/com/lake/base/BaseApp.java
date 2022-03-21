package com.lake.base;

import android.app.Application;

import com.lake.base.helper.CrashHandler;
import com.lake.base.net.RetrofitUtil;

import okhttp3.Interceptor;

/**
 * @author lake
 * create by 2021/8/6 3:26 下午
 * application 基类
 */
public abstract class BaseApp extends Application {
    /**
     * 初始化retrofit的base url
     *
     * @return
     */
    protected abstract String initNetAddress();

    /**
     * cookie过期 拦截器
     * @return
     */
    protected abstract Interceptor tokenInterceptor();

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler.getInstance().init(this);
        RetrofitUtil.init(this,initNetAddress(),tokenInterceptor());
    }
}
