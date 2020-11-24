package com.lake.hbanner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * sub view 基类
 *
 * @author lake
 * create by 11/20/20 2:35 PM
 */
public abstract class ShowView implements SubView {
    protected ImageView mImageCache;

    public ShowView(Context context) {
        mImageCache = new ImageView(context);
    }

    @Override
    public void onShowStart(HBanner hBanner, int position) {

    }

    @Override
    public void onShowFinish() {
    }

    /**
     * 因为当前视图还未显示到屏幕中，所以并没有进行渲染，所以无法拿到drawCache。
     * 所以，还是建议给imagecache设置一个背景返回，充当过度，当然过度view只有首尾
     * 两个view才会有效，非首尾view并不会调用此方法。
     *
     * @return
     */
    @Override
    public View getPreView() {
        if (getView() == null)
            throw new RuntimeException("the getView can not be null!");
        return mImageCache;
    }
}
