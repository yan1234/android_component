package com.yanling.android.webview;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义扩展的JSCall管理器,负责整个JS调用Native的调度控制
 * @author yanling
 * @date 2018-09-19
 */
public class ExtendJSCallManager {

    private static final String TAG = ExtendJSCallManager.class.getSimpleName();

    //定义JSApi管理器对象
    private static ExtendJSCallManager mInstance;
    //定义Map保存待注册的Native接口
    private Map<String, Class> apiMap = new HashMap<>();

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
     * 注册Native端接口
     * @param apiKey，接口对应的apiKey值，唯一性
     * @param apiClass，接口对应的Class对象，用于后面通过反射进行方法调用
     */
    public void register(String apiKey, Class apiClass){
        if (apiMap != null){
            apiMap.put(apiKey, apiClass);
        }
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
     * 注销所有Native端接口
     */
    public void clearAll(){
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
}
