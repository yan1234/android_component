package com.yanling.android.webview;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 自定义扩展的JSCall管理器,负责整个JS调用Native的调度控制
 * @author yanling
 * @date 2018-09-19
 */
public final class ExtendJSCallManager {

    private static final String TAG = ExtendJSCallManager.class.getSimpleName();

    //定义三种策略模式常量
    //通过addJavascriptInterface接口注入对象模式
    public static final String MODE_JS_OBJECT = "MODE_JS_OBJECT";
    //WebViewClient 的shouldOverrideUrlLoading 拦截url处理模式，注意，该模式下同步无法获取返回值
    public static final String MODE_INTERCEPT_URL = "MODE_INTERCEPT_URL";
    //WebChromeClient 的onJsPrompt 拦截prompt模式
    public static final String MODE_INTERCEPT_PROMPT = "MODE_INTERCEPT_PROMPT";

    //定义JS端Callbacks回调对象名字空间,成功回调和失败回调方法
    private static final String JS_CALLBACK_NAME = "extendJSCallbacks";
    private static final String JS_CALLBACK_SUCCESS = "callbackSuccess";
    private static final String JS_CALLBACK_ERROR = "callbackError";

    //定义JSApi管理器对象
    private static ExtendJSCallManager mInstance;
    //定义Map保存待注册的Native接口
    private Map<String, String> apiMap = new HashMap<>();
    //定义弱引用缓存Class对象
    private WeakHashMap<String, Class> apiMapClass = new WeakHashMap<>();
    //定义主线程的handler对象用于线程切换操作
    private Handler handler = new Handler(Looper.getMainLooper());
    //定义变量保存JSCall的模式，默认采用MODE_INTERCEPT_PROMPT
    private String jsCallMode = MODE_INTERCEPT_PROMPT;
    //定义变量保存WebView对象
    private WebView webView;
    //定义是否开启日志记录（默认开启）
    private static boolean isLog = true;

    /**
     * 单例模式初始化管理器对象
     * @return
     */
    public static synchronized ExtendJSCallManager getInstance(){
        if (mInstance == null){
            mInstance = new ExtendJSCallManager();
        }
        return mInstance;
    }

    private ExtendJSCallManager(){}

    /**
     * 初始化设置请求模式
     * @param jsCallMode
     * @return
     */
    public ExtendJSCallManager init(String jsCallMode){
        this.jsCallMode = jsCallMode;
        return this;
    }

    /**
     * 设置日志开关，默认开启
     * @param isLog
     */
    public void setLog(boolean isLog){
        ExtendJSCallManager.isLog = isLog;
    }


    /**
     * 注入WebView对象
     * @param webView
     */
    public void injectWebView(WebView webView){
        this.webView = webView;
    }


    /**
     * 注册Native端接口
     * @param apiKey，接口对应的apiKey值，唯一性
     * @param apiClassName，接口对应的ClassName，用于后面通过反射进行方法调用
     */
    public void register(String apiKey, String apiClassName){
        if (apiMap == null){
            apiMap = new HashMap<>();
        }
        apiMap.put(apiKey, apiClassName);
    }

    /**
     * 批量注册Native端接口
     * @param map
     */
    public void register(Map<String, String> map){
        if (apiMap == null){
            apiMap = new HashMap<>();
        }
        apiMap.putAll(map);
    }

    /**
     * 注销Native端接口
     * @param apiKey
     */
    public void unRegister(String apiKey){
        if (apiMap != null){
            apiMap.remove(apiKey);
        }
    }

    /**
     * 注销Native端接口
     * @param clazz
     */
    public void unRegister(Class clazz){
        if (apiMap != null){
            apiMap.remove(clazz);
        }
    }

    /**
     * 批量注销Native端接口
     * @param map
     */
    public void unRegister(Map<String, String> map){
        if (apiMap != null){
            //遍历Map并删除
            for (String key : map.keySet()){
                apiMap.remove(key);
            }
        }
    }

    /**
     * 注销所有Native端接口
     */
    public void clearAll(){
        if (apiMap != null){
            apiMap.clear();
        }
    }

    /**
     * 执行主线程回调操作
     * @param callback JS端回调的callback接口
     * @param result 返回结果（成功或失败数据）
     * @param status true: 返回成功；false:返回失败
     * @param isKeep "true": 保持住异步时保持住callback回调；"false"：调用结束即销毁JS 端Callback回调对象
     */
    public void jsCallback(final String callback, final String result, final boolean status, final String isKeep){
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //根据状态回调指定的方法（success/error）
                String tmp = status ? JS_CALLBACK_SUCCESS : JS_CALLBACK_ERROR;
                executeJS(webView, JS_CALLBACK_NAME + "." + callback + "." + tmp, null, result, isKeep);
            }
        });
    }

    /**
     * 在主线程中执行Runnable
     * @param runnable
     */
    private void runOnUIThread(Runnable runnable){
        //检验handler是否为空
        if (handler == null){
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(runnable);
    }

    /**
     * 实例化JSCall对象
     * @param apiKey，JSCall类对应key值
     * @return，返回JSCall对象
     * @throws ExtendException
     */
    public AbstractJSCall newJSCall(String apiKey) throws ExtendException{
        //通过apiKey找到对象的ClassName
        String className = apiMap.get(apiKey);
        if (className == null){
            //抛出接口不存在异常
            throw new ExtendException(ExtendException.CODE_NATIVE_API_NOT_FOUND, ExtendException.MSG_NATIVE_API_NOT_FOUND);
        }
        //接着从缓存中取该Class
        Class clazz = apiMapClass.get(apiKey);
        if (clazz == null){
            //缓存为空，则加载该类
            try {
                clazz = Class.forName(className);
                //放入缓存中
                apiMapClass.put(apiKey, clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                log(LogLevel.ERROR, TAG, e.getMessage());
                //抛出接口不存在异常
                throw new ExtendException(ExtendException.CODE_NATIVE_API_NOT_FOUND, ExtendException.MSG_NATIVE_API_NOT_FOUND);
            }
        }
        //实例化对象并进行处理
        if (clazz != null && clazz.getSuperclass().equals(AbstractJSCall.class)){
            //通过类实例化该对象
            try {
                AbstractJSCall jsCall = (AbstractJSCall)clazz.newInstance();
                return jsCall;
            } catch (InstantiationException e) {
                e.printStackTrace();
                log(LogLevel.ERROR, TAG, e.getMessage());
                throw new ExtendException(ExtendException.CODE_INTERNAL_EXCEPTION, ExtendException.MSG_INTERNAL_EXCEPTION);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                log(LogLevel.ERROR, TAG, e.getMessage());
                throw new ExtendException(ExtendException.CODE_INTERNAL_EXCEPTION, ExtendException.MSG_INTERNAL_EXCEPTION);
            }
        }else{
            throw new ExtendException(ExtendException.CODE_INTERNAL_EXCEPTION, ExtendException.MSG_INTERNAL_EXCEPTION);
        }
    }

    /**
     * Native执行JS端function
     * @param webView WebView对象
     * @param callbackName JS接口名，该接口必须确保在window作用域下
     * @param valueCallback 4.4以上可以获取JS端执行的返回值
     * @param params 传递给JS端的可变长度参数列表
     */
    public static void executeJS(WebView webView, String callbackName, ValueCallback<String> valueCallback, String... params){
        //拼接待调用的js串
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:")
                .append(callbackName)
                .append("(");
        //拼接中间的参数
        if (params != null && params.length > 0){
            //拼接第一个参数
            sb.append("'").append(params[0]).append("'");
            for (int i = 1; i < params.length; i++){
                sb.append(",")
                        .append("'").append(params[i]).append("'");
            }
        }
        //最后拼接结束的反括号
        sb.append(")");
        //日志记录
        log(LogLevel.DEBUG, TAG, "执行js方法："  + sb.toString());
        //Android 4.4以下使用loadUrl
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            webView.loadUrl(sb.toString());
        }else{
            webView.evaluateJavascript(sb.toString(), valueCallback);
        }
    }

    /**
     * 自定义日志记录，可以通过开关控制
     * @param logLevel
     * @param tag
     * @param msg
     */
    public static void log(LogLevel logLevel, String tag, String msg){
        //判断日志记录是否开启
        if (isLog){
            switch (logLevel){
                case ASSERT:
                    break;
                case DEBUG:
                    Log.d(tag, msg);
                    break;
                case ERROR:
                    Log.e(tag, msg);
                    break;
                case INFO:
                    Log.e(tag, msg);
                    break;
                case VERBOSE:
                    Log.i(tag, msg);
                    break;
                case WARN:
                    Log.w(tag, msg);
                    break;
            }
        }
    }

    //定义日志记录级别的枚举对象
    public enum LogLevel{
        ASSERT, DEBUG, ERROR, INFO, VERBOSE, WARN
    }

}
