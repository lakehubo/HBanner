package com.lake.banner;


public class BannerConfig {

    /**
     * banner
     */
    public static final int PADDING_SIZE = 5;
    public static final int TIME = 10 * 1000;//默认时间
    public static final int DURATION = 800;
    public static final boolean IS_AUTO_PLAY = true;
    public static final boolean IS_SCROLL = true;

    /**
     * title style
     */
    public static final int TITLE_BACKGROUND = -1;
    public static final int TITLE_HEIGHT = -1;
    public static final int TITLE_TEXT_COLOR = -1;
    public static final int TITLE_TEXT_SIZE = -1;

    /**
     * type
     */
    public static final int VIDEO = 0;
    public static final int IMAGE = 1;
    //public static final int NO_TIME_SET = -1;//不计时模式（适用于纯视频或者纯图片播放） 暂无实现
}
