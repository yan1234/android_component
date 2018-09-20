//常量定义 start

//定义常量标识JSCall
var const_jsToNativeMode = {
    // js调用java对象模式
    JS_OBJECT: 1,
    // 拦截url监听模式
    INTERCEPT_URL: 2,
    // 拦截prompt监听模式
    INTERCEPT_PROMPT: 3
};
//定义JS_OBJECT模式下Native端的接口名
var const_nativeApiName = "extendJSInterface";
// 初始化全局回调callbacks对象，相当于map全局保存Native回调js对象
window.callbacks = new Object;
var callbacks = window.callbacks;

//常量定义 end

/**
 * 封装自定义url通信协议数据 （"apiKey?{'action': '', 'data': '', 'callback':'', 'isAsync': true}"）
 * @param apiKey 对应的Native接口唯一key值，由Native注册时提供
 * @param action 指定接口的操作类型参数
 * @param data 传递数据
 * @param callback 回调JS端接口对象，如果是同步传递空
 * @param isAsync 表示是同步请求还是异步请求
 * @return 返回格式化的url协议数据
 */
function packageApiData(apiKey, action, data, callback, isAsync){
    var jsonObj = {
        "action": action,
        "data": data,
        "callback": callback,
        "isAsync": isAsync
    };
    return apiKey + "?" + JSON.stringify(jsonObj);
}

/**
 * 不同的策略执行不同的调用方式
 * @param jsToNativeMode 调用的策略模式
 * @param urlData 传递的url协议数据
 */
function jsCall(jsToNativeMode, urlData){
    if (jsToNativeMode == const_jsToNativeMode.JS_OBJECT){
        //js调用Native接口模式，Native端注册addJavascriptInterface接口
        //调用形式为window.xxx.xxx
        return window[const_nativeApiName].execute(urlData);
    }else if (jsToNativeMode == const_jsToNativeMode.INTERCEPT_URL){
        // Native端监听url变化然后获取请求数据
        window.location.href = urlData;
    }else if (jsToNativeMode == const_jsToNativeMode.INTERCEPT_PROMPT){
        // Native端监听prompt的值
        return window.prompt(urlData);
    }
}

var extendJSCall = function(){
    //定义对象并返回
    var extendJSCall = new Object;

    /**
     * JSCall初始化，主要是设置Native 调用模式（三种模式），初始化全局回调callbacks对象
     * @param jsToNativeMode 三种模式
     */
    extendJSCall.init = function(jsToNativeMode){
        this.jsToNativeMode = jsToNativeMode;
        this.callbackId = 1;
    }

    /**
     * 同步请求Native，注意如果使用INTERCEPT_URL模式，则不会有返回值
     * @param apiKey 对应的Native接口唯一key值，由Native注册时提供
     * @param action 指定接口的操作类型参数
     * @param data 传递数据
     * @return 返回结果数据，如果没有则返回undefined
     */
    extendJSCall.execute = function(apiKey, action, data){
        //同步调用
        var result = jsCall(this.jsToNativeMode, packageApiData(apiKey, action, data, "", false));
        return result;
    }

    /**
     * 异步请求Native
     * @param apiKey 对应的Native接口唯一key值，由Native注册时提供
     * @param action 指定接口的操作类型参数
     * @param data 传递数据
     * @param success 处理成功回调接口
     * @param error 处理失败回调接口
     */
    extendJSCall.enqueue = function(apiKey, action, data, success, error){
        //将回调对象保存到全局作用域中的callbacks对象中
        var callbackId = apiKey + this.callbackId ++;
        //保存成功和失败回调
        callbacks[callbackId] = {
            success: success,
            error: error
        };
        //异步调用
        var result = jsCall(this.jsToNativeMode, packageApiData(apiKey, action, data, callbackId, true));
        //对结果进行回调处理
    }

    return extendJSCall;
}();