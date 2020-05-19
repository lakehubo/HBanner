package com.lake.banner;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.lake.banner.listener.OnBannerListener;
import com.lake.banner.loader.ImageLoader;
import com.lake.banner.loader.VideoLoader;
import com.lake.banner.loader.VideoViewLoaderInterface;
import com.lake.banner.loader.ViewItem;
import com.lake.banner.loader.ViewItemBean;
import com.lake.banner.loader.ViewLoaderInterface;
import com.lake.banner.net.HttpCallback;
import com.lake.banner.net.HttpClient;
import com.lake.banner.net.HttpParam;
import com.lake.banner.net.HttpThreadPool;
import com.lake.banner.uitls.Constants;
import com.lake.banner.uitls.MD5Util;
import com.lake.banner.view.BannerViewPager;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import static androidx.viewpager.widget.ViewPager.PageTransformer;

public class HBanner extends FrameLayout implements OnPageChangeListener {
    public static final String TAG = HBanner.class.getSimpleName();
    private int mIndicatorMargin = BannerConfig.PADDING_SIZE;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int indicatorSize;
    private int bannerBackgroundImage;
    private int bannerStyle = BannerStyle.CIRCLE_INDICATOR;
    private int scrollTime = BannerConfig.DURATION;
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean isScroll = BannerConfig.IS_SCROLL;
    private boolean isCache = true;
    private String cachePath = Constants.DEFAULT_DOWNLOAD_DIR;//缓存地址
    private boolean isShowTitle = false;//是否显示标题
    private int viewGravity = BannerGravity.CENTER;
    private int mIndicatorSelectedResId = R.drawable.gray_radius;
    private int mIndicatorUnselectedResId = R.drawable.white_radius;
    private int mLayoutResId = R.layout.banner;
    private int titleHeight;
    private int titleBackground;
    private int titleTextColor;
    private int titleTextSize;
    private int count = 0;//总数
    private int currentItem;//当前页码
    private int gravity = -1;
    private int lastPosition = 1;
    private final List<ViewItemBean> itemList;//传入的子视图对象
    private final List<ViewItem> subList;//实际轮播的子视图列表 比传入的多了两个
    private int lastItem;//当前视频子窗口
    private List<ImageView> indicatorImages;//指示器数组
    private Context context;
    private BannerViewPager viewPager;
    private TextView bannerTitle, numIndicatorInside, numIndicator;
    private LinearLayout indicator, indicatorInside, titleView;
    private ImageView bannerDefaultImage;
    private ViewLoaderInterface imageLoader;
    private VideoViewLoaderInterface videoLoader;
    private BannerPagerAdapter adapter;
    private OnPageChangeListener mOnPageChangeListener;
    private BannerScroller mScroller;
    private OnBannerListener listener;
    private DisplayMetrics dm;

    /**
     * 记录item下一次 切换的时间
     */
    private long changeTime = 0;
    /**
     * 当前subview的延迟时间 当点击画面时，
     * 需要记录当前画面剩余的切换时间，
     * 便于释放手指后，若当前页面没有切换，则继续执行剩下的延迟时间
     */
    private int currentDelayTime = 0;

    private WeakHandler handler = new WeakHandler();

    public HBanner(Context context) {
        this(context, null);
    }

    public HBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        subList = new ArrayList<>();
        itemList = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        dm = context.getResources().getDisplayMetrics();
        indicatorSize = dm.widthPixels / 80;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        itemList.clear();
        handleTypedArray(context, attrs);
        View view = LayoutInflater.from(context).inflate(mLayoutResId, this, true);
        bannerDefaultImage = view.findViewById(R.id.bannerDefaultImage);
        viewPager = view.findViewById(R.id.bannerViewPager);
        titleView = view.findViewById(R.id.titleView);
        indicator = view.findViewById(R.id.circleIndicator);
        indicatorInside = view.findViewById(R.id.indicatorInside);
        bannerTitle = view.findViewById(R.id.bannerTitle);
        numIndicator = view.findViewById(R.id.numIndicator);
        numIndicatorInside = view.findViewById(R.id.numIndicatorInside);
        bannerDefaultImage.setImageResource(bannerBackgroundImage);
        initViewPagerScroll();
    }

    private void handleTypedArray(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HBanner);
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.HBanner_indicator_width, indicatorSize);
        mIndicatorHeight = typedArray.getDimensionPixelSize(R.styleable.HBanner_indicator_height, indicatorSize);
        mIndicatorMargin = typedArray.getDimensionPixelSize(R.styleable.HBanner_indicator_margin, BannerConfig.PADDING_SIZE);
        mIndicatorSelectedResId = typedArray.getResourceId(R.styleable.HBanner_indicator_drawable_selected, R.drawable.gray_radius);
        mIndicatorUnselectedResId = typedArray.getResourceId(R.styleable.HBanner_indicator_drawable_unselected, R.drawable.white_radius);
        scrollTime = typedArray.getInt(R.styleable.HBanner_scroll_time, BannerConfig.DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.HBanner_is_auto_play, BannerConfig.IS_AUTO_PLAY);
        titleBackground = typedArray.getColor(R.styleable.HBanner_title_background, BannerConfig.TITLE_BACKGROUND);
        titleHeight = typedArray.getDimensionPixelSize(R.styleable.HBanner_title_height, BannerConfig.TITLE_HEIGHT);
        titleTextColor = typedArray.getColor(R.styleable.HBanner_title_textcolor, BannerConfig.TITLE_TEXT_COLOR);
        titleTextSize = typedArray.getDimensionPixelSize(R.styleable.HBanner_title_textsize, BannerConfig.TITLE_TEXT_SIZE);
        mLayoutResId = typedArray.getResourceId(R.styleable.HBanner_banner_layout, mLayoutResId);
        bannerBackgroundImage = typedArray.getResourceId(R.styleable.HBanner_banner_default_image, R.drawable.no_banner);
        typedArray.recycle();
    }

    private void initViewPagerScroll() {
        try {
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new BannerScroller(viewPager.getContext());
            mScroller.setDuration(scrollTime);
            mField.set(viewPager, mScroller);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public HBanner isAutoPlay(boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
        return this;
    }

    //设置图片加载器
    public HBanner setImageLoader(ViewLoaderInterface imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    // 设置视频加载器
    public HBanner setVideoLoader(VideoViewLoaderInterface videoLoader) {
        this.videoLoader = videoLoader;
        return this;
    }

    /**
     * 显示标题
     *
     * @param showTitle
     * @return
     */
    public HBanner setShowTitle(boolean showTitle) {
        this.isShowTitle = showTitle;
        return this;
    }

    public HBanner setIndicatorGravity(@IntRange(from = IndicatorGravity.LEFT, to = IndicatorGravity.RIGHT) int type) {
        switch (type) {
            case IndicatorGravity.LEFT:
                this.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                break;
            case IndicatorGravity.CENTER:
                this.gravity = Gravity.CENTER;
                break;
            case IndicatorGravity.RIGHT:
                this.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }
        return this;
    }

    public HBanner setBannerAnimation(Class<? extends PageTransformer> transformer) {
        try {
            setPageTransformer(true, transformer.newInstance());
        } catch (Exception e) {
            Log.e(TAG, "Please set the PageTransformer class");
        }
        return this;
    }

    /**
     * Set the number of pages that should be retained to either side of the
     * current page in the view hierarchy in an idle state. Pages beyond this
     * limit will be recreated from the adapter when needed.
     *
     * @param limit How many pages will be kept offscreen in an idle state.
     * @return Banner
     */
    public HBanner setOffscreenPageLimit(int limit) {
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(limit);
        }
        return this;
    }

    /**
     * Set a {@link PageTransformer} that will be called for each attached page whenever
     * the scroll position is changed. This allows the application to apply custom property
     * transformations to each page, overriding the default sliding look and feel.
     *
     * @param reverseDrawingOrder true if the supplied PageTransformer requires page views
     *                            to be drawn from last to first instead of first to last.
     * @param transformer         PageTransformer that will modify each page's animation properties
     * @return Banner
     */
    public HBanner setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        viewPager.setPageTransformer(reverseDrawingOrder, transformer);
        return this;
    }

    public HBanner setBannerStyle(@IntRange(from = BannerStyle.NOT_INDICATOR
            , to = BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE) int bannerStyle) {
        this.bannerStyle = bannerStyle;
        return this;
    }

    //cache video and image
    public HBanner setCache(boolean cache) {
        this.isCache = cache;
        return this;
    }

    /**
     * 设置缓存地址
     *
     * @param path
     * @return
     */
    public HBanner setCachePath(String path) {
        if (!TextUtils.isEmpty(path))
            this.cachePath = path;
        return this;
    }

    @IntDef({BannerGravity.CENTER, BannerGravity.CENTER_HORIZONTAL, BannerGravity.FULL_SCREEN})
    @Retention(RetentionPolicy.SOURCE)
    private @interface subViewGravity {
    }

    //子视图显示位置
    public HBanner setViewGravity(@subViewGravity int gravity) {
        this.viewGravity = gravity;
        return this;
    }

    public HBanner setViewPagerIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    //设置子试图列表
    public HBanner setViews(List<ViewItemBean> list) {
        itemList.clear();
        itemList.addAll(list);
        this.count = itemList.size();
        return this;
    }

    public void update(List<ViewItemBean> list) {
        if (count > 0 && isAutoPlay) {
            stopAutoPlay();
        }
        lastItem = 0;
        currentDelayTime = 0;
        changeTime = 0;
        this.itemList.clear();
        this.subList.clear();
        this.indicatorImages.clear();
        this.itemList.addAll(list);
        this.count = this.itemList.size();
        start();
    }

    public void updateBannerStyle(int bannerStyle) {
        indicator.setVisibility(GONE);
        numIndicator.setVisibility(GONE);
        numIndicatorInside.setVisibility(GONE);
        indicatorInside.setVisibility(GONE);
        bannerTitle.setVisibility(View.GONE);
        titleView.setVisibility(View.GONE);
        this.bannerStyle = bannerStyle;
        start();
    }

    public void start() {
        checkCache(itemList);
        checkLoader();
        setBannerStyleUI();
        setItemViewList(itemList);
        setViewPagerViews();
    }

    public void onResume() {
        if (isAutoPlay) {
            if (currentDelayTime > 0)
                startAutoPlay(currentDelayTime);
            else
                startAutoPlay();
        }
    }

    public void onPause() {
        if (isAutoPlay) {
            stopAutoPlay();
        }
    }

    public void onStop() {
        if (isAutoPlay && subList.size() > 1) {
            currentDelayTime = subList.get(currentItem).getTime();
        }
    }

    /**
     * 设置标题样式
     */
    private void setTitleStyleUI() {
        bannerTitle.setVisibility(isShowTitle ? View.VISIBLE : View.GONE);
        titleView.setVisibility(isShowTitle ? View.VISIBLE : View.GONE);
        if (titleBackground != -1) {
            titleView.setBackgroundColor(titleBackground);
        }
        if (titleHeight != -1) {
            titleView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight));
        }
        if (titleTextColor != -1) {
            bannerTitle.setTextColor(titleTextColor);
        }
        if (titleTextSize != -1) {
            bannerTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        }
    }

    /**
     * 缓存网络视频和网络图片
     */
    private void checkCache(List<ViewItemBean> list) {
        //缓存视频和图片
        if (list.size() != 0) {
            for (ViewItemBean bean : list) {
                if (bean.getUrl() instanceof Uri) {
                    if (bean.getType() == BannerConfig.VIDEO && !isCache)
                        continue;
                    Uri uri = (Uri) bean.getUrl();
                    String pStr = uri.toString();
                    if(!pStr.contains("http"))
                        continue;
                    String type = pStr.substring(pStr.lastIndexOf("."));
                    String cacheFilePath = MD5Util.md5(pStr);
                    Log.e("lake", "checkCache: " + cacheFilePath + type);
                    File file = new File(cachePath + File.separator + cacheFilePath + type);
                    if (!file.exists()) {
                        cacheFile(uri.toString(), file);
                    }
                }
            }
        }
    }

    private void cacheFile(String url, File file) {
        HttpThreadPool.getInstance().post(() -> {
            HttpParam httpParam = new HttpParam(url);
            httpParam.setFileName(file.getName());
            httpParam.setFile(file);
            httpParam.setSavePath(cachePath);
            HttpClient.getInstance().Get(httpParam, new HttpCallback.ProgressRequestHttpCallback<File>() {
                @Override
                public void success(File result) {
                    Log.e("lake", "success: " + result.getName());
                }

                @Override
                public void failed(String Msg) {
                    Log.e("lake", "failed: " + Msg);
                    if (file.exists())
                        file.delete();
                }

                @Override
                public void progress(float progress, float count) {
                    float percent = progress / count * 100;
                    Log.e("lake", "progress: " + String.format("%.2f", percent) + "%");
                }
            });
        });
    }

    /**
     * 检查加载器是否设置，不设置则创建默认的加载器
     */
    private void checkLoader() {
        if (imageLoader == null)
            imageLoader = new ImageLoader();
        if (videoLoader == null)
            videoLoader = new VideoLoader();
    }

    private void setBannerStyleUI() {
        int visibility = count > 1 ? View.VISIBLE : View.GONE;
        switch (bannerStyle) {
            case BannerStyle.CIRCLE_INDICATOR:
                indicator.setVisibility(visibility);
                break;
            case BannerStyle.NUM_INDICATOR:
                numIndicator.setVisibility(visibility);
                break;
            case BannerStyle.NUM_INDICATOR_TITLE:
                numIndicatorInside.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerStyle.CIRCLE_INDICATOR_TITLE:
                indicator.setVisibility(visibility);
                setTitleStyleUI();
                break;
            case BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE:
                indicatorInside.setVisibility(visibility);
                setTitleStyleUI();
                break;
        }
    }

    private void initSubList() {
        subList.clear();
        if (bannerStyle == BannerStyle.CIRCLE_INDICATOR ||
                bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE) {
            createIndicator();
        } else if (bannerStyle == BannerStyle.NUM_INDICATOR_TITLE) {
            numIndicatorInside.setText("1/" + count);
        } else if (bannerStyle == BannerStyle.NUM_INDICATOR) {
            numIndicator.setText("1/" + count);
        }
    }

    /**
     * 设置显示内容列表
     *
     * @param itemList
     */
    private void setItemViewList(List<ViewItemBean> itemList) {
        if (itemList == null || itemList.size() <= 0) {
            bannerDefaultImage.setVisibility(VISIBLE);
            Log.e(TAG, "The image data set is empty.");
            return;
        }
        bannerDefaultImage.setVisibility(GONE);
        initSubList();
        for (int i = 0; i <= count + 1; i++) {
            ViewItemBean itemBean = null;
            if (i == 0) {
                itemBean = itemList.get(count - 1);//实际第一张放置最后一张图片
                if (itemBean.getType() == BannerConfig.VIDEO)
                    itemBean = new ViewItemBean();
            } else if (i == count + 1) {
                itemBean = itemList.get(0);//实际最后一张放置第一张图片
                if (itemBean.getType() == BannerConfig.VIDEO)
                    itemBean = new ViewItemBean();
            } else {
                itemBean = itemList.get(i - 1);
            }
            setViewByLoader(itemBean);
        }
    }

    /**
     * 根据类型生成view
     *
     * @param viewItemBean
     */
    private void setViewByLoader(ViewItemBean viewItemBean) {
        boolean isVideo = viewItemBean.getType() == BannerConfig.VIDEO;
        View v = null;
        ViewLoaderInterface loader = isVideo ? videoLoader : imageLoader;
        if (loader != null) {
            v = loader.createView(context, viewGravity);
        }
        if (v == null) {
            v = isVideo ? new VideoView(context) : new ImageView(context);
        }
        ViewItem viewItem = new ViewItem(v, viewItemBean);
        subList.add(viewItem);
    }

    private void createIndicator() {
        indicatorImages.clear();
        indicator.removeAllViews();
        indicatorInside.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            params.leftMargin = mIndicatorMargin;
            params.rightMargin = mIndicatorMargin;
            if (i == 0) {
                imageView.setImageResource(mIndicatorSelectedResId);
            } else {
                imageView.setImageResource(mIndicatorUnselectedResId);
            }
            indicatorImages.add(imageView);
            if (bannerStyle == BannerStyle.CIRCLE_INDICATOR ||
                    bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE)
                indicator.addView(imageView, params);
            else if (bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE)
                indicatorInside.addView(imageView, params);
        }
    }

    private void setViewPagerViews() {
        currentItem = 1;
        if (adapter == null) {
            adapter = new BannerPagerAdapter();
            viewPager.addOnPageChangeListener(this);
        }
        viewPager.setAdapter(adapter);
        viewPager.setFocusable(true);
        viewPager.setCurrentItem(1);
        if (gravity != -1)
            indicator.setGravity(gravity);
        if (isScroll && count > 1) {
            viewPager.setScrollable(true);
        } else {
            viewPager.setScrollable(false);
        }
        if (isAutoPlay)
            startAutoPlay();
    }

    private void startAutoPlay() {
        handler.removeCallbacks(task);
        int delayTime = count > 0 ? subList.get(currentItem).getTime() : 0;
        changeTime = System.currentTimeMillis() + delayTime;
        Log.i("auto", "startAutoPlay: " + delayTime);
        handler.postDelayed(task, delayTime);
    }

    /**
     * @param delayTime
     */
    private void startAutoPlay(int delayTime) {
        Log.i("auto", "startAutoPlay: " + delayTime);
        startPositionVideoView(currentItem);
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }

    private void stopAutoPlay() {
        Log.i("auto", "stopAutoPlay: ");
        stopPositionVideoView(currentItem);
        if (changeTime != 0) {
            currentDelayTime = (int) (changeTime - System.currentTimeMillis());
            Log.i("auto", "剩余延迟: " + currentDelayTime);
        }
        handler.removeCallbacks(task);
    }

    //循环任务
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (count > 1 && isAutoPlay) {
                currentItem = currentItem % (count + 1) + 1;
                if (currentItem == 1) {
                    viewPager.setCurrentItem(currentItem, false);
                } else {
                    viewPager.setCurrentItem(currentItem);
                }
                int delayTime = subList.get(currentItem).getTime();
                changeTime = System.currentTimeMillis() + delayTime;
                handler.postDelayed(task, delayTime);
            } else {
                if (count == 1 && subList.size() > 1) {//单视频循环
                    if (subList.get(1).getType() == BannerConfig.VIDEO) {
                        int delayTime = subList.get(1).getTime();
                        View view = subList.get(1).getView();
                        if (view instanceof VideoView) {
                            videoLoader.displayView(context, (VideoView) view);
                        }
                        changeTime = System.currentTimeMillis() + delayTime;
                        handler.postDelayed(task, delayTime);
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isAutoPlay) {
            int page = currentItem;
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                stopAutoPlay();
            }
            super.dispatchTouchEvent(ev);//保证onPageSelected在up前执行
            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_CANCEL
                    || action == MotionEvent.ACTION_OUTSIDE) {
                if (page == currentItem) {
                    Log.i("auto", "没切换画面");
                    startAutoPlay(Math.max(0, currentDelayTime));
                } else {
                    Log.i("auto", "切换了！");
                    startAutoPlay();
                }
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    public int toRealPosition(int position) {
        int realPosition = (position - 1) % count;
        if (realPosition < 0)
            realPosition += count;
        return realPosition;
    }

    class BannerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return subList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Log.e("lake", "instantiateItem: " + position);
            ViewItem item = subList.get(position);
            View view = item.getView();
            container.addView(view);
            if (listener != null) {
                view.setOnClickListener(v -> {
                    listener.OnBannerClick(toRealPosition(position));
                });
            }
            if (view instanceof VideoView) {
                videoLoader.onPrepared(context, item.getUrl(), (VideoView) view, cachePath);
            }
            if (view instanceof ImageView) {
                imageLoader.onPrepared(context, item.getUrl(), view, cachePath);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.e("lake", "destroyItem: " + position);
            View item = (View) object;
            container.removeView(item);
            if (item instanceof VideoView) {
                videoLoader.onDestroy((VideoView) item);
            }
            if (item instanceof ImageView) {
                imageLoader.onDestroy(item);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        switch (state) {
            case 0://No operation
            case 1://start Sliding
                if (currentItem == 0) {//第一张图片
                    viewPager.setCurrentItem(count, false);
                } else if (currentItem == count + 1) {
                    viewPager.setCurrentItem(1, false);
                }
                break;
            case 2://end Sliding
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(toRealPosition(position), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("auto", "onPageSelected: " + position);
        if (lastItem != 0 && lastItem != position)
            stopPositionVideoView(lastItem);
        lastItem = position;

        currentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(toRealPosition(position));
        }

        displayPositionVideoView(position);

        if (bannerStyle == BannerStyle.CIRCLE_INDICATOR ||
                bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE ||
                bannerStyle == BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE) {
            indicatorImages.get((lastPosition - 1 + count) % count).setImageResource(mIndicatorUnselectedResId);
            indicatorImages.get((position - 1 + count) % count).setImageResource(mIndicatorSelectedResId);
            lastPosition = position;
        }
        if (position == 0) position = count;
        if (position > count) position = 1;
        switch (bannerStyle) {
            case BannerStyle.CIRCLE_INDICATOR:
                break;
            case BannerStyle.NUM_INDICATOR:
                numIndicator.setText(position + "/" + count);
                break;
            case BannerStyle.NUM_INDICATOR_TITLE:
                numIndicatorInside.setText(position + "/" + count);
                bannerTitle.setText(subList.get(position).getTitle());
                break;
            case BannerStyle.CIRCLE_INDICATOR_TITLE:
            case BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE:
                bannerTitle.setText(subList.get(position).getTitle());
                break;
        }
    }

    private void stopPositionVideoView(int position) {
        ViewItem v = subList.get(position);
        View view = v.getView();
        if (view instanceof VideoView) {
            videoLoader.onStop((VideoView) view);
        }
    }

    private void startPositionVideoView(int position) {
        ViewItem v = subList.get(position);
        View view = v.getView();
        if (view instanceof VideoView) {
            videoLoader.onResume((VideoView) view);
        }
    }

    private void displayPositionVideoView(int position) {
        ViewItem v = subList.get(position);
        View view = v.getView();
        if (view instanceof VideoView) {
            videoLoader.displayView(context, (VideoView) view);
        }
    }

    /**
     * @param listener
     * @return
     */
    public HBanner setOnBannerListener(OnBannerListener listener) {
        this.listener = listener;
        return this;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        mOnPageChangeListener = onPageChangeListener;
    }

    public void releaseBanner() {
        stopAutoPlay();
        handler.removeCallbacksAndMessages(null);
    }
}
