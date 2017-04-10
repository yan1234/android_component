package com.yanling.android.dialog.popdialog.bottommenu;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yanling.android.dialog.popdialog.R;

import java.util.List;

/**
 * 定义菜单适配器
 * @author yanling
 * @date 2017-03-20
 */
public class MenuAdapter extends BaseAdapter{

    //定义上下文对象
    private Context mContext;
    //定义数据源
    private List<BottomMenuItem> list;

    public MenuAdapter(){

    }

    public MenuAdapter(Context context, List<BottomMenuItem> list){
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0: list.size();
    }

    @Override
    public Object getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            //加载布局
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.popdialog_bottom_menu_item, null);
            //初始化ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.tv = (TextView)convertView.findViewById(R.id.bottom_menu_item);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //设置tv的选项语
        viewHolder.tv.setText(list.get(position).getText());
        //设置背景
        //viewHolder.tv.setBackgroundResource(list.get(position).getResource_bg_selector());
        //根据不同的版本设置背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            viewHolder.tv.setBackground(list.get(position).getBg_stateListDrawable());
        }else{
            viewHolder.tv.setBackgroundDrawable(list.get(position).getBg_stateListDrawable());
        }
        //设置字体颜色
        viewHolder.tv.setTextColor(list.get(position).getText_colorStateList());

        return convertView;
    }

    public void setList(List<BottomMenuItem> list){
        this.list = list;
    }


    class ViewHolder{
        public TextView tv;
    }
}
