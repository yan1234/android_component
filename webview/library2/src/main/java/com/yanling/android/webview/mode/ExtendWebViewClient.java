package com.yanling.android.webview.mode;

import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yanling.android.webview.AbstractJSCall;
import com.yanling.android.webview.ExtendException;
import com.yanling.android.webview.ExtendJSCallManager;
import com.yanling.android.webview.data.ExtendJSURL;
import com.yanling.android.webview.data.JSCallEntity;

/**
 * 扩展的WebViewClient
 * @author yanling
 * @date 2018-09-18
 */
public class ExtendWebViewClient extends WebViewClient {

    private static final String TAG = ExtendWebViewClient.class.getSimpleName();

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        JSCallEntity entity = new JSCallEntity();
        try {
            //获取传递的内容数据
            String apiKey = ExtendJSURL.parseURL(url, entity);
            //实例化JSCall
            AbstractJSCall jsCall = ExtendJSCallManager.getInstance().newJSCall(apiKey);
            //执行Native接口方法并返回数据
            jsCall.jsCall(entity);
            //返回拦截处理
            return true;
        } catch (ExtendException e) {
            e.printStackTrace();
            ExtendJSCallManager.log(ExtendJSCallManager.LogLevel.ERROR, TAG, e.getMessage());
            //判断不是url协议格式异常，则也拦截处理
            if (!e.getErrMsg().equals(ExtendException.CODE_URL_DATA_WRONG)){
                return true;
            }
        }
        //默认返回false,表示继续当前请求
        return super.shouldOverrideUrlLoading(view, url);
    }

}
