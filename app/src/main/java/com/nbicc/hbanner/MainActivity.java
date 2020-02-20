package com.nbicc.hbanner;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.Toast;

import com.lake.banner.BannerGravity;
import com.lake.banner.HBanner;
import com.lake.banner.BannerStyle;
import com.lake.banner.loader.ViewItemBean;
import com.lake.banner.transformer.DefaultTransformer;

import java.util.ArrayList;
import java.util.List;

import static com.lake.banner.BannerConfig.IMAGE;
import static com.lake.banner.BannerConfig.VIDEO;

public class MainActivity extends AppCompatActivity {
    private HBanner banner;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] NEEDED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        banner = findViewById(R.id.banner);
        verifyStoragePermissions(this);
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            //检测权限
            if (!checkPermissions(NEEDED_PERMISSIONS)) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, NEEDED_PERMISSIONS, REQUEST_EXTERNAL_STORAGE);
            } else {
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        /**
         * 视频图片混播
         */
        List<ViewItemBean> list = new ArrayList<>();
        Uri path1 = Uri.parse("https://v-cdn.zjol.com.cn/123468.mp4");
        Uri path2 = Uri.parse("https://v-cdn.zjol.com.cn/276982.mp4");
        Uri imageUrl = Uri.parse("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579170629919&di=fc03a214795a764b4094aba86775fb8f&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D4061015229%2C3374626956%26fm%3D214%26gp%3D0.jpg");
        list.add(new ViewItemBean(VIDEO, path1, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, imageUrl, 2 * 1000));
        list.add(new ViewItemBean(VIDEO, path2, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b1, 2 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b2, 2 * 1000));

        banner.setViews(list)
                .setBannerAnimation(DefaultTransformer.class)
                .setBannerStyle(BannerStyle.NUM_INDICATOR)
                .setCache(true)//可以不用设置，默认为true
                .setViewGravity(BannerGravity.CENTER)
                .start();
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allGranted = true;
        for (int results : grantResults) {
            allGranted &= results == PackageManager.PERMISSION_GRANTED;
        }
        if (allGranted) {
            init();
        } else {
            Toast.makeText(this, "Maybe can not cache the video or play the local resource!", Toast.LENGTH_LONG).show();
        }
    }
}
