package com.yanling.android.utils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;

/**
 * android文件操作工具类
 * @author yanling
 * @date 2016-05-03 09:20
 */
public class FileUtils {


    /**
     * 删除文件夹（支持嵌套文件夹）、文件
     * @param file，待删除的文件对象
     * @throws FileNotFoundException
     */
    public static void deleteFile(File file) throws FileNotFoundException{
        //判断文件对象是否存在
        if (!file.exists()){
            throw new FileNotFoundException();
        }
        //判断该文件对象是目录
        if (!file.isDirectory()){
            //不是目录，则调用文件删除
            deleteFileSafety(file);
            return;
        }
        /**
         * 递归遍历
         * 1、先判断该目录下面是否还存在子目录
         * 2、如果不存在子目录，则遍历子文件直接删除
         * 3、如果存在子目录，则遍历到的子文件直接删除，子目录则递归进入删除
         */
        //得到该目录下的子目录
        File[] listFile = file.listFiles();
        //遍历子目录
        for (File tmpFile : listFile){
            //判断是否是文件
            if (!tmpFile.isDirectory()){
                deleteFileSafety(file);
            }else{
                //判断是目录则递归调用
                deleteFile(tmpFile);
            }
        }
        //最后删除空目录
        deleteFileSafety(file);
    }

    /**
     * 删除文件夹（支持嵌套文件夹）、文件
     * @param filePath，待删除的文件路径
     * @throws FileNotFoundException
     */
    public static void deleteFile(String filePath) throws FileNotFoundException{
        //先得到该文件对象
        File file = new File(filePath);
        deleteFile(file);
    }

    /**
     * 安全的删除文件（先对文件进行重命名，然后再删除）
     * 主要为了解决android系统下对相同文件名删除的资源占用问题
     * @param file，待删除的文件对象
     * @return，true：删除成功；false：删除失败
     * @throws FileNotFoundException
     */
    public static boolean deleteFileSafety(File file) throws FileNotFoundException{
        //判断文件是否存在
        if (!file.exists()){
            //不存在直接抛出异常
            throw new FileNotFoundException();
        }
        //先给该文件进行重命名然后进行删除
        String tmpPath = file.getParent() + File.separator + System.currentTimeMillis();
        File tmp = new File(tmpPath);
        //重命名
        file.renameTo(tmp);
        //重命名然后删除
        return tmp.delete();
    }

    /**
     * 安全的删除文件（先对文件进行重命名，然后再删除）
     * 主要为了解决android系统下对相同文件名删除的资源占用问题
     * @param filePath，待删除的文件路径
     * @return，true：删除成功；false：删除失败
     * @throws FileNotFoundException
     */
    public static boolean deleteFileSafety(String filePath) throws FileNotFoundException{
        //根据路径得到文件对象然后删除
        return deleteFileSafety(new File(filePath));
    }

    /**
     * 拷贝文件
     * @param sourcefile
     * @param targetFile
     * @oaram isOverride
     */
    public static boolean copyFile(File sourcefile,  File targetFile, boolean isOverride)
            throws FileNotFoundException, FileAlreadyExistsException{
        //判断源文件是否存在
        if (!sourcefile.exists()){
            throw new FileNotFoundException();
        }
        //判断目标文件是否存在，并且是否覆盖
        if (!isOverride && targetFile.exists()){
            //当选择不覆盖，并且目标文件对象存在
            throw new FileAlreadyExistsException("目标文件对象已经存在");
        }
        /**
         * 根据待拷贝的源文件是目录还是文件进行区分处理
         */
        if (sourcefile.isDirectory()){
            //如果拷贝到的目标文件是文件对象的话退出
            if (targetFile.isFile()){
                throw new FileNotFoundException("目录不能拷贝到文件中");
            }else{
                //如果是目录的话

            }
        }
        //判断
        return true;
    }

}
