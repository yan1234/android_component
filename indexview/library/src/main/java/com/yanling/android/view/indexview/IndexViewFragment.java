package com.yanling.android.view.indexview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 索引界面的Fragment
 * @author yanling
 * @date 2017-04-26
 */
public class IndexViewFragment extends Fragment {

    private static final String TAG = IndexViewFragment.class.getSimpleName();

    //定义上下文对象
    private Context mContext;

    //定义整个界面对象
    private View view;

    //定义主内容区域和索引区域listview
    private RecyclerView rv_content;
    private ListView lv_index;

    //定义中心的索引预览
    private TextView tv_preview;

    //定义索引列表的适配器
    private IndexAdapter indexAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //获取activity对象
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_indexview, container, false);
        //初始化view
        initView();
        //初始化事件
        initEvent();
        return view;
    }

    /**
     * 初始化界面布局
     */
    private void initView(){
        rv_content = (RecyclerView) view.findViewById(R.id.fragment_rv_content);
        lv_index = (ListView)view.findViewById(R.id.fragment_lv_index);
        tv_preview = (TextView)view.findViewById(R.id.fragment_tv_preview);

        //绑定适配器
        lv_index.setAdapter(indexAdapter = new IndexAdapter());
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        //给索引列表添加滑动监听事件
        lv_index.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //按下时改变当前内容列表
                    case MotionEvent.ACTION_MOVE:
                        //按下滑动时动态的改变当前内容列表
                        //计算当前处于listview的第几个位置
                        int position = ((ListView)v).pointToPosition((int)event.getX(), (int)event.getY());
                        if (position != -1){
                            onIndexChange(position);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //松开时处理相应的事件
                        tv_preview.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });
    }

    /**
     * 索引改变时执行操作
     * @param position，当前索引位置
     */
    private void onIndexChange(int position){
        //设置中心预览界面的值
        tv_preview.setText("" + index_list[position]);
        tv_preview.setVisibility(View.VISIBLE);
        //内容区域滑动到索引指定的位置
        //根据索引列表计算内容列表的位置
        int rv_position = ((RecyclerContentAdapter)rv_content.getAdapter())
                .getPosition(position, RecyclerContentAdapter.ITEM_TYPE_INDEX);
        rv_content.smoothScrollToPosition(rv_position);
    }

    //定义索引列表, 首位是'↑',表示回到顶部
    private char to_top = '↑';
    public static char[] index_list = {'*', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '#'
    };

    /**
     * 定义索引列表的适配器
     */
    class IndexAdapter extends BaseAdapter{

        //Item高度
        private static final int ITEM_HEIGHT = 50;

        public IndexAdapter(){

        }

        @Override
        public int getCount() {
            return index_list.length;
        }

        @Override
        public Object getItem(int position) {
            return index_list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //加载布局数据
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_lv_index, null);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ITEM_HEIGHT
            );
            convertView.setLayoutParams(params);
            TextView tv = (TextView)convertView.findViewById(R.id.item_list_index_tv);
            tv.setText("" + index_list[position]);
            return convertView;
        }
    }

    public RecyclerView getRv_content() {
        return rv_content;
    }
}
