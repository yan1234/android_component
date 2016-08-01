package com.yanling.android.view.progress;

import android.content.Context;

/**
 * 工具集
 * @author yanling
 * @date 2016-08-01
 */
public class Utils {

    /**
     * 将dp转化为px像素
     * @param context，上下文
     * @param dp，dp值
     * @return，px值
     */
    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        //便于四舍五入
        return (int)(dp * scale + 0.5f);
    }

    /**
     * 将sp转化为px
     * @param context, 上下文
     * @param sp，sp值
     * @return，px值
     */
    public static int sp2px(Context context, float sp) {
        float scale = context.getResources().getDisplayMetrics().scaledDensity;
        //便于四舍五入
        return (int)(sp * scale + 0.5f);
    }
}
