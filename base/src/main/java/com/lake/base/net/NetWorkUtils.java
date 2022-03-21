package com.lake.base.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @author lake
 * create by 2021/8/9 12:02 下午
 */
public class NetWorkUtils {
    /**
     * 检测网络连接状态是否可用，这里采用的第一种方案
     * 以下是可用网络:
     * NetworkCapabilities.TRANSPORT_CELLULAR,
     * NetworkCapabilities.TRANSPORT_WIFI,
     * NetworkCapabilities.TRANSPORT_BLUETOOTH,
     * NetworkCapabilities.TRANSPORT_ETHERNET,
     * NetworkCapabilities.TRANSPORT_VPN,
     * NetworkCapabilities.TRANSPORT_WIFI_AWARE,
     * NetworkCapabilities.TRANSPORT_LOWPAN
     */
    public static boolean isEnableToNetwork(Context context, NetworkCapabilities networkCapabilities) {
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    /**
     * 是否有网络连接，不管是wifi还是数据流量
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        boolean available = info.isAvailable();
        return available;
    }
}
