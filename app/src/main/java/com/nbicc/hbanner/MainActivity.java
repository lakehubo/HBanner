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
        Uri path1 = Uri.parse("http://221.12.125.147:8092/files/b79f4e5a-5eb5-4838-a083-a11076754453.mp4");
        Uri path2 = Uri.parse("http://221.12.125.147:8092/files/37efc33a-92b0-4d19-a27f-a39af63ad8a0.mp4");
        Uri imageUrl = Uri.parse("http://221.12.125.147:8092/files/14a27ccf-ff5d-4287-a041-1846c0df1a7d.jpg");
        list.add(new ViewItemBean(VIDEO, path1, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, imageUrl, 2 * 1000));
        list.add(new ViewItemBean(VIDEO, path2, 15 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b1, 2 * 1000));
        list.add(new ViewItemBean(IMAGE, R.mipmap.b2, 2 * 1000));

        banner.setViews(list)
                .setBannerAnimation(DefaultTransformer.class)
                .setBannerStyle(BannerStyle.NUM_INDICATOR)
                .setCache(true)//可以不用设置，默认为true
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
