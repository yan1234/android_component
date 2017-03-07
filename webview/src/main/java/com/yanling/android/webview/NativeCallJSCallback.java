package com.yanling.android.webview;

/**
 * 定义native端调用js的返回值毁掉接口
 * @author yanling
 * @date 2017-03-06
 */
public interface NativeCallJSCallback {

    /**
     * js端数据返回回调方法
     * @param data，返回的数据
     */
    void onJSCallback(String data);

    /**
     * 调用js端出错回调方法
     * @param errMsg
     */
    void onError(String errMsg);

}
