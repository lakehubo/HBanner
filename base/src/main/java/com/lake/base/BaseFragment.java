package com.lake.base;

import static android.os.Looper.getMainLooper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lake.base.dialog.WaitingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author lake
 * create by 2021/8/6 5:24 下午
 * fragment基类
 */
public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;
    private WaitingDialog waitingDialog;

    /**
     * 初始化布局
     *
     * @return
     */
    protected abstract int initLayout();

    /**
     * 初始化view
     *
     * @param view
     */
    protected abstract void initView(View view);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(initLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onHiddenChanged(isHidden());
    }

    @Override
    public void onPause() {
        super.onPause();
        onHiddenChanged(false);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
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
        Fragment fragment = getChildFragmentManager().findFragmentByTag("wt");
        if (fragment instanceof WaitingDialog) {
            waitingDialog = (WaitingDialog) fragment;
        }
        if (waitingDialog == null) {
            waitingDialog = WaitingDialog.newInstance();
        }
        getChildFragmentManager().executePendingTransactions();
        if (waitingDialog.isAdded() || fragment != null) {
            waitingDialog.setText(getString(res));
        } else {
            waitingDialog.setText(getString(res));
            getChildFragmentManager().beginTransaction().add(waitingDialog, "wt").commitAllowingStateLoss();
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
