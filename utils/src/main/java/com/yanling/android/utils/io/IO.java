package com.yanling.android.utils.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 处理输入输出流的工具集
 * @author yanling
 * @date 2016-05-04 09:54
 */
public class IO {


    /**
     * 输入流到输出流的转换
     * @param inputStream，输入流对象
     * @param outputStream，输出流对象
     * @throws IOException
     */
    public static void inputToOutput(InputStream inputStream, OutputStream outputStream) throws IOException{
        //定义读取的长度
        int length = 0;
        //定义缓冲区的大小
        byte[] buff = new byte[1024];
        //循环读取输入流的内容到缓冲区
        while ((length = inputStream.read(buff)) != -1){
            //写入输出流
            outputStream.write(buff, 0, length);
        }
        //关闭输入输出流
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

}
