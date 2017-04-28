package com.yanling.android.view.indexview.demo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanling.android.view.indexview.IndexViewFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试demo
 */
public class MainActivity extends Activity{

    //定义数据源
    Map<String, List<String>> map = new HashMap<String, List<String>>();

    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        IndexViewFragment fragment = new IndexViewFragment();
        adapter = new MyAdapter();
        fragment.setRecyclerAdapter(adapter);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment, fragment);
        transaction.commit();
    }

    private void initData(){
        int count = 0;
        for (int i = 0; i < IndexViewFragment.index_list.length; i++){
            List<String> list = new ArrayList<String>();

            list.add(""+count++);
            list.add(""+count++);
            list.add(""+count++);
            map.put(""+IndexViewFragment.index_list[i], list);
        }
    }

    class MyAdapter extends IndexViewFragment.RecyclerAdapter<MyAdapter.MyViewHolder>{
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(
                    LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.layout_content, parent, false)
            );
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MyViewHolder)holder).content_tv.setText(map.get(getKey()).get(position));
        }

        @Override
        public int getItemCount() {
            return map.get(getKey()).size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            public TextView content_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                content_tv = (TextView)itemView.findViewById(R.id.layout_content_tv);
            }
        }
    }
}
