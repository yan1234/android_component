package com.yanling.android.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * 扩展android 自带的webview控件
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendWebView extends WebView{

    //定义WebViewClient对象
    private ExtendWebViewClient webViewClient;

    public ExtendWebView(Context context) {
        super(context);
        init();
    }

    public ExtendWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExtendWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //设置不能为滑动
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        //设置javascript为开启
        this.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        //设置自定以的WebViewClient
        this.webViewClient = (ExtendWebViewClient) client;
        //设置js调用java接口
        this.addJavascriptInterface(new ExtendJSApi(webViewClient), ExtendJSApi.API_NAME);
    }

    public ExtendWebViewClient getWebViewClient() {
        return webViewClient;
    }
}
