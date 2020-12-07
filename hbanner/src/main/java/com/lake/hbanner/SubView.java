package com.lake.hbanner;

import android.view.View;

/**
 * 轮换view的接口
 *
 * @author lake
 * create by 11/17/20 2:41 PM
 */
public interface SubView {
    /**
     * 开始显示，该方法会返回HBanner对象，此时你可以在这里接管
     * 轮播控制，比如暂停，并自行计时播放下一张等操作。当view为视频时，
     * 推荐自行控制视频显示时间。
     */
    void onShowStart(final HBanner hBanner, int position);

    /**
     * 结束显示
     */
    void onShowFinish();

    /**
     * 当前view显示的时间 单位ms
     *
     * @return
     */
    long duration();

    /**
     * 获取子view
     *
     * @return
     */
    View getView();

    /**
     * 当前view不为ImageView时候，比如为VideoView，
     * 则需要覆盖此方法，返回一张图片替代VideoView的显示，
     * 该方法主要为了视频未加载完全时候的显示以及循环播放
     * 的首尾画面的过度
     *
     * @return
     */
    View getPreView();

    /**
     * 同步所用的tag 标记，该接口只有在多banner协同下才有用
     *
     * @return
     */
    String getTag();
}
