package com.yanling.android.webview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 扩展android 自带的WebViewClient
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendWebViewClient extends WebViewClient{


    private final static String TAG = ExtendWebViewClient.class.getSimpleName();

    //定义webview对象
    private ExtendWebView webView;
    //定义扩展的url对象
    private ExtendURL extendURL;

    //定义map保存js端调用java端的接口方法
    private Map<String, JSCallNativeCallback> jsCallNativeMap = new HashMap<>();
    //定义map保存java端调用js端的接口方法
    private Map<String, NativeCallJSCallback> nativeCallJsMap = new HashMap<>();

    //定义标志变量保存页面是否加载完成
    private boolean isPageFinished = false;

    public ExtendWebViewClient(ExtendWebView webView, ExtendURL extendURL){
        this.webView = webView;
        this.extendURL = extendURL;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //监听url的变化实现js调用native接口

        //解析url地址
        try {
            url = URLDecoder.decode(url, this.extendURL.getCharset().name());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // @TODO 异常处理
            return true;
        }

        Log.d(TAG, "url:" + url);

        //判断url地址协议是否是native调用js端，js端返回数据
        if (url.startsWith(this.extendURL.getPrefix_nativecalljs())){
            //解析数据
            try {
                String[] result = this.extendURL.parseNativeCallJS(url);
                //处理js端返回数据
                this.handlerJSReturn(result[0], result[1]);
            } catch (ExtendException e) {
                e.printStackTrace();
            }

            return true;
        }//判断url地址协议是否是js端调用native端动态注册的接口（等同于window.xxx.xxx调用）
        else if (url.startsWith(this.extendURL.getPrefix_jscallnative())){
            //解析协议
            String[] result = null;
            try {
                result = this.extendURL.parseJSCallNative(url);
                //处理js调用
                this.handlerJSCall(result[0], result[1], result[2]);
            } catch (ExtendException e) {
                e.printStackTrace();
                // @TODO 异常处理
            }

            return true;
        }else{
            //默认处理
            return super.shouldOverrideUrlLoading(view, url);
        }


    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        isPageFinished = false;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        isPageFinished = true;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    /**
     * 注册js调用native的回调接口
     * @param methodName，接口名
     * @param jsCallNativeCallback，回调
     */
    public void registerJSCallNative(String methodName, final JSCallNativeCallback jsCallNativeCallback){
        if (jsCallNativeMap == null){
            jsCallNativeMap = new HashMap<>();
        }
        //将注册的接口放入map中
        jsCallNativeMap.put(methodName, jsCallNativeCallback);
    }

    /**
     * 注销js调用native的回调接口
     * @param methodName，接口名
     */
    public void unRegisterJSCallNative(String methodName){
        if (jsCallNativeMap != null){
            jsCallNativeMap.remove(methodName);
        }
    }

    /**
     * 处理js端调用native事件
     * @param methodName，调用的方法名
     * @param data，传递的数据
     * @param callback, js调用native后获取返回数据的回调方法名，如果callback为null，表示不需要返回值
     */
    public void handlerJSCall(String methodName, final String data, final String callback){
        //根据接口名找到回调接口
        if (jsCallNativeMap != null){
            //获取回调接口
            JSCallNativeCallback jsCallNativeCallback = jsCallNativeMap.get(methodName);
            //调用回调方法,执行相应的java方法
            if (callback == null || "null".equals(callback)){
                //不需要返回值
                jsCallNativeCallback.onNativeCallback(data, null);
            }else{
                jsCallNativeCallback.onNativeCallback(data, new ValueCallback<String>(){

                    @Override
                    public void onReceiveValue(final String data) {

                        ((Activity)webView.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //native端处理结束后将数据返回给js端,这里不再需要js给native返回数据，所以设置回调为null
                                try {
                                    nativeCallJS(callback, data, null);
                                } catch (ExtendException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }

        }
    }

    /**
     * 处理js返回的数据
     * @param data,js端返回的数据
     * @param callbackId，调用js的回调id
     */
    public void handlerJSReturn(String data, String callbackId){
        if (nativeCallJsMap != null){
            //获取map中保存的回调信息
            NativeCallJSCallback nativeCallJSCallback = nativeCallJsMap.get(callbackId);
            if (nativeCallJSCallback != null){
                //返回js端的消息
                nativeCallJSCallback.onJSCallback(data);
                //移除该回调
                nativeCallJsMap.remove(callbackId);
            }
        }
    }

    /**
     * 调用js端的方法并获取返回值
     * @param methodName，js端的方法名
     * @param msg，发送给js方法的数据
     * @param nativeCallJsCallback，返回js端数据回调接口，如果为null，标示不需要返回值
     * @exception ExtendException
     */
    public void nativeCallJS(String methodName, String msg, final NativeCallJSCallback nativeCallJsCallback)
            throws ExtendException{
        //判断页面是否加载完毕
        if (!isPageFinished){
            throw new ExtendException("", "Page Not Finished");
        }

        //如果不是在主线程中调用，抛出异常
        if (Thread.currentThread() != Looper.getMainLooper().getThread()){
            throw new ExtendException(ExtendException.CODE_NOT_RUN_ON_MAIN_THREAD, "Not Run Oxn Main Thread");
        }
        //标示js端不需要获取返回值
        if (nativeCallJsCallback == null){
            //直接调用loadUrl方法
            this.webView.loadUrl("javascript:" + methodName + "('" + msg + "')");
        }else{
            //判断android版本是否是4.4以前
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){

                //生成callbackId
                String callbackId = "" + SystemClock.elapsedRealtime();
                //将当前回调保存到map对象中
                nativeCallJsMap.put(callbackId, nativeCallJsCallback);

                //封装传递给js端的参数
                String data = this.extendURL.packageNativeCallJSMsg(msg, callbackId);

                //调用老方法进行js调用
                this.webView.loadUrl("javascript:"+methodName + "('" + data + "')");

            }else{
                //调用4.4及以后可以获取js返回值的方式
                this.webView.evaluateJavascript("javascript:" + methodName + "('" + msg + "')",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String data) {
                                //获取js端返回的数据
                                nativeCallJsCallback.onJSCallback(data);
                            }
                        });
            }
        }

    }
}
