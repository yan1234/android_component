package com.yanling.android.view.slideshow;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 轮播图Fragment
 * @author yanling
 * @date 2018-12-13
 */
public class SlideShowFragment extends Fragment {

    //定义宿主Activity对象
    private Activity mActivity;
    //定义滑动Pager对象
    private ViewPager mViewPager;
    //定义底部的标识容器
    private LinearLayout layout_tips;
    //定义待展示的图片列表对象
    private Object[] sources;
    //定义选中和未选中状态的drawable
    private Drawable selectedDrawable, unSelectedDrawable;
    //定义对象保存当前处在页面位置
    private int currentPosition = 0;


    public void setImageSources(int[] drawableIds){
        if (drawableIds != null && drawableIds.length > 0){
            this.sources = new Integer[drawableIds.length];
            for (int i = 0; i < sources.length; i++){
                sources[i] = drawableIds[i];
            }
        }

    }

    public void setImageSources(Bitmap[] bitmaps){
        if (bitmaps != null && bitmaps.length > 0){
            this.sources = new Bitmap[bitmaps.length];
            for (int i = 0; i < sources.length; i++){
                sources[i] = bitmaps[i];
            }
        }
    }

    /**
     * 设置选中和未选中的Drawable对象
     * @param selectedDrawable
     * @param unSelectedDrawable
     */
    public void setTipDrawable(Drawable selectedDrawable, Drawable unSelectedDrawable){
        this.selectedDrawable = selectedDrawable;
        this.unSelectedDrawable = unSelectedDrawable;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        //初始化view
        initView(view);
        return view;
    }

    /**
     * 初始化view
     * @param parentView
     */
    private void initView(View parentView){
        //加载子view
        mViewPager = (ViewPager) parentView.findViewById(R.id.slideshow_viewpager);
        layout_tips = (LinearLayout)parentView.findViewById(R.id.slideshow_tips);

        //设置适配器
        mViewPager.setAdapter(new ImagePagerAdapter());
        initTips();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //保存当前位置
                currentPosition = position;
                //设置指定序列tip为选中状态，其他的为未选中
                setTipStatus();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 根据资源数量添加相应的tip
     */
    private void initTips(){
        for (int i = 0; i < sources.length; i++){
            //构造tip容器
            ImageView imageView = new ImageView(layout_tips.getContext());
            //默认为未选中状态
            imageView.setImageDrawable(unSelectedDrawable);
            //添加到父容器中
            layout_tips.addView(imageView);

        }
    }

    /**
     * 设置标识的选中状态
     */
    private void setTipStatus(){
        //遍历所有标识
        for (int i = 0 ; i < sources.length; i++){
            if (i == currentPosition){
                //设置当前选中状态
                ((ImageView)layout_tips.getChildAt(i)).setImageDrawable(selectedDrawable);
            }else{
                //设置未选中状态
                ((ImageView)layout_tips.getChildAt(i)).setImageDrawable(unSelectedDrawable);
            }
        }
    }

    /**
     * 页面适配器
     */
    public class ImagePagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return sources == null ? 0 : sources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //构造ImageView对象，并添加到容器中
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //设置资源
            //动态判断数据来源
            if (sources[position] instanceof Bitmap){
                imageView.setImageBitmap((Bitmap) sources[position]);
                Bitmap bitmap;
                //bitmap.compress()
            }else if (sources[position] instanceof Integer){

                imageView.setImageResource((int) sources[position]);
            }
            container.addView(imageView);
            return imageView;
        }
    }
}
