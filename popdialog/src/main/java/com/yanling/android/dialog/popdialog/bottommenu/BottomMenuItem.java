package com.yanling.android.dialog.popdialog.bottommenu;

import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;

/**
 * 定义每一项弹框参数设置对象
 * 主要包括２块：
 * １、text标题
 * ２、颜色设置，分为２部分：
 *      １、item背景颜色(包括常态和按下状态)
 *      ２、item text文字字体颜色(包括常态和按下状态)
 *
 * @author yanling
 * @date 2017-03-20
 */
public class BottomMenuItem {

    //定义选项文字
    private String text;

    //定义选项背景颜色drawable selector
    private StateListDrawable bg_stateListDrawable;

    //定义选项文字的颜色
    private ColorStateList text_colorStateList;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public StateListDrawable getBg_stateListDrawable() {
        return bg_stateListDrawable;
    }

    public void setBg_stateListDrawable(StateListDrawable bg_stateListDrawable) {
        this.bg_stateListDrawable = bg_stateListDrawable;
    }

    public ColorStateList getText_colorStateList() {
        return text_colorStateList;
    }

    public void setText_colorStateList(ColorStateList text_colorStateList) {
        this.text_colorStateList = text_colorStateList;
    }
}
