package com.yanling.android.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 实例化JSCall对象
     * @param apiKey，JSCall类对应key值
     * @return，返回JSCall对象
     * @throws ExtendException
     */
    public static AbstractJSCall newJSCall(String apiKey) throws ExtendException{
        //通过apiKey找到对象的实例对象
        Class clazz = mInstance.apiMap.get(apiKey);
        if (clazz != null && clazz.getSuperclass().equals(AbstractJSCall.class)){
            //通过类实例化该对象
            try {
                AbstractJSCall jsCall = (AbstractJSCall)clazz.newInstance();
                return jsCall;
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new ExtendException(ExtendException.NATIVE_API_NOT_FOUND, e.getMessage());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new ExtendException(ExtendException.NATIVE_API_NOT_FOUND, e.getMessage());
            }
        }else{
            throw new ExtendException(ExtendException.NATIVE_API_NOT_FOUND, "Native Api Not Found");
        }
    }

    /**
     * 暴露给JS端的Java对象API
     */
    public static class ExtendJSApi{

        /**
         * JS同步执行Native端接口并获取返回值
         * 注意这里由于JS和Native通信只能在主线程中进行，所以该方法只能用于简单数据请求返回处理场景
         * @param apiKey 对应的接口key值
         * @param action 执行操作类型
         * @param data 传递的数据
         * @return，返回执行情况
         */
        @JavascriptInterface
        public String execute(String apiKey, String action, String data){
            try {
                //实例化JSCall
                AbstractJSCall jsCall = newJSCall(apiKey);
                //执行同步操作并返回数据
                return jsCall.jsCall(action, data, "", false);
            } catch (ExtendException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return "";
        }

        /**
         * JS异步执行Native接口
         * @param apiKey 待执行接口key值
         * @param action 操作类型
         * @param callbackId JS端回调的callback id值
         * @param data，传递数据
         * @return，返回执行情况
         */
        @JavascriptInterface
        public String enqueue(String apiKey, String action, String callbackId, String data){
            try {
                //实例化JSCall
                AbstractJSCall jsCall = newJSCall(apiKey);
                //执行异步操作
                jsCall.jsCall(action, data, callbackId, true);
            } catch (ExtendException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return "";
        }
    }
}
