package com.yanling.android.webview;

/**
 * 扩展的异常类
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendException extends Exception{

    private static final String TAG = ExtendException.class.getSimpleName();

    //定义错误编码
    //本地接口不存在
    public static final String CODE_NATIVE_API_NOT_FOUND = "-1";
    public static final String MSG_NATIVE_API_NOT_FOUND = "Native Api Not Found";
    //内部系统处理异常
    public static final String CODE_INTERNAL_EXCEPTION = "-2";
    public static final String MSG_INTERNAL_EXCEPTION = "Internal Exception";
    //数据协议格式错误异常
    public static final String CODE_URL_DATA_WRONG = "-3";
    public static final String MSG_URL_DATA_WRONG = "Url Data Is Wrong";


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
