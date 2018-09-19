package com.yanling.android.webview;

import android.os.Handler;
import android.os.Looper;

/**
 * 定义抽象JS异步调用方法
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

    public final void success(String result){
        jsCallback(result, true);
    }

    public final void error(String errMsg){
        jsCallback(errMsg, false);
    }

    private void jsCallback(String result, boolean status){
        handler.post(new JSCallback(status, result) {
            @Override
            void onSuccess(String result) {
                //成功回调
            }

            @Override
            void onError(String errMsg) {
                //失败回调
            }
        });
    }

    private abstract class JSCallback implements Runnable{

        //定义返回状态，true: 成功，false: 失败
        private boolean status;
        //定义返回结果
        private String data;

        public JSCallback(boolean status, String data) {
            this.status = status;
            this.data = data;
        }

        /**
         * 成功回调
         * @param result
         */
        abstract void onSuccess(String result);

        /**
         * 失败回调
         * @param errMsg
         */
        abstract void onError(String errMsg);

        @Override
        public void run() {
            //执行回调
            if (status){
                onSuccess(data);
            }else{
                onError(data);
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());

}
