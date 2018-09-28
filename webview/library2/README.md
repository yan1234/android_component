# v2.x版本框架说明文档
> @author yanling

### 一、使用说明

1. 引入依赖库（JS端使用[`ExtendJSCall.js`](src/main/javascript/ExtendJSCall.js), Java端直接使用gradle依赖）

2. Java端初始化

    ```java
        //1、初始化并选择通信模式
        ExtendJSCallManager.getInstance()
            .init(ExtendJSCallManager.MODE_INTERCEPT_PROMPT)
            .injectWebView(webView);

        //2、对应模式的初始化
        //注意三种模式配置的不同
        //1、JS_OBJ模式, 采用扩展的JavascriptInterface接口对象
        //webView.addJavascriptInterface(new ExtendJSInterface(), ExtendJSInterface.API_NAME);
        //2、INTERCEPT_URL模式，采用扩展的WebViewClient对象
        //webView.setWebViewClient(new ExtendWebViewClient());
        //3、INTERCEPT_PROMPT模式，采用扩展的WebChromeClient对象
        //webView.setWebChromeClient(new ExtendWebChromeClient());

        //3、继承AbstractJSCall给JS端提供具体实现接口

        //4、注册API接口
        ExtendJSCallManager.getInstance().register("apiKey", "className");
    ```

 3. JS端初始化

    ```javascript
        //1、初始化通信模式（和Java端保持一致）
        extendJSCall.init(extendJSCall.const_jsToNativeMode.INTERCEPT_URL)

        //2、调用接口("apiKey"为Java端注册)
        //同步调用,可以获取返回值
        var result = extendJSCall.execute("apiKey", "action", "data");

        //异步调用
        extendJSCall.enqueue("apiKey", "action", "data",
            function(success){
                //成功回调
            },
            function(error){
                //失败回调
            }
    ```

 4. Java端接口实现

    ```java

        //定义API接口对象
        public class DemoJSCall extends AbstractJSCall{

            @Override
            public String execute(String action, String data) {
                //同步返回结果
                return "success";
            }

            @Override
            public void enqueue(String action, String data) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //异步返回结果
                        //返回成功
                        success("success", false);
                        //返回失败
                        error("error")
                    }
                }).start();
            }

        }

    ```

### 二、结构说明