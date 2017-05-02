package com.yanling.android.view.indexview.demo;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanling.android.view.indexview.IndexViewFragment;
import com.yanling.android.view.indexview.RecyclerContentAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试demo
 */
public class MainActivity extends Activity{

    //定义数据源
    List<Data> data;

    MyAdapter adapter;

    IndexViewFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        fragment = new IndexViewFragment();
        adapter = new MyAdapter();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment, fragment);
        transaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fragment.getRv_content().setLayoutManager(new LinearLayoutManager(MainActivity.this));
        fragment.getRv_content().setAdapter(adapter);
    }

    private void initData(){
        int count = 3;
        data = new ArrayList<Data>();
        for (int i = 0 ; i < IndexViewFragment.index_list.length; i ++){
            Data data1 = new Data(IndexViewFragment.index_list[i]+"", (count * i + 1) + "");
            Data data2 = new Data(IndexViewFragment.index_list[i]+"", ""+(count * i + 2));
            Data data3 = new Data(IndexViewFragment.index_list[i]+"", ""+(count * i + 3));

            data.add(data1);
            data.add(data2);
            data.add(data3);
        }

    }

    class MyAdapter extends RecyclerContentAdapter{


        @Override
        public int getItemViewType(int position) {
            if (position % 4 == 0){
                return ITEM_TYPE_INDEX;
            }else{
                return ITEM_TYPE_CONTENT;
            }
        }


        @Override
        public RecyclerView.ViewHolder createIndexHolder(ViewGroup parent) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.layout_index, parent, false);
            return new IndexHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder createContentHolder(ViewGroup parent) {
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.layout_content, parent, false);
            return new ContentHolder(view);
        }

        @Override
        public void bindIndexHolder(RecyclerView.ViewHolder holder, int position) {

            ((IndexHolder)holder).tv.setText("" + IndexViewFragment.index_list[getRealPosition(position)]);
        }

        @Override
        public void bindContentHolder(RecyclerView.ViewHolder holder, int position) {

            ((ContentHolder)holder).tv.setText(data.get(getRealPosition(position)).text);
        }

        @Override
        public int getIndexCount() {
            return IndexViewFragment.index_list.length;
        }

        @Override
        public int getContentCount() {
            return data.size();
        }
    }

    class IndexHolder extends RecyclerView.ViewHolder{

        public TextView tv;

        public IndexHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.layout_index_tv);
        }
    }

    class ContentHolder extends RecyclerView.ViewHolder{

        public TextView tv;

        public ContentHolder(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.layout_content_tv);
        }
    }

    class Data{
        public String index;
        public String text;

        public Data(String index, String text){
            this.index = index;
            this.text = text;
        }
    }
}
