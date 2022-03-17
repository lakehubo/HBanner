package com.lake.banner.uitls;

import android.os.Environment;
import java.io.File;

/**
 * 常量配置
 * @author lake
 */
public class Constants {
    public static final String ROOT_DIR = Environment.getExternalStorageDirectory() + File.separator + "HBanner";
    public static final String DEFAULT_DOWNLOAD_DIR = ROOT_DIR + File.separator + "_cache";
}
