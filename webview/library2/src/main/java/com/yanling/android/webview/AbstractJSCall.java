package com.yanling.android.webview;


import com.yanling.android.webview.data.JSCallEntity;

/**
 * 定义抽象JS异步调用接口抽象类，继承实现该抽象类即可进行JS和Native交互
 * @author yanling
 * @date 2018-09-17
 */
public abstract class AbstractJSCall {

    //定义JS端回调的Callback
    private String callback;

    /**
     * 同步执行Native调用
     * @param action 同步调用对应的操作类型
     * @param data js端传递的参数
     * @return，返回给js端结果
     */
    public abstract String execute(String action, String data);

    /**
     * 异步执行Native调用
     * @param action 操作类型
     * @param data js端传递参数
     * @return 返回执行情况
     * @throws ExtendException
     */
    public abstract void enqueue(String action, String data);

    /**
     * JS端调用Native接口方法
     * @param entity JS请求的数据
     * @return
     */
    public final String jsCall(JSCallEntity entity){
        if (!entity.isAsync()){
            //同步执行
            return execute(entity.getAction(), entity.getData());
        }else{
            //异步执行
            //保存JS端回调的callback id
            this.callback = entity.getCallback();
            enqueue(entity.getAction(), entity.getData());
            return "";
        }
    }

    /**
     * 异步执行是返回成功回调，无特殊场景，isKeep设置为false便于JS对无用回调的回收，节省内存空间
     * @param result 返回结果
     * @param isKeep true: 表示持续向js端回调，一般用于多个回调结果（进度返回等场景）；false: 只需要回调一次，然后JS端执行后就销毁回调对象，便于内存释放
     */
    public final void success(String result, boolean isKeep){
        ExtendJSCallManager.getInstance().jsCallback(callback, result, true, isKeep ? "true" : "false");
    }

    /**
     * 异步执行时返回失败回调
     * @param errMsg
     */
    public final void error(String errMsg){
        ExtendJSCallManager.getInstance().jsCallback(callback, errMsg, false, "false");
    }

}
