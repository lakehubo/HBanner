package com.lake.hbanner;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lake.banner.uitls.LogUtils;
import com.lake.banner.view.BannerViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_IDLE;
import static androidx.viewpager.widget.ViewPager.SCROLL_STATE_SETTLING;

/**
 * 新版hbanner接口实现
 *
 * @author lake
 * create by 11/17/20 2:39 PM
 */
/* package */ class HBannerImp implements HBanner {

    final ViewPager viewPager;
    /**
     * 传入的集合
     */
    private final List<SubView> items = new ArrayList<>();

    private final List<View> usingItems = new ArrayList<>();
    /**
     * 当前位置，开始位置为0
     */
    private int curPosition = 1;
    private int lastPosition = -1;
    /**
     * 切换时间补正 大概会有几ms的偏移
     */
    private long timeOffset = 5;

    private boolean isAuto = false;
    /**
     * 当前状态
     */
    private PlayStatus status = PlayStatus.NOT_RUNNING;
    /**
     * 待同步的附属banner
     */
    private final ArrayList<HBannerImp> attachedHBanners = new ArrayList<>();
    /**
     * 协同模式
     */
    private SyncMode mode = SyncMode.SYNC_BY_INDEX;

    private final PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return usingItems.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = usingItems.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    };
    private final Handler handler;

    private final Runnable autoRunnable = () -> showNextInner(true);

    HBannerImp(ViewPager viewPager) {
        this.viewPager = viewPager;
        attachedHBanners.clear();
        checkViewAttached(viewPager);
        handler = viewPager.getHandler();
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            boolean dragging = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case SCROLL_STATE_IDLE://final position
                        LogUtils.e("lake", "onPageScrollStateChanged: idle auto=" + isAuto);
                        final int position = curPosition;
                        if (dragging) {
                            if (position == usingItems.size() - 1) {
                                if (isAuto)
                                    autoPlayAfter(items.get(0).duration() + timeOffset, "drag0");
                            } else if (position == 0) {
                                if (isAuto)
                                    autoPlayAfter(items.get(items.size() - 1).duration() + timeOffset, "drag1");
                            } else {
                                if (isAuto)
                                    autoPlayAfter(items.get(getRealPosition(curPosition)).duration() + timeOffset, "drag2");
                            }
                            dragging = false;
                        }
                        if (position == usingItems.size() - 1) {
                            viewPager.setCurrentItem(1, false);//false会缺失idle回调
                        } else if (position == 0) {
                            viewPager.setCurrentItem(usingItems.size() - 2, false);
                        }
                        break;
                    case SCROLL_STATE_DRAGGING://if user dragging it
                        stopPlay();
                        dragging = true;
                        break;
                    case SCROLL_STATE_SETTLING://the view is settling!
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.e("lake", "onPageSelected: " + position);
                if (lastPosition != -1 && lastPosition != position)
                    isAuto = items.get(getRealPosition(lastPosition)).onShowFinish();

                if (position > 0 && position < usingItems.size() - 1) {
                    LogUtils.e("lake", "onShowFinish: " + lastPosition + ":" + isAuto);
                    SubView subView = items.get(getRealPosition(position));
                    subView.onShowStart(HBannerImp.this, getRealPosition(position));
                    lastPosition = position;
                }
                curPosition = position;
            }
        });
    }

    @Override
    public void sources(List<SubView> subViews) {
        if (subViews == null)
            throw new NullPointerException("subViews is null!");
        pause(0);
        items.clear();
        items.addAll(subViews);
        notifyAdapter();
    }

    @Override
    public void remove(int position) {
        if (position < 0 || position >= items.size())
            throw new IndexOutOfBoundsException("the position is out of the subViews list!");
        pause(0);
        items.remove(position);
        notifyAdapter();
    }

    @Override
    public void addSubView(SubView sub) {
        if (sub == null)
            throw new NullPointerException("subView is null!");
        pause(0);
        items.add(sub);
        notifyAdapter();
    }

    @Override
    public void addSubView(int position, SubView sub) {
        if (position < 0 || position > items.size())
            throw new IndexOutOfBoundsException("the position is out of the subViews list!");
        pause(0);
        items.add(position, sub);
        notifyAdapter();
    }

    @Override
    public void pause(long timeout) {
        isAuto = false;//暂停
        stopPlay();
        if (timeout > 0) {
            autoPlayAfter(timeout, "pause");
        } else {
            status = PlayStatus.NOT_RUNNING;
        }
    }

    @Override
    public void play(boolean auto) {
        stopPlay();
        if (items.size() > 0) {
            isAuto = auto;
            initPosition();
            viewPager.setCurrentItem(curPosition);
            if (isAuto) {
                status = PlayStatus.RUNNING;
                autoPlayAfter(items.get(getRealPosition(curPosition)).duration(), "play");
            }
        }
    }

    /**
     * 重置位置
     */
    private void initPosition() {
        curPosition = 1;
        lastPosition = -1;
    }

    @Override
    public void showNext(boolean auto) {
        isAuto = auto;
        showNextInner(auto);
    }

    private void showNextInner(boolean auto) {
        if (items.size() == 0)
            throw new RuntimeException("the subViews list size is 0!there is not any view to show!");
        final int position = curPosition;
        int prePosition = position + 1;
        boolean smoothScroll = isSmoothScroll(position);
        if (auto) {
            if (smoothScroll) {
                autoPlayAfter(items.get(getRealPosition(prePosition)).duration() + timeOffset, "auto");
            }
        }
        viewPager.setCurrentItem(prePosition, smoothScroll);
    }

    @Override
    public void setPosition(int position) {
        if (position < 0 || position >= items.size())
            throw new IndexOutOfBoundsException("the position is out of the subViews list!");
        viewPager.setCurrentItem(position + 1);
    }

    @Override
    public int getPosition() {
        if (items.size() == 0)
            return -1;
        return viewPager.getCurrentItem();
    }

    @Override
    public SubView getCurrentSubView() {
        if (items.size() == 0)
            return null;
        return items.get(curPosition);
    }

    @Override
    public SubView getSubView(int position) {
        if (position < 0 || position >= items.size())
            throw new IndexOutOfBoundsException("the position is out of the subViews list!");
        return items.get(position);
    }

    @Override
    public PlayStatus getBannerStatus() {
        return status;
    }

    @Override
    public void setTimeOffset(long time) {
        timeOffset = time;
    }

    private void notifyAdapter() {
        updateUsingViewItems();
        pagerAdapter.notifyDataSetChanged();
        initPosition();
    }

    private boolean isSmoothScroll(int position) {
        return position != 0 && position != usingItems.size() - 1;
    }

    /**
     * 获取子元素实际显示的序号
     *
     * @param position
     * @return
     */
    private int getRealPosition(int position) {
        if (position == 0)
            return items.size() - 1;
        if (position == usingItems.size() - 1) {
            return 0;
        }
        return position - 1;
    }

    /**
     * 更新显示列表
     */
    private void updateUsingViewItems(int count) {
        usingItems.clear();
        usingItems.add(items.get(count - 1).getPreView());
        for (int i = 0; i < count; i++) {
            SubView view = items.get(i);
            usingItems.add(view.getView());
        }
        usingItems.add(items.get(0).getPreView());
    }

    private void updateUsingViewItems() {
        updateUsingViewItems(items.size());
    }

    /**
     * 一旦触发该方法，则会一直循环调用autoRunnable方法，
     * 除非调用stopPlay以停止循环
     *
     * @param time
     * @param tag
     */
    private void autoPlayAfter(long time, String tag) {
        LogUtils.d("auto", tag);
        handler.postDelayed(autoRunnable, time);
    }

    /**
     * 停止存放在主线程队列中的autoRunnable任务
     */
    private void stopPlay() {
        handler.removeCallbacks(autoRunnable);
    }

    @Override
    public void setSyncMode(SyncMode mode) {
        this.mode = mode;
    }

    /**
     * 添加协同banner
     *
     * @param hBanner
     */
    @Override
    public void addSyncHBanner(HBanner hBanner) {
        Objects.requireNonNull(hBanner);
        pause(0);
        checkAddedHBannerItem(hBanner);
        initSyncItems();
    }

    @Override
    public void addSyncHBanner(List<HBanner> hBanners) {
        Objects.requireNonNull(hBanners);
        pause(0);
        for (HBanner banner : hBanners)
            checkAddedHBannerItem(banner);
        initSyncItems();
    }

    /**
     * 移除协同banner
     *
     * @param hBanner
     */
    @Override
    public void removeSyncHBanner(HBanner hBanner) {
        Objects.requireNonNull(hBanner);
        pause(0);
        checkRemoveHBannerItem(hBanner);
        initSyncItems();
    }

    @Override
    public void removeSyncHBanner(List<HBanner> hBanners) {
        Objects.requireNonNull(hBanners);
        pause(0);
        for (HBanner banner : hBanners)
            checkRemoveHBannerItem(banner);
        initSyncItems();
    }

    @Override
    public void removeAllSyncHBanner() {
        pause(0);
        attachedHBanners.clear();
        initSyncItems();
    }

    /**
     * 检测banner的item数量，并根据tag进行协同时间分配计算
     *
     * @param hBanner
     */
    private void checkAddedHBannerItem(HBanner hBanner) {
        if (!(hBanner instanceof HBannerImp))
            throw new IllegalArgumentException("the banner type is not the correct original imp!its can not be added!");
        HBannerImp otherImp = (HBannerImp) hBanner;
        if (mode == SyncMode.SYNC_BY_INDEX) {
            if (otherImp.items.size() < items.size())
                throw new IllegalArgumentException("the items of banner by added is must be bigger or same as this banner!");
            if (otherImp.items.size() > items.size()) {
                otherImp.updateUsingViewItems(items.size());
            }
        } //else if (mode == SyncMode.SYNC_BY_TAG) {

        //}
        attachedHBanners.add(otherImp);
    }

    /**
     * 移除协同项
     *
     * @param hBanner
     */
    private void checkRemoveHBannerItem(HBanner hBanner) {
        if (!(hBanner instanceof HBannerImp))
            throw new IllegalArgumentException("the banner type is not the correct original imp!its can not be remove!");
        HBannerImp otherImp = (HBannerImp) hBanner;
        attachedHBanners.remove(otherImp);
    }

    private void initSyncItems() {
        BannerViewPager parentViewPager = convertViewPager();
        parentViewPager.clearAllSyncPager();
        if (mode == SyncMode.SYNC_BY_INDEX) {
            for (HBannerImp banner : attachedHBanners) {
                parentViewPager.addSyncViewPager(banner.convertViewPager());
            }
        } //else if (mode == SyncMode.SYNC_BY_TAG) {

        //}
    }

    private BannerViewPager convertViewPager() {
        if (!(viewPager instanceof BannerViewPager))
            throw new IllegalArgumentException("if you want to use sync banner,please use BannerViewPager!");
        return (BannerViewPager) viewPager;
    }

    /**
     * 检测view是否已加载
     *
     * @param viewPager
     */
    private void checkViewAttached(ViewPager viewPager) {
        if (!viewPager.isAttachedToWindow())
            throw new IllegalArgumentException("your viewPager has not attached,it will can not get the handler!");
    }

    @Override
    public void release() {
        stopPlay();
        attachedHBanners.clear();
        usingItems.clear();
        viewPager.clearOnPageChangeListeners();
        viewPager.setAdapter(null);
        viewPager.removeAllViews();
    }

    @Override
    public boolean isAuto() {
        return isAuto;
    }
}
