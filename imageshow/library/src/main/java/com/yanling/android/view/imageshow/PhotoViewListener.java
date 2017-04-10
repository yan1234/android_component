package com.yanling.android.view.imageshow;

/**
 * 定义PhotoView接口事件
 * @author yanling
 * @date 2016-08-11
 */
public interface PhotoViewListener {

    /**
     * 手势操作从右滑动到左边（一般应用场景为向右翻页）
     * @param photoView
     */
    void onSlideToLeft(PhotoView photoView);

    /**
     * 手势操作从左滑动到右边（一般应用场景为向左翻页）
     * @param photoView
     */
    void onSlideToRight(PhotoView photoView);
}
