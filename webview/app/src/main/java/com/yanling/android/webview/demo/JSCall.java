package com.yanling.android.webview.demo;

import android.util.Log;

import com.yanling.android.webview.AbstractJSCall;

/**
 * 测试JSObj模式
 * @author yanling
 * @date 2018-09-27
 */
public class JSCall extends AbstractJSCall {

    @Override
    public String execute(String action, String data) {

        Log.d("JSCall", "同步执行: " + action + "   " + data);
        return "success";
    }

    @Override
    public void enqueue(String action, String data) {
        Log.d("JSCall", "异步执行: " + action + "   " + data);

        new Thread(new Runnable() {
            @Override
            public void run() {
                success("success");
            }
        }).start();
    }
}
