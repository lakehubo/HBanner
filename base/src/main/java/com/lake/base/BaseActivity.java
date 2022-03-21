package com.lake.base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.lake.base.dialog.WaitingDialog;
import com.lake.base.net.NetWorkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

/**
 * @author lake
 * create by 2021/8/6 5:23 下午
 * activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    public static final int PERMISSION_CODE = 0x110;
    private String[] permissions;
    private WaitingDialog waitingDialog;

    /**
     * 应用所需的权限
     *
     * @return
     */
    protected abstract String[] hasPermissions();

    /**
     * 初始化
     *
     * @return
     */
    protected abstract @LayoutRes
    int initLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());
        initContentView();
        ButterKnife.bind(this);
        permissions = hasPermissions();
        checkActivityPermission();
        initData();
    }

    protected void initContentView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkStatus();
    }

    @Override
    protected void onPause() {
        unRegisterNetworkStatus();
        super.onPause();
    }

    /**
     * 权限检测完毕
     */
    public void allGrant() {
    }

    /**
     * 存在无授权的权限
     *
     * @param permissions
     */
    public void hasDenied(String[] permissions) {
    }

    /**
     * 检测当前权限
     */
    protected void checkActivityPermission() {
        if (permissions != null && permissions.length > 0) {
            boolean denied = false;
            for (String p : permissions) {
                if (checkSelfPermission(p) == PERMISSION_DENIED) {
                    denied = true;
                    requestPermissions(permissions, PERMISSION_CODE);
                    break;
                }
            }
            if (!denied) {
                allGrant();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean denied = false;
                List<String> deniedAList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int grant = grantResults[i];
                    if (grant == PERMISSION_DENIED) {
                        deniedAList.add(permissions[i]);
                        denied = true;
                    }
                }
                if (denied) {
                    hasDenied(deniedAList.toArray(new String[0]));
                } else {
                    allGrant();
                }
            }
        }
    }

    /**
     * 注册监听网络状态
     */
    private void registerNetworkStatus() {
        Context context = getBaseContext();
        if (context != null) {
            ConnectivityManager networkService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                networkService.registerDefaultNetworkCallback(networkCallback);
            } else {
                networkService.registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
            }
        }
    }

    /**
     * 注销监听网络状态
     */
    private void unRegisterNetworkStatus() {
        Context context = getBaseContext();
        if (context != null) {
            ConnectivityManager networkService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkService.unregisterNetworkCallback(networkCallback);
        }
    }

    /**
     * 网络状态回调
     */
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            Log.d(TAG, "onCapabilitiesChanged: 网络状态改变--->" + networkCapabilities.toString());
            if (NetWorkUtils.isEnableToNetwork(getBaseContext(), networkCapabilities)) {
                Log.d(TAG, "onCapabilitiesChanged ---> 网络可用");
            }
        }
    };

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private Handler mHandler;

    public Handler getMainHandler() {
        if (mHandler == null) {
            mHandler = new Handler(getMainLooper());
        }
        return mHandler;
    }

    /**
     * 显示加载框
     *
     * @param res
     */
    public void showWaitDialog(int res) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("wt");
        if (fragment instanceof WaitingDialog) {
            waitingDialog = (WaitingDialog) fragment;
        }
        if (waitingDialog == null) {
            waitingDialog = WaitingDialog.newInstance();
        }
        getSupportFragmentManager().executePendingTransactions();
        if (waitingDialog.isAdded()) {
            waitingDialog.setText(getString(res));
        } else {
            waitingDialog.setText(getString(res));
            getSupportFragmentManager().beginTransaction().add(waitingDialog, "wt").commitAllowingStateLoss();
        }
    }

    /**
     * 隐藏加载框
     */
    public void hideWaitDialog() {
        if (waitingDialog != null) {
            getMainHandler().postDelayed(() -> waitingDialog.dismissAllowingStateLoss(), 100);
        }
    }
}
