package com.yanling.android.view.progress;

import java.io.Serializable;

/**
 * ProgressView基本样式配置实体
 * @author yanling
 * @date 2016-08-01
 */
public class ProgressViewParam implements Serializable{

    //加载的进度条的高度
    private int reached_bar_height;

    //未加载的进度条的高度
    private int unreached_bar_height;

    //加载进度条的颜色
    private int reached_bar_color;

    //未加载的进度条的颜色
    private int unreached_bar_color;

    //显示进度的字体大小
    private int text_size;

    //显示进度的字体颜色
    private int text_color;

    //显示进度值的偏移
    private int text_offset;

    //是否显示进度值（0:visibility,表示显示进度值，1:不显示）
    private int text_visibility;

    //显示进度信息后缀（一般为：%）
    private String text_suffix;

    //显示进度信息前缀
    private String text_prefix;

    //定义最大的进度值（一般为100)
    private int progress_max;

    //定义当前的进度值（默认初始为0）
    private int progress_current;

    public int getReached_bar_height() {
        return reached_bar_height;
    }

    public void setReached_bar_height(int reached_bar_height) {
        this.reached_bar_height = reached_bar_height;
    }

    public int getUnreached_bar_height() {
        return unreached_bar_height;
    }

    public void setUnreached_bar_height(int unreached_bar_height) {
        this.unreached_bar_height = unreached_bar_height;
    }

    public int getReached_bar_color() {
        return reached_bar_color;
    }

    public void setReached_bar_color(int reached_bar_color) {
        this.reached_bar_color = reached_bar_color;
    }

    public int getUnreached_bar_color() {
        return unreached_bar_color;
    }

    public void setUnreached_bar_color(int unreached_bar_color) {
        this.unreached_bar_color = unreached_bar_color;
    }

    public int getText_size() {
        return text_size;
    }

    public void setText_size(int text_size) {
        this.text_size = text_size;
    }

    public int getText_color() {
        return text_color;
    }

    public void setText_color(int text_color) {
        this.text_color = text_color;
    }

    public int getText_offset() {
        return text_offset;
    }

    public void setText_offset(int text_offset) {
        this.text_offset = text_offset;
    }

    public int getText_visibility() {
        return text_visibility;
    }

    public void setText_visibility(int text_visibility) {
        this.text_visibility = text_visibility;
    }

    public String getText_suffix() {
        return text_suffix;
    }

    public void setText_suffix(String text_suffix) {
        this.text_suffix = text_suffix;
    }

    public String getText_prefix() {
        return text_prefix;
    }

    public void setText_prefix(String text_prefix) {
        this.text_prefix = text_prefix;
    }

    public int getProgress_max() {
        return progress_max;
    }

    public void setProgress_max(int progress_max) {
        this.progress_max = progress_max;
    }

    public int getProgress_current() {
        return progress_current;
    }

    public void setProgress_current(int progress_current) {
        this.progress_current = progress_current;
    }
}
