package com.lake.hbanner;

import androidx.viewpager.widget.ViewPager;

import java.util.List;

/**
 * 新版banner接口
 *
 * @author lake
 * create by 11/17/20 2:38 PM
 */
public interface HBanner {
    /**
     * 轮播数据列表
     *
     * @param subViews
     */
    void sources(List<SubView> subViews);

    /**
     * 移除指定位置的subview
     *
     * @param position
     */
    void remove(int position);

    /**
     * 在结尾添加新的subview
     *
     * @param sub
     */
    void addSubView(SubView sub);

    /**
     * 在指定位置添加subview
     *
     * @param sub
     * @param position
     */
    void addSubView(SubView sub, int position);

    /**
     * 暂停轮播，timeout时间后自动继续，timeout为0表示永久暂停
     *
     * @param timeout
     */
    void pause(long timeout);

    /**
     * 开始轮播,若当前存在暂停则继续当前位置进行播放
     */
    void play();

    /**
     * 切换到下一张view
     *
     * @param auto 是否后面自动播放
     */
    void showNext(boolean auto);

    /**
     * 重置当前播放位置
     *
     * @param position
     */
    void setPosition(int position);

    /**
     * 获取当前播放位置
     *
     * @return
     */
    int getPosition();

    /**
     * 获取当前显示的子view
     *
     * @return
     */
    SubView getCurrentSubView();

    /**
     * 获取指定位置的子view
     *
     * @param position
     * @return
     */
    SubView getSubView(int position);

    /**
     * 利用viewPager创建对应的操作接口
     *
     * @param viewPager
     * @return
     */
    static HBanner create(ViewPager viewPager) {
        return new HBannerImp(viewPager);
    }
}
