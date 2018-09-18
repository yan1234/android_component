package com.yanling.android.webview;

import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * 工具类
 * @author yanling
 * @date 2018-09-18
 */
public class Utils {

    /**
     * Native执行JS端function
     * @param webView WebView对象
     * @param callbackName JS接口名，该接口必须确保在window作用域下
     * @param valueCallback 4.4以上可以获取JS端执行的返回值
     * @param params 传递给JS端的可变长度参数列表
     */
    public static void executeJS(WebView webView, String callbackName, ValueCallback<String> valueCallback, String... params){
        //拼接待调用的js串
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:")
                .append(callbackName)
                .append("(");
        //拼接中间的参数
        if (params != null && params.length > 0){
            //拼接第一个参数
            sb.append("'").append(params[0]).append("'");
            for (int i = 1; i < params.length; i++){
                sb.append(",")
                        .append("'").append(params[i]).append("'");
            }
        }
        //最后拼接结束的反括号
        sb.append(")");
        //Android 4.4以下使用loadUrl
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            webView.loadUrl(sb.toString());
        }else{
            webView.evaluateJavascript(sb.toString(), valueCallback);
        }
    }

}
