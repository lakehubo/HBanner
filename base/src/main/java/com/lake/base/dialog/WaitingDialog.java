package com.lake.base.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.lake.base.R;
import com.lake.base.utils.AnimationHelper;

/**
 * 网络请求等待框
 * @author lake
 * create by 2021/10/26 1:39 下午
 */
public class WaitingDialog extends DialogFragment {
    private View view;
    public ImageView ivWaiting;
    public TextView tvWaiting;
    protected Animation animation;
    private String waitMsg = "";

    public static WaitingDialog newInstance() {
        Bundle args = new Bundle();
        WaitingDialog fragment = new WaitingDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.WaitingDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消背景遮罩
        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getDialog().setCanceledOnTouchOutside(false);
        view = inflater.inflate(R.layout.dialog_waiting, container, false);
        ivWaiting = view.findViewById(R.id.iv_waiting);
        tvWaiting = view.findViewById(R.id.tv_waiting_msg);
        tvWaiting.setText(waitMsg);
        startAnimation();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            dialog.getWindow().setLayout((int) (displayMetrics.widthPixels * 0.8),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
        }
    }

    private void startAnimation() {
        if (animation == null) {
            animation = AnimationHelper.makeRotateAnimationFarword(1500);
        }
        if (ivWaiting != null) {
            ivWaiting.setAnimation(animation);
            animation.start();
        }
    }

    private void stopAnimation() {
        if (animation != null) {
            animation.cancel();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAnimation();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        manager.executePendingTransactions();
        if (!isAdded()) {
            super.show(manager, tag);
            startAnimation();
        }
    }

    public void setText(String string) {
        waitMsg = string;
        if (tvWaiting != null) {
            tvWaiting.setText(string);
        }
    }
}
