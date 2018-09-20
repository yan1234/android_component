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
    public abstract String execute(String action, String data) throws ExtendException;

    /**
     * 异步执行Native调用
     * @param action 操作类型
     * @param data js端传递参数
     * @return 返回执行情况
     * @throws ExtendException
     */
    public abstract String enqueue(String action, String data) throws ExtendException;

    /**
     * JS端调用Native接口方法
     * @param entity JS请求的数据
     * @return
     * @throws ExtendException
     */
    public final String jsCall(JSCallEntity entity) throws ExtendException{
        if (!entity.isAsync()){
            //同步执行
            return execute(entity.getAction(), entity.getData());
        }else{
            //异步执行
            //保存JS端回调的callback id
            this.callback = entity.getCallback();
            return enqueue(entity.getAction(), entity.getData());
        }
    }

    /**
     * 异步执行时返回成功回调
     * @param result
     */
    public final void success(String result){
        ExtendJSCallManager.getInstance().jsCallback(callback, result, true);
    }

    /**
     * 异步执行时返回失败回调
     * @param errMsg
     */
    public final void error(String errMsg){
        ExtendJSCallManager.getInstance().jsCallback(callback, errMsg, false);
    }

}
