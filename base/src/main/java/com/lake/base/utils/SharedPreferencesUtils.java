package com.lake.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;

/**
 * sp 存储工具
 *
 * @author lake
 * create by 2021/8/17 9:51 上午
 */
public class SharedPreferencesUtils {
    private static final String SP_FILE_NAME = "bjbn_bj_cx_sp";
    /**
     * 当前用户id
     */
    public static final String USERID = "user_id";
    /**
     * 当前用户密码
     */
    public static final String PASSWORD = "password";
    /**
     * 当前用户密码
     */
    public static final String COOKIE = "cookie";
    /**
     * 当前角色类型地图地址
     */
    public static final String MAP_SERVER = "map_server_id";

    @StringDef({USERID, PASSWORD, COOKIE, MAP_SERVER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SPKey {
    }

    public static void putString(Context context, @SharedPreferencesUtils.SPKey String tag, String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(tag, value);
        ed.apply();
    }

    public static String getString(Context context, @SharedPreferencesUtils.SPKey String tag) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(tag, "");
    }

    public static void putStringSet(Context context, @SharedPreferencesUtils.SPKey String tag, HashSet<String> value) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putStringSet(tag, value);
        ed.apply();
    }

    public static HashSet<String> getStringSet(Context context, @SharedPreferencesUtils.SPKey String tag) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return (HashSet<String>) sp.getStringSet(tag, null);
    }

    public static void putInt(Context context, @SharedPreferencesUtils.SPKey String tag, int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(tag, value);
        ed.apply();
    }

    public static int getInt(Context context, @SharedPreferencesUtils.SPKey String tag) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(tag, -1);
    }

    public static void putLong(Context context, @SharedPreferencesUtils.SPKey String tag, long value) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putLong(tag, value);
        ed.apply();
    }

    public static long getLong(Context context, @SharedPreferencesUtils.SPKey String tag) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(tag, -1L);
    }

    public static void removeCookie(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(COOKIE).apply();
    }
}
