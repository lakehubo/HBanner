package com.lake.base.net;

import android.content.Context;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.HashSet;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * cookie设置
 *
 * @author lake
 */
public class CookiesInterceptor implements Interceptor {

    public static final String COOKIE_NAME = "cookiedata";
    public static final String COOKIE_KEY = "cookie";

    Context context;

    public CookiesInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();
        HashSet<String> sharedPreferences = (HashSet<String>) context
                .getSharedPreferences(COOKIE_NAME, Context.MODE_PRIVATE)
                .getStringSet(COOKIE_KEY, null);
        if (sharedPreferences != null) {
            for (String cookie : sharedPreferences) {
                requestBuilder.addHeader("Cookie", cookie);
            }
        }
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
