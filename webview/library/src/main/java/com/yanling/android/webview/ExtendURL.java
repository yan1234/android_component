package com.yanling.android.webview;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

/**
 * 自定义js于java端交互的协议，
 * 主要是用来进行js调用java的一种途径，即java端监听url的变化来达到数据通信
 * @author yanling
 * @date 2017-03-06
 */
public class ExtendURL {


    //定义url的编码格式
    private Charset charset;

    /**
     * js调用native协议格式为：xx://native?{methodName:xxx, data:{data:xxx, callback:xxx}}
     * 其中methodName为native端动态注册的接口名
     * data为js传递的数据格式，其中内层的data为数据，callback为native给js返回数据的回调方法名称，若为null标示不需要回调
     */
    //定义js端调用native端的协议前缀
    private String prefix_jscallnative;

    /**
     * native调用js返回数据协议格式为：xx://js?{data:xxx, callbackId:xxx}
     * data为返回的数据,callbackId为native端调用js端的回调id
     */
    //定义native端调用js端返回数据的协议前缀
    private String prefix_nativecalljs;


    /**
     * 解析js调用native的数据
     * @param url，待解析的url地址
     * @return，返回长度为2的数组，String[0]为methodName, String[1]为data, String[2]为callback
     * @throws ExtendException
     */
    public String[] parseJSCallNative(String url) throws ExtendException{
        //首先截取"?"后面的数据
        String tmp = url.substring(url.indexOf("?")+1);
        try {
            //解析数据
            JSONObject json = new JSONObject(tmp);
            //返回结果
            String[] result = new String[3];
            result[0] = json.getString("methodName");
            result[1] = json.getJSONObject("data").getString("data");
            result[2] = json.getJSONObject("data").getString("callback");

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ExtendException("", e.getMessage());
        }
    }

    /**
     * 解析native端调用js返回数据
     * @param url
     * @return,调用后返回的数据,String[0]为数据，String[1]为callbackId
     * @throws ExtendException
     */
    public String[] parseNativeCallJS(String url) throws ExtendException{
        //首先截取"?"后面的数据
        String tmp = url.substring(url.indexOf("?") + 1);
        try {
            JSONObject json = new JSONObject(tmp);
            String[] result = new String[2];
            result[0] = json.getString("data");
            result[1] = json.getString("callbackId");

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ExtendException("", e.getMessage());
        }
    }

    public String packageNativeCallJSMsg(String msg, String callbackId){

        try {
            JSONObject json = new JSONObject();
            json.put("msg", msg);
            json.put("callbackId", callbackId);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }


    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getPrefix_jscallnative() {
        return prefix_jscallnative;
    }

    public void setPrefix_jscallnative(String prefix_jscallnative) {
        this.prefix_jscallnative = prefix_jscallnative;
    }

    public String getPrefix_nativecalljs() {
        return prefix_nativecalljs;
    }

    public void setPrefix_nativecalljs(String prefix_nativecalljs) {
        this.prefix_nativecalljs = prefix_nativecalljs;
    }
}
