package com.lake.banner.listener;


/**
 * 旧版接口，由于返回的下标是从1开始，下标越界而废弃（因为有人使用所以不能直接删除）
 */
@Deprecated
public interface OnBannerClickListener {
    void OnBannerClick(int position);
}
