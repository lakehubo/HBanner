package com.nbicc.hbanner;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.lake.banner.transformer.VerticalPageTransformer;
import com.lake.banner.view.BannerViewPager;
import com.lake.hbanner.HBanner;
import com.lake.hbanner.ImageSubView;
import com.lake.hbanner.SubView;
import com.lake.hbanner.VideoSubView;
import com.lake.hbanner.VideoViewType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener {
    private Button add, remove;
    private BannerViewPager viewPager, viewPager2;
    private HBanner hBanner, hBanner2;
    private View rootView;

    public String[] NEEDED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this)
                .inflate(R.layout.activity_main, null);
        setContentView(rootView);
        viewPager = findViewById(R.id.viewpager);
        viewPager2 = findViewById(R.id.viewpager2);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);

        add.setOnClickListener(this);
        remove.setOnClickListener(this);
    }

    @Override
    String[] needPermissions() {
        return NEEDED_PERMISSIONS;
    }

    @Override
    void afterPermissions() {
        init();
    }

    @Override
    public void onGlobalLayout() {
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        verifyStoragePermissions(this);
    }

    private void init() {
        /**
         * 在create banner前需要确保viewpager控件已经被创建
         * 这里是双viewpager，为了方便所以直接对根布局进行视图创建
         * 进行回调
         */
        hBanner = HBanner.create(viewPager);

        List<SubView> data = new ArrayList<>();
        data.add(new ImageSubView.Builder(getBaseContext())
                .url("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4148675854,1608370142&fm=26&gp=0.jpg")
                .duration(6000)
                .build());
        data.add(new ImageSubView.Builder(getBaseContext())
                .resId(R.mipmap.b2)
                .duration(5000)
                .build());
        data.add(new ImageSubView.Builder(getBaseContext())
                .resId(R.mipmap.b3)
                .duration(5000)
                .build());
        data.add(new VideoSubView.Builder(getBaseContext())
                .url("https://v-cdn.zjol.com.cn/123468.mp4")
                .gravity(VideoViewType.FULL)
                .isSub(false)
                .build());

        hBanner2 = HBanner.create(viewPager2);
        List<SubView> data2 = new ArrayList<>();
        //被同步banner无需设置时间
        data2.add(new ImageSubView.Builder(getBaseContext())
                .url("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1607078748657&di=32e2fa257aa53426f8ab1fbcb43e1325&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20180629%2Fd444958986894a6994533eda59edb460.jpeg")
                .build());
        data2.add(new ImageSubView.Builder(getBaseContext())
                .resId(R.mipmap.b2)
                .build());
        data2.add(new ImageSubView.Builder(getBaseContext())
                .resId(R.mipmap.b3)
                .build());
        data2.add(new VideoSubView.Builder(getBaseContext())
                .url("https://v-cdn.zjol.com.cn/276982.mp4")
                .isSub(true)
                .build());

        hBanner2.sources(data2);
        hBanner.sources(data);
        //设置viewpager切换方式
        viewPager.setPageTransformer(true, new VerticalPageTransformer());

        hBanner.addSyncHBanner(hBanner2);
        //开始显示或者自动播放
        hBanner.play(true);
    }

    @Override
    protected void onResume() {
        if (hBanner != null)
            hBanner.play(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (hBanner != null)
            hBanner.pause(0);
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                hBanner.addSubView(0, new ImageSubView.Builder(getBaseContext())
                        .resId(R.mipmap.defalteimage)
                        .duration(5000)
                        .build());
                hBanner.play(true);
                break;
            case R.id.remove:
                hBanner.removeSyncHBanner(hBanner2);
                hBanner.play(true);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        if (hBanner != null)
            hBanner = null;
        super.onStop();
    }
}
