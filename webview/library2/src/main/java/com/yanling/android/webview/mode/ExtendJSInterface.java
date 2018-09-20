package com.yanling.android.webview.mode;

import android.webkit.JavascriptInterface;

import com.yanling.android.webview.AbstractJSCall;
import com.yanling.android.webview.ExtendException;
import com.yanling.android.webview.ExtendJSCallManager;
import com.yanling.android.webview.data.ExtendJSURL;
import com.yanling.android.webview.data.JSCallEntity;

/**
 * 自定义扩展的JavaScript Interface接口
 * @author yanling
 * @date 2018-09-19
 */
public final class ExtendJSInterface {

    private static final String TAG = ExtendJSInterface.class.getSimpleName();

    //定义js端调用的api名称，调用方式window.API_NAME.xxx
    public static final String API_NAME = "extendJSInterface";

    /**
     * 执行Native端接口并获取返回值，同时支持同步和异步执行，具体控制由isAsync控制
     * @param url 请求url协议格式数据
     * @return，返回执行情况
     */
    @JavascriptInterface
    public String execute(String url){
        if (ExtendJSURL.isJSCallURL(url)){
            JSCallEntity entity = new JSCallEntity();
            try {
                //获取传递的内容数据
                String apiKey = ExtendJSURL.parseURL(url, entity);
                //实例化JSCall
                AbstractJSCall jsCall = ExtendJSCallManager.getInstance().newJSCall(apiKey);
                //执行Native接口方法并返回数据
                return jsCall.jsCall(entity);
            } catch (ExtendException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
