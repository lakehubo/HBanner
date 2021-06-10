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
     * 轮播数据列表，在轮播状态下调用此方法会导致轮播暂停
     *
     * @param subViews
     */
    void sources(List<SubView> subViews);

    /**
     * 移除指定位置的subview，在轮播状态下调用此方法会导致轮播暂停
     *
     * @param position
     */
    void remove(int position);

    /**
     * 在结尾添加新的subview，在轮播状态下调用此方法会导致轮播暂停
     *
     * @param sub
     */
    void addSubView(SubView sub);

    /**
     * 在指定位置添加subview，在轮播状态下调用此方法会导致轮播暂停
     *
     * @param position
     * @param sub
     */
    void addSubView(int position, SubView sub);

    /**
     * 暂停轮播，timeout时间后自动继续，timeout为0表示永久暂停
     *
     * @param timeout
     */
    void pause(long timeout);

    /**
     * 开始轮播,若当前存在暂停则继续当前位置进行播放
     *
     * @param auto 是否自动播放
     */
    void play(boolean auto);

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
     * 获取banner当前的状态
     * {@link PlayStatus}
     *
     * @return
     */
    PlayStatus getBannerStatus();

    /**
     * 设置每次的轮播时间偏移，设置该值后会给所有的view加上该值
     *
     * @param time 单位ms
     */
    void setTimeOffset(long time);

    /**
     * 设置同步模式
     * {@link SyncMode#SYNC_BY_INDEX} one by one模式
     *
     */
    void setSyncMode(SyncMode mode);

    /**
     * 同步另一banner，多banner协同
     * 根据传入的item序号进行同步，
     * 协同支持两种模式，一种需要
     * 两个banner的item数量保持一致，否则会导致其中
     * 一个banner的item无法显示完整
     * 另一种不需要 两个banner的item数量保持一致，会自动计算不同banner中
     * 的同一tag并进行时间计算，多tag部分会平均少tag的总时间
     *
     * @param hBanner
     */
    void addSyncHBanner(HBanner hBanner);

    void addSyncHBanner(List<HBanner> hBanners);

    /**
     * 移除同步的banner
     *
     * @param hBanner
     */
    void removeSyncHBanner(HBanner hBanner);

    void removeSyncHBanner(List<HBanner> hBanners);

    /**
     * 移除所有同步的banner
     */
    void removeAllSyncHBanner();

    /**
     * 利用viewPager创建对应的操作接口
     *
     * @param viewPager
     * @return
     */
    static HBanner create(ViewPager viewPager) {
        return new HBannerImp(viewPager);
    }

    /**
     * 释放所有资源以及清空绑定的viewPager
     */
    void release();

    /**
     * 是否为自动播放
     * @return
     */
    boolean isAuto();
}
