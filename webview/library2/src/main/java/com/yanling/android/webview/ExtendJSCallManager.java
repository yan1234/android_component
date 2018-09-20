package com.yanling.android.webview;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;

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

    //定义JSApi管理器对象
    private static ExtendJSCallManager mInstance;
    //定义Map保存待注册的Native接口
    private Map<String, Class> apiMap = new HashMap<>();
    //定义主线程的handler对象用于线程切换操作
    private Handler handler = new Handler(Looper.getMainLooper());
    //定义变量保存JSCall的模式，默认采用MODE_INTERCEPT_PROMPT
    private String jsCallMode = MODE_INTERCEPT_PROMPT;


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
     * 注册Native端接口
     * @param apiKey，接口对应的apiKey值，唯一性
     * @param apiClass，接口对应的Class对象，用于后面通过反射进行方法调用
     */
    public void register(String apiKey, Class apiClass){
        if (apiMap == null){
            apiMap = new HashMap<>();
        }
        apiMap.put(apiKey, apiClass);
    }

    /**
     * 批量注册Native端接口
     * @param map
     */
    public void register(Map<String, Class> map){
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
    public void unRegister(Map<String, Class> map){
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
     */
    public void jsCallback(final String callback, final String result, final boolean status){
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (status){
                    //处理成功回调
                }else{
                    //处理失败回调
                }
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
        //通过apiKey找到对象的实例对象
        Class clazz = apiMap.get(apiKey);
        if (clazz != null && clazz.getSuperclass().equals(AbstractJSCall.class)){
            //通过类实例化该对象
            try {
                AbstractJSCall jsCall = (AbstractJSCall)clazz.newInstance();
                return jsCall;
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new ExtendException(ExtendException.CODE_NATIVE_API_NOT_FOUND, e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ExtendException(ExtendException.CODE_NATIVE_API_NOT_FOUND, e.getMessage());
            }
        }else{
            throw new ExtendException(ExtendException.CODE_NATIVE_API_NOT_FOUND, "Native Api Not Found");
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
        //Android 4.4以下使用loadUrl
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            webView.loadUrl(sb.toString());
        }else{
            webView.evaluateJavascript(sb.toString(), valueCallback);
        }
    }

}
