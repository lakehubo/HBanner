package com.lake.hbanner;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.lake.banner.uitls.LogUtils;

import java.util.ArrayList;
import java.util.List;

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
    /**
     * 显示用的集合
     */
    private final List<View> usingItems = new ArrayList<>();
    /**
     * 当前位置，开始位置为1，实际为item中的0
     */
    private int curPosition = 1;
    private int lastPosition = -1;

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
    };
    private final Handler handler;

    private final Runnable autoRunnable = () -> showNext(true);

    HBannerImp(ViewPager viewPager) {
        this.viewPager = viewPager;
        handler = viewPager.getHandler();
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            boolean dragging = false;

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case SCROLL_STATE_IDLE://final position
                        if (dragging) {
                            final int position = curPosition;
                            if (position == usingItems.size() - 1) {
                                viewPager.setCurrentItem(1, false);
                                handler.postDelayed(autoRunnable, items.get(0).duration());
                                LogUtils.e("lake","idel1");
                            } else if (position == 0) {
                                viewPager.setCurrentItem(usingItems.size() - 2, false);
                                handler.postDelayed(autoRunnable, items.get(items.size() - 1).duration());
                                LogUtils.e("lake","idel2");
                            } else {
                                handler.postDelayed(autoRunnable, items.get(getRealPosition(curPosition)).duration());
                                LogUtils.e("lake","idel3");
                            }
                            dragging = false;
                        }
                        break;
                    case SCROLL_STATE_DRAGGING://if user dragging it
                        handler.removeCallbacks(autoRunnable);
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
                Log.e("lake", "onPageSelected: " + position);
                if (position > 0 && position < usingItems.size() - 1) {
                    if (lastPosition != -1)
                        items.get(getRealPosition(lastPosition)).onShowFinish();
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
        items.clear();
        items.addAll(subViews);
        updateUsingItems();
        notifyAdapter();
    }

    @Override
    public void remove(int position) {
        items.remove(position);
        updateUsingItems();
        notifyAdapter();
    }

    @Override
    public void addSubView(SubView sub) {
        items.add(sub);
        updateUsingItems();
        notifyAdapter();
    }

    @Override
    public void addSubView(SubView sub, int position) {
        items.add(position, sub);
        updateUsingItems();
        notifyAdapter();
    }

    @Override
    public void pause(long timeout) {
        handler.removeCallbacks(autoRunnable);
        if (timeout > 0) {
            handler.postDelayed(autoRunnable, timeout);
        }
    }

    @Override
    public void play() {
        if (items.size() > 0) {
            viewPager.setCurrentItem(curPosition);
            handler.postDelayed(autoRunnable, items.get(getRealPosition(curPosition)).duration());
        }
    }

    @Override
    public void showNext(boolean auto) {
        final int position = curPosition;
        int prePosition = (position + 1) % (usingItems.size());
        Log.e("lake", "run: " + prePosition);
        boolean smoothScroll = isSmoothScroll(prePosition);
        viewPager.setCurrentItem(prePosition, smoothScroll);
        if (auto) {
            if (smoothScroll) {
                handler.postDelayed(autoRunnable, items.get(getRealPosition(prePosition)).duration());
                Log.e("lake", "run: " + items.get(getRealPosition(prePosition)).duration());
            } else {
                handler.post(autoRunnable);
            }
        }
    }

    @Override
    public void setPosition(int position) {
        viewPager.setCurrentItem(position + 1);
    }

    @Override
    public int getPosition() {
        return getRealPosition(viewPager.getCurrentItem());
    }

    @Override
    public SubView getCurrentSubView() {
        return items.get(getRealPosition(curPosition));
    }

    @Override
    public SubView getSubView(int position) {
        return items.get(position);
    }

    private void notifyAdapter() {
        pagerAdapter.notifyDataSetChanged();
    }

    /**
     * 0-6 转换为 0-4 因为人为加入2个子view，首尾滑动衔接
     *
     * @param position
     * @return
     */
    private int getRealPosition(int position) {
        if (position == 0)
            return items.size() - 1;
        if (position == usingItems.size() - 1)
            return 0;
        return position - 1;
    }

    /**
     * 是否有滑动动画
     *
     * @param position
     * @return
     */
    private boolean isSmoothScroll(int position) {
        if (position == 0 || position == usingItems.size() - 1)
            return false;
        return true;
    }

    /**
     * 自动填充首尾元素
     */
    private void updateUsingItems() {
        usingItems.clear();
        usingItems.add(items.get(items.size() - 1).getPreView());//first
        for (SubView subView : items) {
            usingItems.add(subView.getView());
        }
        usingItems.add(items.get(0).getPreView());//end
    }
}
