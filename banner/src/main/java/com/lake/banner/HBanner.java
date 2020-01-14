package com.lake.banner;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.IntRange;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.provider.MediaStore;
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
import java.lang.reflect.Field;
import java.net.URI;
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
    private int delayTime = BannerConfig.TIME;
    private int scrollTime = BannerConfig.DURATION;
    private boolean isAutoPlay = BannerConfig.IS_AUTO_PLAY;
    private boolean isScroll = BannerConfig.IS_SCROLL;
    private boolean isCache = true;
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
    private int scaleType = 1;
    private List<String> titles;
    private List imageUrls;
    private final List<ViewItem> itemViews;//内容
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
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        itemViews = new ArrayList<>();
        indicatorImages = new ArrayList<>();
        dm = context.getResources().getDisplayMetrics();
        indicatorSize = dm.widthPixels / 80;
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        itemViews.clear();
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
        scaleType = typedArray.getInt(R.styleable.HBanner_image_scale_type, scaleType);
        delayTime = typedArray.getInt(R.styleable.HBanner_delay_time, BannerConfig.TIME);
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

    public HBanner setDelayTime(int delayTime) {
        this.delayTime = delayTime;
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

    public HBanner setBannerTitles(List<String> titles) {
        this.titles = titles;
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

    public HBanner setViewPagerIsScroll(boolean isScroll) {
        this.isScroll = isScroll;
        return this;
    }

    //设置子试图列表
    public HBanner setViews(List<?> imageUrls) {
        this.imageUrls = imageUrls;
        this.count = imageUrls.size();
        return this;
    }

    public void update(List<?> imageUrls, List<String> titles) {
        this.titles.clear();
        this.titles.addAll(titles);
        update(imageUrls);
    }

    public void update(List<?> imageUrls) {
        this.imageUrls.clear();
        this.itemViews.clear();
        this.indicatorImages.clear();
        this.imageUrls.addAll(imageUrls);
        this.count = this.imageUrls.size();
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

    public HBanner start() {
        checkCache(imageUrls);
        checkLoader();
        setBannerStyleUI();
        setImageList(imageUrls);
        setViewPagerViews();
        return this;
    }

    private void setTitleStyleUI() {
        if (titles.size() != imageUrls.size()) {
            throw new RuntimeException("[Banner] --> The number of titles and images is different");
        }
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
        if (titles != null && titles.size() > 0) {
            bannerTitle.setText(titles.get(0));
            bannerTitle.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 缓存视频
     */
    private void checkCache(List<ViewItemBean> list) {
        //先实现缓存视频
        if (isCache && list.size() != 0) {
            for (ViewItemBean bean : list) {
                if (bean.getType() == BannerConfig.VIDEO) {
                    if (bean.getUrl() instanceof Uri) {
                        Uri uri = (Uri) bean.getUrl();
                        String pStr = uri.toString();
                        String type = pStr.substring(pStr.lastIndexOf("."));
                        String cacheFilePath = MD5Util.md5(pStr);
                        Log.e(TAG, "checkCache: " + cacheFilePath + type);
                        File file = new File(Constants.DEFAULT_DOWNLOAD_DIR + File.separator + cacheFilePath + type);
                        if (!file.exists()) {
                            cacheVideo(uri.toString(), file);
                        }
                    }
                }
            }
        }
    }

    private void cacheVideo(String url, File file) {
        HttpThreadPool.getInstance().post(() -> {
            HttpParam httpParam = new HttpParam(url);
            httpParam.setFileName(file.getName());
            httpParam.setFile(file);
            httpParam.setSavePath(Constants.DEFAULT_DOWNLOAD_DIR);
            HttpClient.getInstance().Get(httpParam, new HttpCallback.ProgressRequestHttpCallback<File>() {
                @Override
                public void success(File result) {
                    Log.e("lake", "success: " + result.getName());
                }

                @Override
                public void failed(String Msg) {
                    Log.e("lake", "failed: ");
                    if (file.exists())
                        file.delete();
                }

                @Override
                public void progress(float progress, float count) {
                    Log.e("lake", "progress: " + progress + "/" + count);
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

    private void initImages() {
        itemViews.clear();
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
     * @param imagesUrl
     */
    private void setImageList(List<ViewItemBean> imagesUrl) {
        if (imagesUrl == null || imagesUrl.size() <= 0) {
            bannerDefaultImage.setVisibility(VISIBLE);
            Log.e(TAG, "The image data set is empty.");
            return;
        }
        bannerDefaultImage.setVisibility(GONE);
        initImages();
        for (int i = 0; i <= count + 1; i++) {
            ViewItemBean itemBean = null;
            if (i == 0) {
                itemBean = imagesUrl.get(count - 1);//实际第一张放置最后一张图片
                if (itemBean.getType() == BannerConfig.VIDEO) {
                    //如果最后一张是视频 插一张黑图片
                    setViewByLoader(new ViewItemBean());
                    continue;
                }
            } else if (i == count + 1) {
                itemBean = imagesUrl.get(0);//实际最后一张放置第一张图片
                if (itemBean.getType() == BannerConfig.VIDEO) {
                    //如果第一张是视频 插一张黑图片
                    setViewByLoader(new ViewItemBean());
                    return;
                }
            } else {
                itemBean = imagesUrl.get(i - 1);
            }
            setViewByLoader(itemBean);
        }
    }

    //加载子视图
    private void setViewByLoader(ViewItemBean viewItemBean) {
        Object url = viewItemBean.getUrl();
        int time = viewItemBean.getTime();
        boolean isVideo = viewItemBean.getType() == BannerConfig.VIDEO;
        View v = null;
        ViewLoaderInterface loader = isVideo ? videoLoader : imageLoader;
        if (loader != null) {
            v = loader.createView(context);
        }
        if (v == null) {
            v = isVideo ? new VideoView(context) : new ImageView(context);
        }
        setScaleType(v);
        ViewItem viewItem = new ViewItem(v, viewItemBean);
        itemViews.add(viewItem);
    }

    private void setScaleType(View imageView) {
        if (imageView instanceof ImageView) {
            ImageView view = ((ImageView) imageView);
            switch (scaleType) {
                case 0:
                    view.setScaleType(ScaleType.CENTER);
                    break;
                case 1:
                    view.setScaleType(ScaleType.CENTER_CROP);
                    break;
                case 2:
                    view.setScaleType(ScaleType.CENTER_INSIDE);
                    break;
                case 3:
                    view.setScaleType(ScaleType.FIT_CENTER);
                    break;
                case 4:
                    view.setScaleType(ScaleType.FIT_END);
                    break;
                case 5:
                    view.setScaleType(ScaleType.FIT_START);
                    break;
                case 6:
                    view.setScaleType(ScaleType.FIT_XY);
                    break;
                case 7:
                    view.setScaleType(ScaleType.MATRIX);
                    break;
            }

        }
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


    public void startAutoPlay() {
        handler.removeCallbacks(task);
        handler.postDelayed(task, delayTime);
    }

    public void stopAutoPlay() {
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
                handler.postDelayed(task, delayTime);
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.i(tag, ev.getAction() + "--" + isAutoPlay);
//        if (isAutoPlay) {
//            int action = ev.getAction();
//            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
//                    || action == MotionEvent.ACTION_OUTSIDE) {
//                startAutoPlay();
//            } else if (action == MotionEvent.ACTION_DOWN) {
//                stopAutoPlay();
//            }
//        }
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
            return itemViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Log.e("lake", "instantiateItem: " + position);
            ViewItem item = itemViews.get(position);
            View view = item.getView();
            container.addView(view);
            if (listener != null) {
                view.setOnClickListener(v -> {
                    listener.OnBannerClick(toRealPosition(position));
                });
            }
            if (view instanceof VideoView) {
                videoLoader.onPrepared(context, item.getUrl(), (VideoView) view);
            }
            if (view instanceof ImageView) {
                imageLoader.onPrepared(context, item.getUrl(), view);
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
        Log.e("lake", "onPageSelected: " + position);
        if (lastItem != position) {
            ViewItem v = itemViews.get(lastItem);
            View view = v.getView();
            if (view instanceof VideoView) {
                videoLoader.onStop((VideoView) view);
            }
        }
        lastItem = position;

        currentItem = position;
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(toRealPosition(position));
        }

        ViewItem v = itemViews.get(position);
        View view = v.getView();
        if (view instanceof VideoView) {
            videoLoader.displayView(context, (VideoView) view);
        }
        delayTime = v.getTime();

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
                bannerTitle.setText(titles.get(position - 1));
                break;
            case BannerStyle.CIRCLE_INDICATOR_TITLE:
                bannerTitle.setText(titles.get(position - 1));
                break;
            case BannerStyle.CIRCLE_INDICATOR_TITLE_INSIDE:
                bannerTitle.setText(titles.get(position - 1));
                break;
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
