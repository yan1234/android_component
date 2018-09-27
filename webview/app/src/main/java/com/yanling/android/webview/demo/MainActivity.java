package com.yanling.android.webview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.yanling.android.webview.ExtendJSCallManager;
import com.yanling.android.webview.mode.ExtendJSInterface;
import com.yanling.android.webview.mode.ExtendWebChromeClient;
import com.yanling.android.webview.mode.ExtendWebViewClient;

/**
 * 测试ExtendJSCall
 * @author yanling
 * @date 2018-09-27
 */
public class MainActivity extends Activity {


    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)this.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/www/TestExtendJSCall.html");

        //测试
        try {
            //testJSObj();
            //testJSUrl();
            testJSPrompt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试JSObj模式
     */
    private void testJSObj() throws Exception{
        //开启通道
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new ExtendJSInterface(), ExtendJSInterface.API_NAME);

        //注册API接口
        ExtendJSCallManager.getInstance()
                .init(ExtendJSCallManager.MODE_JS_OBJECT)
                .injectWebView(webView);
        //注册H5端API
        ExtendJSCallManager.getInstance().register("aaa", "com.yanling.android.webview.demo.JSObj");

    }

    /**
     * 测试JSUrl模式
     * @throws Exception
     */
    private void testJSUrl() throws Exception{
        //开启通道
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new ExtendWebViewClient());

        //注册API接口
        ExtendJSCallManager.getInstance()
                .init(ExtendJSCallManager.MODE_INTERCEPT_URL)
                .injectWebView(webView);
        //注册H5端API
        ExtendJSCallManager.getInstance().register("aaa", "com.yanling.android.webview.demo.JSCall");
    }

    /**
     * 测试JSPrompt模式
     * @throws Exception
     */
    private void testJSPrompt() throws Exception{
        //开启通道
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new ExtendWebChromeClient());

        //注册API接口
        ExtendJSCallManager.getInstance()
                .init(ExtendJSCallManager.MODE_INTERCEPT_PROMPT)
                .injectWebView(webView);
        //注册H5端API
        ExtendJSCallManager.getInstance().register("aaa", "com.yanling.android.webview.demo.JSCall");
    }
}
