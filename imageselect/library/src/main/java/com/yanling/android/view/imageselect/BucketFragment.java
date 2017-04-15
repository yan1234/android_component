package com.yanling.android.view.imageselect;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定义目录展示的Fragment
 * @author yanling
 * @date 2017-04-15
 */
public class BucketFragment extends Fragment{

    private Context mContext;
    //定义目录列表布局
    private ListView listView;
    private ItemAdapter adapter;

    //定义目录列表
    private Map<String, List<String>> bucketMap;
    private List<String> bucketList;

    //当前选中的目录的名称
    private String checked_bucketName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //加载目录界面
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mContext = getActivity();
        listView = (ListView)view.findViewById(R.id.fragment_category_list);
        adapter = new ItemAdapter();
        listView.setAdapter(adapter);
        //设置item项点击事件监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //记录当前点击的目录名
                checked_bucketName = bucketList.get(position);
                //更新图片选择列表
                ((ImageSelectActivity)getActivity()).bucketItemClick(checked_bucketName);
                //更新listview布局
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    /**
     * 设置目录列表
     * @param bucketList
     * @param map
     */
    public void setBucketList(List<String> bucketList, Map<String, List<String>> map){
        this.bucketList = bucketList;
        this.bucketMap = map;
    }

    /**
     * 设置当前选中的目录
     * @param checked_bucketName
     */
    public void setChecked_bucketName(String checked_bucketName){
        this.checked_bucketName = checked_bucketName;
    }


    class ItemAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return bucketList.size();
        }

        @Override
        public Object getItem(int position) {
            return bucketList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null){
                //加载item布局
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_fragment_list, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.layout = (LinearLayout)convertView.findViewById(R.id.item_fragment_list_layout);
                viewHolder.img = (ImageView)convertView.findViewById(R.id.item_fragment_list_thumbnail);
                viewHolder.name = (TextView)convertView.findViewById(R.id.item_fragment_list_category);
                viewHolder.number = (TextView)convertView.findViewById(R.id.item_fragment_list_number);
                viewHolder.checked = (ImageView)convertView.findViewById(R.id.item_fragment_list_checked);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //设置目录的高度
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,100);
            //viewHolder.layout.setLayoutParams(params);
            //设置缩略图(当前目录的第一张图片)
            Glide.with(mContext).load(bucketMap.get(bucketList.get(position)).get(0))
                    .crossFade().into(viewHolder.img);
            //目录名称
            viewHolder.name.setText(bucketList.get(position));
            viewHolder.number.setText(bucketMap.get(bucketList.get(position)).size() + "张");
            //当前选中的目录
            if (bucketList.get(position).equals(checked_bucketName)){
                viewHolder.checked.setVisibility(View.VISIBLE);
            }else{
                viewHolder.checked.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class ViewHolder{
            //定义item布局
            public LinearLayout layout;
            //缩略图
            public ImageView img;
            //目录名
            public TextView name;
            //目录文件数量
            public TextView number;
            //是否是当前目录标志
            public ImageView checked;
        }
    }
}
