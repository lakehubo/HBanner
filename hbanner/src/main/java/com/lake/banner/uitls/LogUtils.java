package com.lake.banner.uitls;

import android.util.Log;

public class LogUtils {
    public static boolean show_log = false;

    public static void i(String tag, String msg) {
        if (show_log)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (show_log)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (show_log)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (show_log)
            Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (show_log)
            Log.w(tag, msg);
    }
}
