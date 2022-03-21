package com.lake.base.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationHelper {
    /**
     * 透明渐变的一种效果
     *
     * @param fromAlpha
     * @param toAlpha
     */
    public static Animation makeAlphaAnimation(float fromAlpha, float toAlpha) {
        return makeAlphaAnimation(fromAlpha, toAlpha, 500);
    }

    public static Animation makeAlphaAnimation(float fromAlpha, float toAlpha, long durationMillis) {
        Animation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setDuration(durationMillis);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }

    /**
     * 整体缩放的一种效果
     *
     * 以下四个值的解释只在RELATIVE_TO_SELF正确
     * fromX：起始该view横向所占的长度与自身宽度比值。
     * toX：结束该view横向所占的长度与自身长度比值。
     * fromY：起始该view纵向所占的长度与自身高度比值。
     * toY：结束该view纵向所占的长度与自身长度比值。
     * 以上四种属性值 0.0表示收缩到没有， 1.0表示正常无伸缩， 值小于1.0表示收缩， 值大于1.0表示放大
     * pivotXType：X轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
     * pivotXValue：为动画相对于物件的X坐标的开始位置
     * pivotYType：Y轴的伸缩模式，可以取值为ABSOLUTE、RELATIVE_TO_SELF、RELATIVE_TO_PARENT。
     * pivotYValue：为动画相对于物件的Y坐标的开始位置
     * pivotXValue、pivotYValue 从0%-100%中取值
     * 50%为物件的X或Y方向坐标上的中点位置 ,如果是伸长，则是端点左右（上下）两边同时伸长toX-fromX（toY-fromy）
     * 100%为物体的右端点（下端点），如果是伸长，则只是向左（上）伸长toX-fromX（toY-fromy），端点的另一边无任何动作。
     */
    public static Animation makeScaleAnimation(float fromScaleValue, float toScaleValue) {
        return makeScaleAnimation(fromScaleValue, toScaleValue, 500);
    }

    public static Animation makeScaleAnimation(float fromScaleValue, float toScaleValue, long durationMillis) {
        Animation alphaAnimation = new ScaleAnimation(fromScaleValue, toScaleValue, fromScaleValue, toScaleValue, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        alphaAnimation.setDuration(durationMillis);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }

    /**
     * 旋转的一种效果
     *
     * @param fromDegrees
     * @param toDegrees
     */
    public static Animation makeRotateAnimation(int fromDegrees, int toDegrees) {
        return makeRotateAnimation(fromDegrees, toDegrees, 500);
    }

    public static Animation makeRotateAnimation(int fromDegrees, int toDegrees, long durationMillis) {
        Animation alphaAnimation =  new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        alphaAnimation.setDuration(durationMillis);
        return alphaAnimation;
    }

    /**
     * 一直旋转动画
     * @param durationMillis
     * @return
     */
    public static Animation makeRotateAnimationFarword(long durationMillis){
        Animation rotateAnimation =  new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(durationMillis);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(-1);
        return rotateAnimation;
    }

    /**
     * 定义从屏幕顶部进入的动画效果
     *
     * @param context
     */
    public static Animation inFromTopAnimation(Context context) {
        Animation inFromTop = new TranslateAnimation(0.0f, 0.0f, -DensityUtils.getScreenHeight(context), 0.0f);
        inFromTop.setFillAfter(true);
        inFromTop.setDuration(500);
        inFromTop.setRepeatMode(Animation.ZORDER_BOTTOM);
        return inFromTop;
    }

    /**
     * 定义从屏幕顶部退出的动画效果
     *
     * @param context
     */
    public static Animation outToTopAnimation(Context context) {
        Animation outToTop = new TranslateAnimation(0.0f, 0.0f, 0.0f, -DensityUtils.getScreenHeight(context));
        outToTop.setFillAfter(true);
        outToTop.setDuration(500);
        outToTop.setRepeatMode(Animation.ZORDER_BOTTOM);
        return outToTop;
    }

    /**
     * 定义从屏幕底部进入的动画效果
     *
     * @param context
     */
    public static Animation inFromBottomAnimation(Context context) {
        Animation inFromBottom = new TranslateAnimation(0.0f, 0.0f, DensityUtils.getScreenHeight(context), 0.0f);
        inFromBottom.setFillAfter(true);
        inFromBottom.setDuration(500);
        inFromBottom.setRepeatMode(Animation.ZORDER_TOP);
        return inFromBottom;
    }

    /**
     * 定义从屏幕底部退出的动画效果
     *
     * @param context
     */
    public static Animation outToBottomAnimation(Context context) {
        Animation outToBottom = new TranslateAnimation(0.0f, 0.0f, 0.0f, DensityUtils.getScreenHeight(context));
        outToBottom.setFillAfter(true);
        outToBottom.setDuration(500);
        outToBottom.setRepeatMode(Animation.ZORDER_NORMAL);
        return outToBottom;
    }

    /**
     * 定义从左侧进入的动画效果
     */
    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }


    /**
     * 定义从左侧退出的动画效果
     */
    public static Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }


    /**
     * 定义从右侧进入的动画效果
     */
    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    /**
     * 定义从右侧退出时的动画效果
     */
    public static Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }

    //----------------------设备列表点击动效-----------------------//
    public static Animator getDeviceOpenAnimation(View target) {
        target.setScaleX(0);
        target.setScaleY(0);
        target.setAlpha(1);
        PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
        PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
        final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(target, pvhsX, pvhsY);
        animation.setDuration(280);
        return animation;
    }

    public static Animator getDeviceOpenAnimation2(View target) {
        target.setScaleX(1);
        target.setScaleY(1);
        target.setAlpha(0.1f);
        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
        PropertyValuesHolder pvhsX2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.94f);
        PropertyValuesHolder pvhsY2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.94f);

        final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(target, pvhA, pvhsX2, pvhsY2);
        animation.setDuration(200);
        return animation;
    }

    public static Animator getDeviceCloseAnimation2(View target) {
        target.setScaleX(1);
        target.setScaleY(1);
        target.setAlpha(1);
        PropertyValuesHolder pvhsX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
        PropertyValuesHolder pvhsY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
        PropertyValuesHolder pvhA = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
        final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(target, pvhA, pvhsX, pvhsY);
        animation.setDuration(280);
        return animation;
    }

    public static Animator getDeviceCloseAnimation(View target) {
        target.setScaleX(0.94f);
        target.setScaleY(0.94f);

        PropertyValuesHolder pvhsX2 = PropertyValuesHolder.ofFloat(View.SCALE_X, 1);
        PropertyValuesHolder pvhsY2 = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1);
        final ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(target, pvhsX2, pvhsY2);
        animation.setDuration(200);
        return animation;
    }

    //----------------------设备列表点击动效------------------------//

}
