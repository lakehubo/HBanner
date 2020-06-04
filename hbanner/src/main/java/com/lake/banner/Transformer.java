package com.lake.banner;

import androidx.viewpager.widget.ViewPager.PageTransformer;

import com.lake.banner.transformer.DefaultTransformer;
import com.lake.banner.transformer.VerticalPageTransformer;

public class Transformer {
    public static Class<? extends PageTransformer> Default = DefaultTransformer.class;
    public static Class<? extends PageTransformer> Vertical = VerticalPageTransformer.class;
}
