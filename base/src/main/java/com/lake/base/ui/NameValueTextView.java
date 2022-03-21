package com.lake.base.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lake.base.R;

/**
 * 通用的名称：值显示框
 *
 * @author lake
 * create by 2021/10/19 5:21 下午
 */
public class NameValueTextView extends LinearLayout {
    private Context context;
    private TextView name;
    private TextView tvValue;

    public NameValueTextView(Context context) {
        this(context, null);
    }

    public NameValueTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NameValueTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        attrView(context, attrs);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.nv_textview_layout, this, true);
        name = view.findViewById(R.id.nv_name);
        tvValue = view.findViewById(R.id.nv_text);
    }

    private void attrView(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.NameValueTextView);
            String nameStr = typedArray.getString(R.styleable.NameValueTextView_tv_name);
            if (!TextUtils.isEmpty(nameStr)) {
                name.setText(nameStr);
            }
            String value = typedArray.getString(R.styleable.NameValueTextView_tv_value);
            if (!TextUtils.isEmpty(value)) {
                tvValue.setText(value);
            }
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }
    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        tvValue.setOnClickListener(l);
    }

    public String getText() {
        return tvValue.getText().toString();
    }

    public void setText(String s) {
        tvValue.setText(s);
    }

    public TextView getTextView(){
        return tvValue;
    }

}
