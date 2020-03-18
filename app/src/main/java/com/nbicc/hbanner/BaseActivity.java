package com.nbicc.hbanner;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 0x01;

    abstract void afterPermissions();
    abstract String[] needPermissions();

    public void verifyStoragePermissions(Activity activity) {
        try {
            //检测权限
            if (!checkPermissions(needPermissions())) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, needPermissions(), REQUEST_EXTERNAL_STORAGE);
            } else {
                afterPermissions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            afterPermissions();
        } else {
            Toast.makeText(this, "Maybe can not cache the video or play the local resource!", Toast.LENGTH_LONG).show();
        }
    }
}
