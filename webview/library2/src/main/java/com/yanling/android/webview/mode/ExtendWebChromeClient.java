package com.yanling.android.webview.mode;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.yanling.android.webview.AbstractJSCall;
import com.yanling.android.webview.ExtendException;
import com.yanling.android.webview.ExtendJSCallManager;
import com.yanling.android.webview.data.ExtendJSURL;
import com.yanling.android.webview.data.JSCallEntity;

/**
 * 扩展的WebChromeClient
 * @author yanling
 * @date 2018-09-18
 */
public class ExtendWebChromeClient extends WebChromeClient {

    private static final String TAG = ExtendWebChromeClient.class.getSimpleName();

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        JSCallEntity entity = new JSCallEntity();
        try {
            //获取传递的内容数据
            String apiKey = ExtendJSURL.parseURL(message, entity);
            //实例化JSCall
            AbstractJSCall jsCall = ExtendJSCallManager.getInstance().newJSCall(apiKey);
            //执行Native接口方法并返回数据
            String resultData = jsCall.jsCall(entity);
            result.confirm(ExtendJSURL.packageResult(resultData, true));
            //返回拦截处理
            return true;
        } catch (ExtendException e) {
            e.printStackTrace();
            ExtendJSCallManager.log(ExtendJSCallManager.LogLevel.ERROR, TAG, e.getMessage());
            //判断不是url协议格式异常，则也拦截处理
            if (!e.getErrMsg().equals(ExtendException.CODE_URL_DATA_WRONG)){
                //返回异常
                result.confirm(ExtendJSURL.packageResult(e.getMessage(), false));
                return true;
            }
        }
        //默认返回false,不进行任何处理
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
