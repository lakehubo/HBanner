package com.lake.base;

import android.os.Environment;

import java.io.File;

/**
 * @author lake
 * create by 2021/8/6 5:48 下午
 */
public class Constants {
    /**
     * retrofit  默认读写超时设置 单位：s
     */
    public static final long DEFAULT_TIME = 30;
    /**
     * retrofit 默认连接超时设置 单位：s
     */
    public static final long DEFAULT_CONNECT_TIME = 10;
    /**
     * 崩溃日志存储路径
     */
    public static final String CRASH_LOG_PATH = Environment.getDataDirectory()
            + File.separator + "crash";
}
