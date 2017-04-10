package com.yanling.android.utils.log;

import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志工具集（V1.0）
 * 主要包括输出到控制台和文件
 *
 * 使用方法：日志默认存储目录为手机自带存储目录根目录下的log文件夹，
 * 如果要修改目录则初始化时调用setLogDir设置目录，然后调用静态的v，d，i，w，e输出日志
 *
 * @author yanling
 * @date 2017-01-28
 */
public class MyLog {


    //定义日志的级别
    private final static int LEVEL_VERBOSE = 0;
    private final static int LEVEL_DEBUG = 1;
    private final static int LEVEL_INFO = 2;
    private final static int LEVEL_WARN = 3;
    private final static int LEVEL_ERROR = 4;
    private final static int LEVEL_ASSERT = 5;

    //定义日志输出的的目标(0: 输出到终端；1：输出到文件；2：输出到终端和文件)
    public final static int LOG_TO_CONSOLE = 0;
    public final static int LOG_TO_FILE = 1;
    public final static int LOG_TO_ALL = 2;

    //定义日志文件命名格式
    private final static SimpleDateFormat LOG_FILENAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    //定义日志内容时间标注格式
    private final static SimpleDateFormat LOG_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //定义日志存储目录(默认在自带存储卡的log目录下)
    private static String logDir = Environment.getExternalStorageDirectory()
            + File.separator + "log";

    //定义双缓冲队列进行保存, 当其中一个缓冲区快满是向另一个缓冲区写入，然后将满的缓冲区写入文件
    private static char[] buff1, buff2;

    //定义变量保存是否输出日志，可用于快速的开启/关闭日志
    private static boolean isLog = true;


    /**
     * 对外日志接口
     */

    /**
     * 日志工具初始化
     * @param logDir，日志保存的目录
     * @param saveDays，日志保存的天数
     */
    public static void init(String logDir, int saveDays){
        //设置当前日志的保存目录(默认目录为手机自带存储根目录下的log文件夹)
        MyLog.logDir = logDir;
        //初始化缓冲队列
        //buff1 = new char[1024 * 5];
        //buff2 = new char[1024 * 5];
        //根据保存天数删除多余的文件
        File dir = new File(logDir);
        if (dir.exists()){
            //遍历文件删除7天以前的
            File[] files = dir.listFiles();
            //定义当前时间
            Calendar c = Calendar.getInstance();
            //获取当天凌晨的毫秒数
            long currentDay = new Date(c.YEAR, c.MONTH, c.DAY_OF_MONTH).getTime();
            //定义时间间隔
            long periods = saveDays * 24 * 60 * 60 * 1000;
            for (File file : files){
                //如果时间间隔超过了定义的，删除
                if (currentDay - file.lastModified() > periods){
                    file.delete();
                }
            }
        }
    }

    /**
     * 设置是否输出日志
     * @param isLog，true:表示输出；false:标示关闭输出
     */
    public static void setIsLog(boolean isLog){
        MyLog.isLog = isLog;
    }


    /**
     * 输出日志级别为Verbose的到控制台
     * @param tag，日志标签
     * @param message，日志消息实体
     */
    public static void v(String tag, String message){
        log(LEVEL_VERBOSE, tag, message, LOG_TO_CONSOLE);
    }

    /**
     * 输出日志级别为Verbose的到控制台或文件（输出到文件的会默认输出到控制台）
     * @param tag，日志标签
     * @param message，日志信息实体
     * @param logTo，标志变量表示日志输出，主要是3种情况，LOG_TO_CONSOLE, LOG_TO_FILE, LOG_TO_ALL
     */
    public static void v(String tag, String message, int logTo){
        log(LEVEL_VERBOSE, tag, message, logTo);
    }


    public static void d(String tag, String message){
        log(LEVEL_DEBUG, tag, message, LOG_TO_CONSOLE);
    }

    public static void d(String tag, String message, int logTo){
        log(LEVEL_DEBUG, tag, message, logTo);
    }


    public static void i(String tag, String message){
        log(LEVEL_INFO, tag, message, LOG_TO_CONSOLE);
    }

    public static void i(String tag, String message, int logTo){
        log(LEVEL_INFO, tag, message, logTo);
    }


    public static void w(String tag, String message){
        log(LEVEL_WARN, tag, message, LOG_TO_CONSOLE);
    }

    public static void w(String tag, String message, int logTo){
        log(LEVEL_WARN, tag, message, logTo);
    }


    public static void e(String tag, String message){
        log(LEVEL_ERROR, tag, message, LOG_TO_CONSOLE);
    }

    public static void e(String tag, String message, int logTo){
        log(LEVEL_ERROR, tag, message, logTo);
    }


    /**
     * 输出日志到控制台，文件
     * @param level，日志级别
     * @param tag，日志标签
     * @param message，日志实体信息
     * @param logTo，日志输出目标
     */
    private static void log(int level, String tag, String message, int logTo){

        //判断是否输出日志
        if (!isLog){
            //进行日志输出,直接退出
            return;
        }

        //定义变量保存日志类别用于保存文件
        String levelType = "";
        //判断日志类别
        switch (level){
            case LEVEL_VERBOSE:
                //判断是否需要向控制台输出
                if (logTo == MyLog.LOG_TO_CONSOLE || logTo == MyLog.LOG_TO_ALL){
                    Log.v(tag, message);
                }
                levelType = "Verbose";
                break;
            case LEVEL_DEBUG:
                //判断是否需要向控制台输出
                if (logTo == MyLog.LOG_TO_CONSOLE || logTo == MyLog.LOG_TO_ALL){
                    Log.d(tag, message);
                }
                levelType = "Debug";
                break;
            case LEVEL_INFO:
                //判断是否需要向控制台输出
                if (logTo == MyLog.LOG_TO_CONSOLE || logTo == MyLog.LOG_TO_ALL){
                    Log.i(tag, message);
                }
                levelType = "Info";
                break;
            case LEVEL_WARN:
                //判断是否需要向控制台输出
                if (logTo == MyLog.LOG_TO_CONSOLE || logTo == MyLog.LOG_TO_ALL){
                    Log.w(tag, message);
                }
                levelType = "Warn";
                break;
            case LEVEL_ERROR:
                //判断是否需要向控制台输出
                if (logTo == MyLog.LOG_TO_CONSOLE || logTo == MyLog.LOG_TO_ALL){
                    Log.e(tag, message);
                }
                levelType = "Error";
                break;
        }
        //输出到文件
        if (logTo == LOG_TO_FILE || logTo == LOG_TO_ALL){
            //写入到文件
            writeToFile(levelType, tag, message);
        }
    }

    /**
     * 将日志按照一定的格式写入文件
     * @param levelType，日志级别
     * @param tag，日志标签
     * @param message， 日志实体消息
     */
    private static void writeToFile(String levelType, String tag, String message){

        //首先判断日志目录是否存在
        File dir = new File(logDir);
        if (!dir.exists()){
            dir.mkdirs();
        }
        //写入文件
        FileWriter fw = null;
        String logFileName = LOG_FILENAME_FORMAT.format(new Date()) + ".log";
        try {
            fw = new FileWriter(dir.getPath() + File.separator + logFileName, true);
            //先添加时间标签
            fw.append(LOG_TIME_FORMAT.format(new Date()) + "  ");
            //再添加日志级别
            fw.append(levelType + "  ");
            //再添加日志标签
            fw.append(tag + "  ");
            //然后添加日志内容
            fw.append(message);
            //最后换行
            fw.append("\r\n");

            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭输出流
            if (fw != null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
