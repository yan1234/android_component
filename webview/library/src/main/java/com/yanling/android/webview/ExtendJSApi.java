package com.yanling.android.webview;


import android.os.Handler;
import android.webkit.JavascriptInterface;

import java.lang.reflect.InvocationTargetException;

/**
 * 扩展的js调用java的api类
 * 该类主要包括2部分：
 * 1、自定义的js调用java的方法，需要动态的注册
 * 2、常用的js调用java方法，包括拨打电话，相机等系统功能
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendJSApi {

    //定义js端调用的api名称，调用方式window.API_NAME.xxx
    public static final String API_NAME = "extendJSApi";

    //定义WebViewClient对象
    private ExtendWebViewClient webViewClient;

    public ExtendJSApi(ExtendWebViewClient webViewClient){
        this.webViewClient = webViewClient;
    }

    /**
     * 对外暴露的统一的js调用java的接口,调用方式为window.API_NAME.callNativeApi(methodName, data, callback);
     * @param methodName，调用的操作名
     * @param data，传递的数据
     * @param callback, 调用native后获取返回数据的回调方法
     */
    @JavascriptInterface
    public void callNativeApi(final String methodName, final String data, final String callback){
        //根据方法名获取要调用的接口方法
        webViewClient.handlerJSCall(methodName, data, callback);

        try {
            this.getClass().getMethod(methodName).invoke(this, data);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
