package com.lake.banner.view;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.ArrayList;

/**
 * 具备协同功能viewpager
 * @author lake
 */
public class BannerViewPager extends ViewPager {
    private boolean scrollable = true;
    private final ArrayList<BannerViewPager>
            syncViewPager = new ArrayList<>();

    public BannerViewPager(Context context) {
        super(context);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addSyncViewPager(BannerViewPager viewPager) {
        syncViewPager.add(viewPager);
    }

    public void removeSyncViewPager(BannerViewPager viewPager) {
        syncViewPager.remove(viewPager);
    }

    public void clearAllSyncPager(){
        syncViewPager.clear();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        for (BannerViewPager pager : syncViewPager) {
            pager.onTouchEvent(ev);
        }
        if (this.scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0) {
                return false;
            }
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
        for (BannerViewPager pager : syncViewPager) {
            pager.setCurrentItem(item);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
        for (BannerViewPager pager : syncViewPager) {
            pager.setCurrentItem(item, smoothScroll);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        for (BannerViewPager pager : syncViewPager) {
            pager.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        for (BannerViewPager pager : syncViewPager) {
            pager.onInterceptTouchEvent(ev);
        }
        if (this.scrollable) {
            if (getCurrentItem() == 0 && getChildCount() == 0) {
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    public void setScrollable(boolean scrollable) {
        for (BannerViewPager pager : syncViewPager) {
            pager.scrollable = scrollable;
        }
        this.scrollable = scrollable;
    }
}
