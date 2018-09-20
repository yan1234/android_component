package com.yanling.android.webview.data;

/**
 * JS请求的信息的实体
 * @author yanling
 * @date 2018-09-19
 */
public class JSCallEntity {


    //false: 同步执行，会阻塞当前操作，true: 异步执行，通过传递的callbackId进行JS回调
    private boolean isAsync;

    //执行操作类型
    private String action;

    //请求数据
    private String data;

    //异步执行时回调JS端接口，同步执行时可为空
    private String callback;

    public JSCallEntity() {
    }

    public JSCallEntity(boolean isAsync, String action, String data, String callback) {
        this.isAsync = isAsync;
        this.action = action;
        this.data = data;
        this.callback = callback;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }
}
