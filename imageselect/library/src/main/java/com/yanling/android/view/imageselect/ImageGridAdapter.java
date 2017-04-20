package com.yanling.android.view.imageselect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片展示网格布局适配器
 * @author yanling
 * @date 2017-04-13
 */
public class ImageGridAdapter extends BaseAdapter{

    private static final String TAG = ImageGridAdapter.class.getSimpleName();

    //定义上下文
    private Context mContext;
    //定义展示的图片地址列表
    private List<String> images;
    //定义已经选中的图片列表
    private List<String> selectImgs;
    private int item_width;
    //定义等宽等高的布局设置
    private AbsListView.LayoutParams params;

    //定义item缓存map对象,String为每一个item待展示的图片地址
    private Map<String, View> viewCacheMap = new HashMap<>();

    //定义变量标志当前是否滑动(默认false页面加载时自动加载)
    private boolean isScroll = false;

    public ImageGridAdapter(Context context, List<String> images, List<String> selectImgs, int item_width){
        this.mContext = context;
        this.images = images;
        this.selectImgs = selectImgs;
        this.item_width = item_width;
        params = new AbsListView.LayoutParams(item_width, item_width);
    }

    public void setScrollState(boolean isScroll){
        this.isScroll = isScroll;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        //是否缓存了该item
        if (!viewCacheMap.containsKey(images.get(position)) || viewCacheMap.get(images.get(position)) == null){
            //没有缓存,加载
            viewHolder = new ViewHolder();
            //载入布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_imageselect, parent, false);
            viewHolder.layout = (RelativeLayout)convertView.findViewById(R.id.item_imageselect_layout);
            viewHolder.img = (ImageView)convertView.findViewById(R.id.item_imageselect_img);
            viewHolder.select_layout = (RelativeLayout)convertView.findViewById(R.id.item_imageselect_select_layout);
            viewHolder.select = (ImageView)convertView.findViewById(R.id.item_imageselect_select);
            //将item项缓存起来
            convertView.setTag(viewHolder);
            viewCacheMap.put(images.get(position), convertView);
            //注意要在这里设置,不然会造成第一个item失效
            //控制每一个item的大小(等宽等高显示)
            viewHolder.layout.setLayoutParams(params);
        }else{
            convertView = viewCacheMap.get(images.get(position));
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //当没有滑动时加载图片
        if (!isScroll){
            //显示图片(通过Glide加载图片)
            Glide.with(mContext).load(images.get(position))
                    .crossFade().into(viewHolder.img);
            viewHolder.img.setTag(R.id.image_tag, null);
        }else{
            //设定标记将要加载(将url地址放入tag中)
            viewHolder.img.setTag(R.id.image_tag, images.get(position));
        }
        //设定默认的图片选中情况
        if (selectImgs != null && selectImgs.contains(images.get(position))){
            //执行选中的情况
            viewHolder.select_layout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_transparent_three));
            viewHolder.select.setImageResource(R.drawable.selected);
        }else{
            //载入未被选中的情况
            viewHolder.select_layout.setBackgroundColor(mContext.getResources().getColor(R.color.bg_transparent_two));
            viewHolder.select.setImageResource(R.drawable.unselected);
        }

        return convertView;
    }


    @Override
    public int getCount() {
        return images == null ? 0 : images.size();
    }

    @Override
    public Object getItem(int position) {
        return images == null ? null : images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }

    public List<String> getSelectImgs() {
        return selectImgs;
    }

    public void setSelectImgs(List<String> selectImgs) {
        this.selectImgs = selectImgs;
    }

    class ViewHolder{
        //背景布局
        public RelativeLayout layout;
        //图片
        public ImageView img;
        //选择框布局
        public RelativeLayout select_layout;
        //选择框
        public ImageView select;
    }
}
