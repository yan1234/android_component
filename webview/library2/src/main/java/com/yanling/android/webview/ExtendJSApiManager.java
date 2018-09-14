package com.yanling.android.webview;

import android.webkit.JavascriptInterface;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * JS端调用Native管理器
 * @author yanling
 * @date 2018-09-14
 */
public class ExtendJSApiManager {

    private static final String TAG = ExtendJSApiManager.class.getSimpleName();

    //定义js端调用的api名称，调用方式window.API_NAME.xxx
    public static final String API_NAME = "extendJSApi";
    //定义JSApi管理器对象
    private static ExtendJSApiManager mInstance;
    //定义Map保存待注册的Native接口
    private Map<String, Class> apiMap = new HashMap<>();
    //定义弱引用Map保存执行过得Method方法，主要是用来提升反射效率
    private WeakHashMap<String, Method> methodWeakHashMap = new WeakHashMap<>();

    /**
     * 单例模式初始化管理器对象
     * @return
     */
    public synchronized ExtendJSApiManager getInstance(){
        if (mInstance == null){
            mInstance = new ExtendJSApiManager();
        }
        return mInstance;
    }

    private ExtendJSApiManager(){}

    /**
     * 注册Native端接口
     * @param apiKey，接口对应的apiKey值，唯一性
     * @param apiClass，接口对应的Class对象，用于后面通过反射进行方法调用
     */
    private void register(String apiKey, Class apiClass){
        if (apiMap != null){
            apiMap.put(apiKey, apiClass);
        }
    }

    /**
     * 注销Native端接口
     * @param apiKey
     */
    private void unRegister(String apiKey){
        if (apiMap != null){
            apiMap.remove(apiKey);
        }
    }

    /**
     * 注销所有Native端接口
     */
    private void clearAll(){
        if (apiMap != null){
            apiMap.clear();
        }
    }

    private Object callMethod(String apiKey, String targetMethod, String data){
        //查找到所需要调用的API
        Class apiClass = apiMap.get(apiKey);
        if (apiClass != null){
            //查找该method是否已经存在缓存中
            Method  method = methodWeakHashMap.get(apiKey + targetMethod);
            if ( method== null){
                //通过反射获取将要执行的Method
                try {
                    method = apiClass.getMethod(targetMethod);
                    //缓存当前的method
                    methodWeakHashMap.put(apiKey + targetMethod, method);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            //直接从缓存中获取数据并执行
            try {
                Object obj = method.invoke(apiClass, data);
                return obj;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class ExtendJSApi{


        /**
         * JS同步执行Native端接口并获取返回值(返回值可以为空"")
         * 注意这里由于JS和Native通信只能在主线程中进行，所以该方法只能用于简单数据请求返回处理场景
         * @return，返回执行数据
         */
        @JavascriptInterface
        public String execute(String apiKey, String methodName, String data){
            //通过反射执行指定的方法并获取返回值
            Object obj = mInstance.callMethod(apiKey, methodName, data);
            return (String)obj;
        }

        @JavascriptInterface
        public void enqueue(String apiKey, String methodName, String callbackId, String data){
            //通过反射执行指定方法，并通过callbackId异步返回
            Object obj = mInstance.callMethod(apiKey, methodName, data);
            //异步回调Native to JS
            //@TODO
        }
    }
}
