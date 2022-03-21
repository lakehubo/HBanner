package com.lake.base.net;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.lake.base.R;
import com.lake.base.utils.RxExceptionUtils;
import com.lake.base.utils.ToastUtils;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.ResponseBody;

/**
 * 网络回调基类
 *
 * @author lake
 * create by 2021/8/9 11:54 上午
 */
public abstract class BaseObserver<T> implements Observer<T> {
    private Disposable d;
    private Context context;
    private final boolean mShowToast;
    protected final Context mContext;
    private static Gson gson = new Gson();

    public BaseObserver(Context context) {
        this(context, true);
    }

    public BaseObserver(Context mContext, boolean mShowToast) {
        this.mContext = mContext;
        this.mShowToast = mShowToast;
    }

    @Override
    public void onSubscribe(@NonNull Disposable disposable) {
        this.d = disposable;
        if (!NetWorkUtils.isConnected(mContext)) {
            ToastUtils.showToast(mContext, context.getString(R.string.connect_error));
            d.dispose();
        }
    }

    @Override
    public void onNext(@NonNull T result) {
        //检查cookie过期
        onSuccess(result);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (d.isDisposed()) {
            d.dispose();
        }
        String errorMsg = RxExceptionUtils.exceptionHandler(mContext, e);
        if (mShowToast) {
            ToastUtils.myToast(mContext, errorMsg, false);
        }
        onFailure(e, errorMsg, false);
        onComplete();
    }

    @Override
    public void onComplete() {
        onFinish();
    }

    /**
     * 接口回复成功
     *
     * @param result
     */
    public abstract void onSuccess(T result);

    /**
     * 接口回复失败
     *
     * @param e
     * @param errorMsg
     * @param isResponse
     */
    public abstract void onFailure(Throwable e, String errorMsg, boolean isResponse);

    /**
     * 接口回调完成
     */
    public abstract void onFinish();

}
