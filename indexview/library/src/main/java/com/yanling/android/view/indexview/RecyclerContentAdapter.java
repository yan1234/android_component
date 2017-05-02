package com.yanling.android.view.indexview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 实现内容区域的RecyclerView的适配器
 * @author yanling
 * @date 2017-05-02
 */
public abstract class RecyclerContentAdapter extends RecyclerView.Adapter {

    //标示是索引的ViewHolder类型
    public static final int ITEM_TYPE_INDEX = 1;
    //标示是内容区域的ViewHolder类型
    public static final int ITEM_TYPE_CONTENT = 2;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE_INDEX){
            //返回索引的ViewHolder
            return createIndexHolder(parent);
        }else if (viewType == ITEM_TYPE_CONTENT){
            //返回内容的ViewHolder
            return createContentHolder(parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE_INDEX){
            //绑定索引数据
            bindIndexHolder(holder, position);
        }else if (getItemViewType(position) == ITEM_TYPE_CONTENT){
            //绑定内容数据
            bindContentHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return getIndexCount() + getContentCount();
    }

    /**
     * 计算出数据的实际位置
     * @param position，当前列表的位置
     * @return,返回实际的数据位置
     */
    public int getRealPosition(int position){
        //定义真实的位置
        int real_position = -1;
        //获取当前位置的type类型
        int viewType = getItemViewType(position);
        //为了便于计算效率,根据当前位置采用从前往后或从后往前的策略进行遍历
        if (position < getItemCount() / 2){
            //如果当前所处的位置在中心的前面, 则从前往后遍历
            for (int i = 0; i <= position; i++){
                //记录相同的数量
                if (viewType == getItemViewType(i)){
                    real_position ++;
                }
            }
        }else{
            //从后往前遍历
            //定义变量保存后面的数据的个数
            int out_count = 0;
            for (int i = getItemCount()-1; i > position; i--){
                //记录后面相同类型的数量
                if (viewType == getItemViewType(i)){
                    out_count ++;
                }
            }
            if (viewType == ITEM_TYPE_INDEX){
                //如果是计算索引的位置
                real_position = getIndexCount() - 1 - out_count;
            }else if (viewType == ITEM_TYPE_CONTENT){
                //计算内容的位置
                real_position = getContentCount() - 1 - out_count;
            }
        }

        return real_position;
    }

    /**
     * 通过数据的位置计算出列表的位置
     * @param data_position，数据位置
     * @param viewType，数据类型(ITEM_TYPE_INDEX, ITEM_TYPE_CONTENT)
     * @return,返回列表位置
     */
    public int getPosition(int data_position, int viewType){
        int position = 0;
        int count = 0;
        for (int i = 0; i < getItemCount() - 1; i++){
            if (getItemViewType(i) == viewType){
                count ++;
            }
            //判断读取到的个数是否时当前的序列数
            if (count == data_position + 1){
                position = i;
                break;
            }
        }
        return position;
    }

    public abstract int getItemViewType(int position);

    public abstract int getIndexCount();

    public abstract int getContentCount();

    public abstract RecyclerView.ViewHolder createIndexHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder createContentHolder(ViewGroup parent);

    public abstract void bindIndexHolder(RecyclerView.ViewHolder holder, int position);

    public abstract void bindContentHolder(RecyclerView.ViewHolder holder, int position);
}
