package com.yanling.android.webview.data;


import com.yanling.android.webview.ExtendException;
import com.yanling.android.webview.ExtendJSCallManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 自定义扩展的JSCall URL数据协议
 * 协议格式
 * jscall://apiKey?{action:'', data: '', callback: '', isAsync: true}
 * @author yanling
 * @date 2018-09-19
 */
public class ExtendJSURL {

    private static final String TAG = ExtendJSURL.class.getSimpleName();

    //定义URL协议的前缀
    private static final String PREFIX_STRING = "jscall://";


    /**
     * 解析url 数据
     * @param url js端传递的url数据
     * @param entity 将解析的数据封装在entity中
     * @return 返回apiKey
     * @throws ExtendException
     */
    public static String parseURL(String url, JSCallEntity entity) throws ExtendException{
        //1、校验url数据的合法性
        if (!isJSCallURL(url)){
            throw new ExtendException(ExtendException.CODE_URL_DATA_WRONG, ExtendException.MSG_URL_DATA_WRONG);
        }
        //截取出apiKey和data数据域
        String[] tmp = url.substring(PREFIX_STRING.length()).split("\\?");
        if (tmp != null && tmp.length == 2){
            //获取apiKey
            String apiKey = tmp[0];
            //获取数据data信息
            try {
                JSONObject json = new JSONObject(tmp[1]);
                entity.setAction(json.getString("action"));
                entity.setData(json.getString("data"));
                entity.setCallback(json.getString("callback"));
                entity.setAsync(json.getBoolean("isAsync"));
                //返回apiKey,请求数据通过参数引用传递返回
                return apiKey;
            } catch (JSONException e) {
                e.printStackTrace();
                ExtendJSCallManager.log(ExtendJSCallManager.LogLevel.ERROR, TAG, e.getMessage());
                throw new ExtendException(ExtendException.CODE_URL_DATA_WRONG, ExtendException.MSG_URL_DATA_WRONG);
            }
        }else{
            throw new ExtendException(ExtendException.CODE_URL_DATA_WRONG, ExtendException.MSG_URL_DATA_WRONG);
        }
    }

    /**
     * 封装Native端返回JS端结果数据{"status": true, "result": xxx}
     * @param result 返回实际结果值
     * @param status 结果状态，true: 执行成功，false: 执行失败
     * @return
     */
    public static String packageResult(String result, boolean status){
        JSONObject json = new JSONObject();
        try {
            //封装状态和结果
            json.put("status", status);
            json.put("result", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 校验是否是JS Call协议
     * @param url，待校验的url信息
     * @return
     */
    private static boolean isJSCallURL(String url){
        if (url != null && url.startsWith(PREFIX_STRING)){
            return true;
        }
        return false;
    }

}
