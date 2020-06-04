package com.lake.banner.loader;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import com.lake.banner.VideoGravityType;
import com.lake.banner.R;
import com.lake.banner.uitls.MD5Util;
import java.io.File;

/**
 * 图片代理实现
 */
public class ImageLoader implements ViewLoaderInterface<ImageView> {
    public ImageLoader() {
    }

    @Override
    public ImageView createView(Context context,int gravity) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.values()[gravity]);
        return imageView;
    }

    @Override
    public void onPrepared(Context context, Object path, ImageView imageView,String cachePath) {
        try {
            if (path instanceof Integer) {
                imageView.setImageResource((int) path);
            } else if (path instanceof Uri) {
                String pStr = path.toString();
                String type = pStr.substring(pStr.lastIndexOf("."));
                File file = new File(cachePath + File.separator + MD5Util.md5(path.toString()) + type);
                if (file.exists()) {
                    Log.e("lake", "onPrepared: isCache");
                    imageView.setImageURI(Uri.fromFile(file));
                } else {
                    Log.e("lake", "onPrepared: noCache");
                    imageView.setImageResource(R.mipmap.defaultvideobg);
                }
            } else {
                imageView.setImageBitmap(BitmapFactory.decodeFile((String) path));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(ImageView imageView) {
        System.gc();
    }
}
