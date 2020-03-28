package com.nbicc.hbanner;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import com.lake.banner.BannerGravity;
import com.lake.banner.HBanner;
import com.lake.banner.BannerStyle;
import com.lake.banner.Transformer;
import com.lake.banner.loader.ViewItemBean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.lake.banner.BannerConfig.IMAGE;
import static com.lake.banner.BannerConfig.VIDEO;

public class MainActivity extends BaseActivity {
    private HBanner banner;

    protected String[] NEEDED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    List<ViewItemBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        verifyStoragePermissions(this);
    }

    @Override
    String[] needPermissions() {
        return NEEDED_PERMISSIONS;
    }

    @Override
    void afterPermissions() {
        init();
    }

    private void init() {
        list = new ArrayList<>();
        Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/123468.mp4");
        Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
        Uri imageUrl = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579170629919&di=fc03a214795a764b4094aba86775fb8f&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D4061015229%2C3374626956%26fm%3D214%26gp%3D0.jpg");
        list.add(new ViewItemBean(VIDEO, "标题1", path1, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题2", imageUrl, 2 * 1000));
        list.add(new ViewItemBean(VIDEO, "标题3", path2, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题4", R.mipmap.b1, 2 * 1000));
        list.add(new ViewItemBean(IMAGE, "标题5", R.mipmap.b2, 2 * 1000));

        banner.setViews(list)
                .setBannerAnimation(Transformer.Default)//换场方式
                .setBannerStyle(BannerStyle.CIRCLE_INDICATOR_TITLE)//指示器模式
                .setCache(false)//可以不用设置，默认为true
                .setCachePath(getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath() + File.separator + "hbanner")
                .setViewGravity(BannerGravity.CENTER)
                .setShowTitle(true)//是否显示标题
                .setViewPagerIsScroll(true)//是否支持手滑
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        banner.onResume();
    }

    @Override
    protected void onPause() {
        banner.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        banner.onStop();
        super.onStop();
    }

}
