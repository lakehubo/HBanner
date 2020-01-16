package com.lake.banner.transformer;

import android.view.View;

/**
 * 垂直切换动画
 */
public class VerticalPageTransformer extends ABaseTransformer {
    @Override
    protected void onTransform(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 1) { // [-1,1]
            view.setAlpha(1);
            // Counteract the default slide transition
            view.setTranslationX(view.getWidth() * -position);
            //set Y position to swipe in from top
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
