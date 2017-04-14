package com.yanling.android.view.imageselect;

import java.util.List;

/**
 * 定义外部传递过来的参数配置
 * @author yanling
 * @date 2017-04-14
 */
public class Params {

    //定义bundle传递的key值
    public static final String KEY_COLUMN_NUM = "KEY_COLUMN_NUM";
    public static final String KEY_SELECT_TOTAL_NUM = "KEY_SELECT_TOTAL_NUM";
    public static final String KEY_SELECTED_ITEMS = "KEY_SELECTED_ITEMS";

    //定义默认的选择项的数量
    public static final int default_select_total_num = 5;

    /**
     * 可配置
     * GridView显示的列数,如果不指定，则采用默认列数
     */
    public int column_num;

    /**
     * 不可配置
     * GridView显示的列宽
     */
    public int column_width;

    /**
     * 可配置
     * 可以被选中的最多的项数,如果不指定，采用默认项数
     */
    public int select_total_num;

    /**
     * 可配置
     * 已经选中的图片的地址列表, 不指定则为空
     */
    public List<String> selected_items;


}
