package com.nbicc.hbanner;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.lake.banner.HBanner;
import com.lake.banner.BannerStyle;
import com.lake.banner.loader.ViewItemBean;
import com.lake.banner.transformer.DefaultTransformer;
import com.lake.banner.transformer.VerticalPageTransformer;

import java.util.ArrayList;
import java.util.List;
import static com.lake.banner.BannerConfig.IMAGE;
import static com.lake.banner.BannerConfig.NO_TIME_SET;
import static com.lake.banner.BannerConfig.VIDEO;

public class MainActivity extends AppCompatActivity {
    private HBanner banner;
    private HBanner banner2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        banner2 = findViewById(R.id.banner2);

        /**
         * 视频图片混播
         */
        List<ViewItemBean> list = new ArrayList<>();
        Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/280443.mp4");
        Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
        list.add(new ViewItemBean(VIDEO, path1, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b3, 2 * 1000));
        list.add(new ViewItemBean(VIDEO, path2, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b1, 2 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b2, 2 * 1000));

        banner.setViews(list)
                .setBannerAnimation(DefaultTransformer.class)
                .setBannerStyle(BannerStyle.NUM_INDICATOR)
                .start();

        /**
         * 纯视频点播模式
         */
        List<ViewItemBean> list2 = new ArrayList<>();
        list.add(new ViewItemBean(VIDEO, path1, NO_TIME_SET));
        list.add(new ViewItemBean(VIDEO, path2, NO_TIME_SET));
        banner2.setViews(list2)
                .setBannerAnimation(VerticalPageTransformer.class)
                .setBannerStyle(BannerStyle.NUM_INDICATOR_TITLE)
                .start();
    }

}
