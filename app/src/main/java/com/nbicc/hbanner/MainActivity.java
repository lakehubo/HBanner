package com.nbicc.hbanner;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lake.banner.Banner;
import com.lake.banner.BannerConfig;
import com.lake.banner.loader.LocalImageLoader;
import com.lake.banner.loader.LocalVideoLoader;
import com.lake.banner.loader.ViewItemBean;
import com.lake.banner.transformer.DefaultTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Banner banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);

        List<ViewItemBean> list = new ArrayList<>();
        String path1 = Environment.getExternalStorageDirectory() + File.separator + "MyLocalPlayer" + File.separator + "default.mp4";
        String path2 = Environment.getExternalStorageDirectory() + File.separator + "MyLocalPlayer" + File.separator + "default2.mp4";
        String path3 = Environment.getExternalStorageDirectory() + File.separator + "MyLocalPlayer" + File.separator + "default3.mp4";
        String path4 = Environment.getExternalStorageDirectory() + File.separator + "MyLocalPlayer" + File.separator + "default4.mp4";
        list.add(new ViewItemBean(path1, 15 * 1000));
        list.add(new ViewItemBean(R.mipmap.b3, 10 * 1000));
        list.add(new ViewItemBean(path2, 15 * 1000));
        list.add(new ViewItemBean(R.mipmap.b1, 10 * 1000));
        list.add(new ViewItemBean(path3, 15 * 1000));
        list.add(new ViewItemBean(path4, 15 * 1000));
        list.add(new ViewItemBean(R.mipmap.b2, 10 * 1000));


        //下屏广告
        banner.setViews(list)
                .setBannerAnimation(DefaultTransformer.class)
                .setImageLoader(new LocalImageLoader())
                .setVideoLoader(new LocalVideoLoader())
                .setBannerStyle(BannerConfig.NUM_INDICATOR)
                .start();

    }

}
