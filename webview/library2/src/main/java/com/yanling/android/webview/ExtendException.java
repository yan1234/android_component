package com.yanling.android.webview;

import android.util.Log;

/**
 * 扩展的异常类
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendException extends Exception{

    private static final String TAG = ExtendException.class.getSimpleName();

    //定义错误编码
    //不是在主线程中运行
    public static final String CODE_NOT_RUN_ON_MAIN_THREAD = "1";
    //本地接口不存在
    public static final String NATIVE_API_NOT_FOUND = "2";

    //定义错误编码
    private String errCode;

    //定义错误信息
    private String errMsg;

    public ExtendException(){
        super();
    }

    public ExtendException(String errCode, String errMsg){
        super();
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String getMessage() {
        return "[" + getErrCode() + "], " + getErrMsg();
    }
}
