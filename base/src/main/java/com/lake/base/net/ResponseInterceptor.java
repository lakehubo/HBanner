package com.lake.base.net;

import static com.lake.base.net.CookiesInterceptor.COOKIE_KEY;
import static com.lake.base.net.CookiesInterceptor.COOKIE_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lake.base.utils.SharedPreferencesUtils;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * cookie拦截
 * @author lake
 */
public class ResponseInterceptor implements Interceptor {
    Context context;

    public ResponseInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (!response.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();
            for (String header: response.headers("Set-Cookie")) {
                Log.e("intercept", "----------->" + header);
                cookies.add(header);
            }
            SharedPreferences sharedPreferences = context.getSharedPreferences(COOKIE_NAME,Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sharedPreferences.edit();
            ed.putStringSet(COOKIE_KEY, cookies);
            ed.commit();
        }
        return response;
    }
}
