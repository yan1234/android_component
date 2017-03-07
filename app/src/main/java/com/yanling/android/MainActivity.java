package com.yanling.android;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Toast;

import com.yanling.android.webview.ExtendException;
import com.yanling.android.webview.ExtendURL;
import com.yanling.android.webview.ExtendWebView;
import com.yanling.android.webview.ExtendWebViewClient;
import com.yanling.android.webview.JSCallNativeCallback;
import com.yanling.android.webview.NativeCallJSCallback;

import java.nio.charset.Charset;

public class MainActivity extends Activity {

    private ExtendWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (ExtendWebView)findViewById(R.id.webview);

        init();

    }

    /**
     * 对webview进行初始化
     */
    private void init(){
        //首先设置协议格式
        ExtendURL extendURL = new ExtendURL();
        extendURL.setCharset(Charset.forName("UTF-8"));
        extendURL.setPrefix_jscallnative("xx://native?");
        extendURL.setPrefix_nativecalljs("yy://js?");

        //初始化webviewClient
        ExtendWebViewClient webViewClient = new ExtendWebViewClient(webView, extendURL);

        webView.setWebChromeClient(new WebChromeClient());
        //设置webview
        webView.setWebViewClient(webViewClient);

        //载入页面
        webView.loadUrl("file:///android_asset/www/index.html");

        webView.getWebViewClient().registerJSCallNative("test", new JSCallNativeCallback() {
            @Override
            public void onNativeCallback(String data, ValueCallback<String> callback) {

                Log.d("MainActivity", ">>>>"+data);

                Toast.makeText(MainActivity.this, ">>>" + data, Toast.LENGTH_SHORT).show();

                //给js回传数据
                callback.onReceiveValue("this is android");
            }

            @Override
            public void onError(String errMsg) {

            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.getWebViewClient().nativeCallJS("test", "this is android", new NativeCallJSCallback() {
                        @Override
                        public void onJSCallback(String data) {
                            Log.d("MainActivity", data);
                        }

                        @Override
                        public void onError(String errMsg) {

                        }
                    });
                } catch (ExtendException e) {
                    e.printStackTrace();
                }
            }
        }, 5000);

    }

}
