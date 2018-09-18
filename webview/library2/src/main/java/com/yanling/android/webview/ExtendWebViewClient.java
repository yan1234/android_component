package com.yanling.android.webview;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 扩展的WebViewClient
 * @author yanling
 * @date 2018-09-18
 */
public class ExtendWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //判断是否是JS端调用Native
        if (url.startsWith("js call")){
            //返回true表示取消当前请求
            return true;
        }
        //默认返回false,表示继续当前请求
        return super.shouldOverrideUrlLoading(view, url);
    }


}
