package com.lake.base.net;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.lake.base.Constants;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * create by 2021/8/6 5:27 下午
 * retrofit工具类
 *
 * @author lake
 */
public class RetrofitUtil {
    /**
     * 增加可见性
     */
    private volatile static RetrofitUtil instance;
    private volatile static Retrofit retrofit = null;
    private volatile static String baseUrl = null;
    private volatile static Context mContext = null;
    /**
     * token拦截器
     */
    private volatile static Interceptor tokenInterceptor = null;
    /**
     * api接口缓存
     */
    private volatile static HashMap<String, Object> apiCache = new HashMap<>(16);

    private RetrofitUtil() {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new RuntimeException("RetrofitUtil must be init the url first!");
        }
        initRetrofit(baseUrl);
    }

    /**
     * 初始化url地址
     *
     * @param url
     */
    public static void init(Context context, String url) {
        mContext = context;
        baseUrl = url;
        Log.d("RetrofitManager", "----------->" + baseUrl);
    }

    /**
     * 初始化url地址
     *
     * @param url
     */
    public static void init(Context context, String url, Interceptor interceptor) {
        mContext = context;
        baseUrl = url;
        tokenInterceptor = interceptor;
        Log.d("RetrofitManager", "----------->" + baseUrl);
    }

    public static RetrofitUtil getInstance() {
        if (instance == null) {
            synchronized (RetrofitUtil.class) {
                if (instance == null) {
                    instance = new RetrofitUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 获取接口类
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getApi(Class<T> clazz) {
        Object cacheApi = apiCache.get(clazz.getSimpleName());
        if (cacheApi != null && cacheApi.getClass().equals(clazz)) {
            return (T) cacheApi;
        }
        T api = retrofit.create(clazz);
        apiCache.put(clazz.getSimpleName(), api);
        return api;
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .client(initOkHttp())
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    private OkHttpClient initOkHttp() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(s -> Log.e("RetrofitManager", "----------->" + s));
        logging.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //token拦截
        if (tokenInterceptor != null) {
            builder.addInterceptor(tokenInterceptor);
        }
        return builder
                //设置读取超时时间
                .readTimeout(Constants.DEFAULT_TIME, TimeUnit.SECONDS)
                //设置请求超时时间
                .connectTimeout(Constants.DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                //设置写入超时时间
                .writeTimeout(Constants.DEFAULT_TIME, TimeUnit.SECONDS)
//                .addNetworkInterceptor(new ResponseInterceptor(mContext))
//                .addNetworkInterceptor(new CookiesInterceptor(mContext))
                .addInterceptor(logging)
                //设置出现错误进行重新连接。
                .retryOnConnectionFailure(true)
                .build();
    }
}
