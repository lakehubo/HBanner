package com.lake.base.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

/**
 * 白名单请求工具封装
 *
 * @author lake
 * create by 2022/3/10 4:14 下午
 */
public class AppFirmUtils {
    /**
     * 请求白名单结果标识
     */
    private static final int REQUEST_WHITE_CODE = 0x201;

    /**
     * 判断当前应用是否存在于系统白名单中
     *
     * @param context
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);
            if (powerManager != null)
                isIgnoring = powerManager.isIgnoringBatteryOptimizations(info.applicationInfo.packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isIgnoring;
    }

    /**
     * 请求加入白名单
     *
     * @param activity
     */
    public static void requestIgnoreBatteryOptimizations(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(activity.getPackageName(), 0);
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse(info.applicationInfo.packageName));
            activity.startActivityForResult(intent, REQUEST_WHITE_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到指定应用的首页
     */
    public static void showActivity(String packageName, Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    /**
     * 跳转到指定应用的指定页面
     */
    public static void showActivity(String packageName, String activityDir, Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断手机厂商
     * @return
     */
    public static String checkPhoneFirm() {
        String phoneState = Build.BRAND.toLowerCase(); //获取手机厂商
        if (phoneState.equals("huawei") || phoneState.equals("honor"))
            return PhoneConstant.IS_HUAWEI;
        else if (phoneState.equals("xiaomi") && Build.BRAND != null)
            return PhoneConstant.IS_XIAOMI;
        else if (phoneState.equals("oppo") && Build.BRAND != null)
            return PhoneConstant.IS_OPPO;
        else if (phoneState.equals("vivo") && Build.BRAND != null)
            return PhoneConstant.IS_VIVO;
        else if (phoneState.equals("meizu") && Build.BRAND != null)
            return PhoneConstant.IS_MEIZU;
        else if (phoneState.equals("samsung") && Build.BRAND != null)
            return PhoneConstant.IS_SAMSUNG;
        else if (phoneState.equals("letv") && Build.BRAND != null)
            return PhoneConstant.IS_LETV;
        else if (phoneState.equals("smartisan") && Build.BRAND != null)
            return PhoneConstant.IS_SMARTISAN;

        return "";
    }

    /**
     * 前往设置管理
     * @param context
     */
    public static void gotoWhiteListSetting(Context context) {
        if (checkPhoneFirm().equals(PhoneConstant.IS_HUAWEI)) {
            try {
                showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity", context);
            } catch (Exception e) {
                showActivity("com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.bootstart.BootStartActivity", context);
            }
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_XIAOMI)) {
            showActivity("com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity", context);
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_OPPO)) {
            //oppo:操作步骤：权限隐私 -> 自启动管理 -> 允许应用自启动
            try {
                showActivity("com.coloros.phonemanager", context);
            } catch (Exception e) {
                try {
                    showActivity("com.oppo.safe", context);
                } catch (Exception e2) {
                    try {
                        showActivity("com.coloros.oppoguardelf", context);
                    } catch (Exception e3) {
                        showActivity("com.coloros.safecenter", context);
                    }
                }
            }
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_VIVO)) {
            //vivo:操作步骤：权限管理 -> 自启动 -> 允许应用自启动
            showActivity("com.iqoo.secure", context);
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_MEIZU)) {
            //魅族:操作步骤：权限管理 -> 后台管理 -> 点击应用 -> 允许后台运行
            showActivity("com.meizu.safe", context);
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_SAMSUNG)) {
            //三星:操作步骤：自动运行应用程序 -> 打开应用开关 -> 电池管理 -> 未监视的应用程序 -> 添加应用
            try {
                showActivity("com.samsung.android.sm_cn", context);
            } catch (Exception e) {
                showActivity("com.samsung.android.sm", context);
            }
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_LETV)) {
            //乐视:操作步骤：自启动管理 -> 允许应用自启动
            showActivity("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity", context);
        } else if (checkPhoneFirm().equals(PhoneConstant.IS_SMARTISAN)) {
            //锤子:操作步骤：权限管理 -> 自启动权限管理 -> 点击应用 -> 允许被系统启动
            showActivity("com.smartisanos.security", context);
        }
    }
}
