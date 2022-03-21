package com.lake.base.utils;

import android.content.Context;

import com.lake.base.R;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import retrofit2.HttpException;

/**
 * 网络异常信息处理
 * @author lake
 * create by 2021/8/16 11:50 上午
 */
public class RxExceptionUtils {
    public static String exceptionHandler(Context context, Throwable e) {
        String errorMsg = context.getString(R.string.net_error);
        if (e instanceof UnknownHostException) {
            errorMsg = context.getString(R.string.connect_error);
        } else if (e instanceof SocketTimeoutException) {
            errorMsg = context.getString(R.string.connect_timeout);
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            errorMsg = convertStatusCode(context, httpException);
        } else if (e instanceof ParseException || e instanceof JSONException) {
            errorMsg = context.getString(R.string.parse_data_error);
        }
        return errorMsg;
    }

    /**
     * 状态码
     *
     * @param httpException
     * @return
     */
    private static String convertStatusCode(Context context, HttpException httpException) {
        String msg;
        if (httpException.code() >= 500 && httpException.code() < 600) {
            msg = context.getString(R.string.server_error);
        } else if (httpException.code() >= 400 && httpException.code() < 500) {
            msg = context.getString(R.string.server_can_not_deal_error);
        } else if (httpException.code() >= 300 && httpException.code() < 400) {
            msg = context.getString(R.string.server_reset_host);
        } else {
            msg = httpException.message();
        }
        return msg;
    }
}
