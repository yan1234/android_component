package com.yanling.android.dialog.popdialog;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.yanling.android.dialog.popdialog.bottommenu.BottomMenuDialog;

/**
 * 创建测试用的Activity，主要用于单元测试
 * @author yanling
 * @date 2017-03-20
 */
public class TestActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ((Button)findViewById(R.id.activity_test_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行弹框操作
                onShowBottomMenu();
            }
        });
    }


    //定义底部弹框对象
    public BottomMenuDialog bottomMenuDialog;

    /**
     * 展示底部弹框操作
     */
    public void onShowBottomMenu(){
        bottomMenuDialog = new BottomMenuDialog();
        final String[] array = {"选项1", "选项2"};
        bottomMenuDialog.setMenuItems(TestActivity.this, array);
        bottomMenuDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(TestActivity.this, array[position], Toast.LENGTH_SHORT).show();
            }
        });
        bottomMenuDialog.show();
        Log.d("TestActivity", "test");
    }

}
