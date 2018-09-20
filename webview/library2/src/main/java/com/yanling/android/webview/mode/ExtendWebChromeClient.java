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

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        //判断是否是JS端调用Native
        if (ExtendJSURL.isJSCallURL(url)){
            JSCallEntity entity = new JSCallEntity();
            try {
                //获取传递的内容数据
                String apiKey = ExtendJSURL.parseURL(url, entity);
                //实例化JSCall
                AbstractJSCall jsCall = ExtendJSCallManager.getInstance().newJSCall(apiKey);
                //执行Native接口方法并返回数据
                String resultData = jsCall.jsCall(entity);
                result.confirm(resultData);
            } catch (ExtendException e) {
                e.printStackTrace();
            }
            //返回true表示取消当前请求
            return true;
        }
        //默认返回false,不进行任何处理
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }
}
