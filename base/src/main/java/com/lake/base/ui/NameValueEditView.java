package com.lake.base.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lake.base.R;

/**
 * 通用的名称：值 编辑框
 * @author lake
 * create by 2021/10/19 5:21 下午
 */
public class NameValueEditView extends LinearLayout {
    private Context context;
    private TextView name;
    private EditText editText;

    public NameValueEditView(Context context) {
        this(context,null);
    }

    public NameValueEditView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NameValueEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        attrView(context, attrs);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.nv_editview_layout, this, true);
        name = view.findViewById(R.id.nv_name);
        editText = view.findViewById(R.id.nv_edit);
    }

    private void attrView(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.NameValueEditView);
            String nameStr = typedArray.getString(R.styleable.NameValueEditView_ev_name);
            if (!TextUtils.isEmpty(nameStr)) {
                name.setText(nameStr);
            }
            String value = typedArray.getString(R.styleable.NameValueEditView_ev_value);
            if (!TextUtils.isEmpty(value)) {
                editText.setText(value);
            }
            String hintValue = typedArray.getString(R.styleable.NameValueEditView_ev_hint);
            if (!TextUtils.isEmpty(hintValue)) {
                editText.setHint(hintValue);
            }
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        editText.setOnClickListener(l);
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String s) {
        editText.setText(s);
    }

    public void enable(boolean b) {
        editText.setEnabled(b);
    }

    public EditText getEditText() {
        return editText;
    }

}
