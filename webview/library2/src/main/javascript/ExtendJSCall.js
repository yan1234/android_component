
//1、调用JavaInterface
function a(){
    var result = window.extendJSInterface.execute("apiKey?{'action': '', 'data': '', 'callback':'', 'isAsync': true}");
}

//2、使用url
function b(){
    window.location.href = "apiKey?{'action': '', 'data': '', 'callback':'', 'isAsync': true}";
}

//3、使用onPrompt
function c(){
    var result = window.prompt("apiKey?{'action': '', 'data': '', 'callback':'', 'isAsync': true}", "");
}