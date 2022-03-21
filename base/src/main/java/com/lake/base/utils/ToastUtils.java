package com.lake.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lake.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Toast提示框工具类
 * @author lake
 * create by 2021/8/9 3:41 下午
 */
public class ToastUtils {
    /**
     * 单位：毫秒
     */
    private static final int DEFAULT_TIME_DELAY = 50;
    /**
     * 提示类toast
     */
    private static Toast mToast;
    /**
     * 自定义toast
     */
    private static Toast toast;
    private static Handler mToastHandler;
    private static final List<Toast> toastList = new ArrayList<>();

    /**
     * 显示Toast
     *
     * @param msgResId
     */
    public static void showToast(Context context, int msgResId) {
        if (context != null) {
            showToast(context, context.getString(msgResId));
        }
    }

    /**
     * 显示Toast
     *
     * @param msg
     */
    public static void showToast(Context context, final String msg) {
        if (!(context instanceof Activity)) {
            return;
        }
        if (msg != null && !TextUtils.isEmpty(msg)) {

            if (mToastHandler == null) {
                mToastHandler = new Handler(context.getMainLooper());
            }

            mToastHandler.postDelayed(() -> {

                if (mToast == null) {
                    mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.setText(msg);
                mToast.show();
            }, DEFAULT_TIME_DELAY);
        }
    }

    /**
     * 带icon的提示框
     *
     * @param context
     * @param msgId
     * @param isSuccess
     */
    public static void myToast(Context context, int msgId, boolean isSuccess) {
        if (context != null) {
            myToast(context, context.getString(msgId), isSuccess);
        }
    }

    /**
     * 自定义Toast
     *
     * @param msg
     */
    public static void myToast(Context context, String msg, boolean isSuccess) {
        if ((context instanceof Activity) && msg != null) {

            if (mToastHandler == null) {
                mToastHandler = new Handler(context.getMainLooper());
            }

            mToastHandler.postDelayed(() -> {
                toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                RelativeLayout toastLayout = (RelativeLayout) inflater.inflate(R.layout.custom_toast, null);
                TextView htvCustomInfo = toastLayout.findViewById(R.id.htv_custom_info);
                ImageView ivCustomIcon = toastLayout.findViewById(R.id.iv_custom_icon);
                if (isSuccess) {
                    ivCustomIcon.setImageDrawable(context.getDrawable(R.mipmap.icon_msg_success));
                } else {
                    ivCustomIcon.setImageDrawable(context.getDrawable(R.mipmap.icon_msg_error));
                }
                htvCustomInfo.setText(msg);
                toast.setView(toastLayout);
                toast.show();
            }, DEFAULT_TIME_DELAY);
        }
    }

    /**
     * 不带icon的提示框
     *
     * @param context
     * @param msg
     */
    public static void myToast(Context context, String msg) {
        if ((context instanceof Activity) && msg != null) {
            if (mToastHandler == null) {
                mToastHandler = new Handler(context.getMainLooper());
            }
            mToastHandler.postDelayed(() -> {
                toast = new Toast(context);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                RelativeLayout toastLayout = (RelativeLayout) inflater.inflate(R.layout.custom_toast_no_icon, null);
                TextView htvCustomInfo = toastLayout.findViewById(R.id.htv_custom_info);
                htvCustomInfo.setText(msg);
                toast.setView(toastLayout);
                toast.show();
            }, DEFAULT_TIME_DELAY);
        }
    }
}
