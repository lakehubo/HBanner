package com.lake.banner;

import android.support.v4.view.ViewPager.PageTransformer;

import com.lake.banner.transformer.AccordionTransformer;
import com.lake.banner.transformer.BackgroundToForegroundTransformer;
import com.lake.banner.transformer.CubeInTransformer;
import com.lake.banner.transformer.CubeOutTransformer;
import com.lake.banner.transformer.DefaultTransformer;
import com.lake.banner.transformer.DepthPageTransformer;
import com.lake.banner.transformer.FlipHorizontalTransformer;
import com.lake.banner.transformer.FlipVerticalTransformer;
import com.lake.banner.transformer.ForegroundToBackgroundTransformer;
import com.lake.banner.transformer.RotateDownTransformer;
import com.lake.banner.transformer.RotateUpTransformer;
import com.lake.banner.transformer.ScaleInOutTransformer;
import com.lake.banner.transformer.StackTransformer;
import com.lake.banner.transformer.TabletTransformer;
import com.lake.banner.transformer.ZoomInTransformer;
import com.lake.banner.transformer.ZoomOutSlideTransformer;
import com.lake.banner.transformer.ZoomOutTranformer;

public class Transformer {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Accordion = AccordionTransformer.class;
    public static Class<? extends PageTransformer> BackgroundToForeground = BackgroundToForegroundTransformer.class;
    public static Class<? extends PageTransformer> ForegroundToBackground = ForegroundToBackgroundTransformer.class;
    public static Class<? extends PageTransformer> CubeIn = CubeInTransformer.class;
    public static Class<? extends PageTransformer> CubeOut = CubeOutTransformer.class;
    public static Class<? extends PageTransformer> DepthPage = DepthPageTransformer.class;
    public static Class<? extends PageTransformer> FlipHorizontal = FlipHorizontalTransformer.class;
    public static Class<? extends PageTransformer> FlipVertical = FlipVerticalTransformer.class;
    public static Class<? extends PageTransformer> RotateDown = RotateDownTransformer.class;
    public static Class<? extends PageTransformer> RotateUp = RotateUpTransformer.class;
    public static Class<? extends PageTransformer> ScaleInOut = ScaleInOutTransformer.class;
    public static Class<? extends PageTransformer> Stack = StackTransformer.class;
    public static Class<? extends PageTransformer> Tablet = TabletTransformer.class;
    public static Class<? extends PageTransformer> ZoomIn = ZoomInTransformer.class;
    public static Class<? extends PageTransformer> ZoomOut = ZoomOutTranformer.class;
    public static Class<? extends PageTransformer> ZoomOutSlide = ZoomOutSlideTransformer.class;
}
