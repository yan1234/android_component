package com.yanling.android.webview;

import android.webkit.ValueCallback;

/**
 * js端调用native回调
 * @author yanling
 * @date 2016-03-06
 */
public interface JSCallNativeCallback {

    /**
     * 处理js调用native回调
     * @param data，js端返回的数据
     * @param callback，native给js端返回处理数据
     */
    void onNativeCallback(String data, ValueCallback<String> callback);

    /**
     * 错误处理
     * @param errMsg
     */
    void onError(String errMsg);
}
