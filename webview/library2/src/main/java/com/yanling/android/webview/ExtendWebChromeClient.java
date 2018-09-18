package com.yanling.android.webview;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 扩展的WebChromeClient
 * @author yanling
 * @date 2018-09-18
 */
public class ExtendWebChromeClient extends WebChromeClient {

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        //判断是否是JS端调用
        if (message.startsWith("js call")){
            //返回结果数据
            result.confirm("");
            //返回true拦截处理
            return true;
        }
        //默认返回false,不进行任何处理
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
