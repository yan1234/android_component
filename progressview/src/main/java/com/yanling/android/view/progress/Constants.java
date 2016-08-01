package com.yanling.android.view.progress;

import android.graphics.Color;

/**
 * 常量信息类
 * @author yanling
 * @date 2016-08-01
 */
public class Constants {


    //默认加载进度部分高度（未加载的默认为加载 * 0.9）
    public static final float default_reached_bar_height = 5.0f;

    //设置默认颜色
    public static final int default_text_color = Color.rgb(66, 145, 241);
    public static final int default_reached_color = Color.rgb(66, 145, 241);
    public static final int default_unreached_color = Color.rgb(204, 204, 204);

    //默认字体大小
    public static final int default_text_size = 13;
    //默认进度偏移
    public static final float default_text_offset = 3.0f;

    //默认前后缀
    public static final String default_prefix = "";
    public static final String default_suffix = "%";

    //设置默认最大进度值
    public static final int default_progress_max = 100;

    //设置进度值是否隐藏
    public static final int default_text_visible = 0;
    public static final int default_text_invisible = 1;


}
