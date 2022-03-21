package com.lake.base.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * 环境测试工具包
 *
 * @author lake
 * create by 2022/2/23 11:50 上午
 */
public class EnvironmentUtil {
    private static final String TAG = EnvironmentUtil.class.getSimpleName();
    /**
     * 请求gps结果标识
     */
    private static final int REQUEST_GPS_CODE = 0x101;

    /**
     * 判断网络是否已经连接，当调用整机的方法出现异常的时候，返回true 由ping和http来判断
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return ((networkInfo != null) && networkInfo.isConnected());
        } catch (Exception e) {
            Log.e(TAG, " isNetworkConnect exception:" + e.getMessage());
            return true;
        }
    }


    /**
     * 判断Gps状态
     */
    public static boolean isGpsEnable(Context context) {
        return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断蓝牙状态
     * 需要先判断gps状态
     */
    public static boolean isBluetoothEnable() {
        BluetoothAdapter blue = BluetoothAdapter.getDefaultAdapter();
        return blue != null && blue.isEnabled();
    }

    /**
     * 请求开启gps
     *
     * @param activity
     */
    public static void requestEnableGps(Activity activity) {
        if (!isGpsEnable(activity)) {
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(locationIntent, REQUEST_GPS_CODE);
        }
    }

    /**
     * 请求开启蓝牙
     *
     * @param activity
     */
    public static void requestEnableBluetooth(Activity activity) {
        if (!isGpsEnable(activity)) {
            Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(locationIntent, REQUEST_GPS_CODE);
        }
    }

}
