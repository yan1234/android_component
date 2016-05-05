package com.yanling.android.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * 拷贝文件/空目录（没有嵌套）到指定的目录下面
     * @param sourceFile，源文件对象
     * @param targetDir，拷贝到的指定目录文件对象
     * @param isOverride，是否覆盖指定目录对象true：覆盖，false：不覆盖
     * @return，true:拷贝成功，false:拷贝失败
     * @throws FileNotFoundException
     * @throws FileAlreadyExistsException
     */
    public static boolean copyToDir(File sourceFile,  File targetDir, boolean isOverride)
            throws FileNotFoundException, FileAlreadyExistsException{
        //判断源文件和目标文件是否存在或则目标文件是否是目录
        /**
         * 这里有3种情况直接返回
         * 1、待拷贝的源文件不存在
         * 2、拷贝到的目标文件不存在
         * 3、拷贝到的目标文件不是目录
         */
        if (!sourceFile.exists() || !targetDir.exists() || targetDir.isFile()){
            throw new FileNotFoundException();
        }
        //先得到目标文件对象
        File targetFile = new File(targetDir.getPath() + File.separator + sourceFile.getName());
        //如果不指定覆盖
        /**
         * 这里加入一个文件和目录的区分，有可能文件没有后缀名和目录重名导致混淆
         * 所以判断下待拷贝的源文件对象和目标文件对象是否同为文件/目录
         */
        if (!isOverride && targetFile.exists() && (sourceFile.isFile() == targetFile.isFile())){
            throw new FileAlreadyExistsException("目标目录已经存在该文件对象");
        }
        //根据待拷贝的源文件是目录还是文件进行区分
        if (sourceFile.isFile()){
            //待拷贝的是文件
            //通过输入输出流进行文件拷贝
            //得到文件输入输出流对象
            FileInputStream fis = new FileInputStream(sourceFile);
            FileOutputStream fos = new FileOutputStream(targetFile);
            try {
                //写入文件
                IO.inputToOutput(fis, fos);
            } catch (IOException e) {
                e.printStackTrace();
                try{
                    //出现异常，关闭输入输出流
                    fis.close();
                    fos.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
                //返回失败
                return false;
            }
        }else{
            //待拷贝的是目录
            //直接创建目录
            targetFile.mkdirs();
        }
        return true;
    }


    /**
     * 拷贝文件/空目录（没有嵌套）到指定的目录下面
     * @param sourceFile，源文件对象
     * @param targetDirPath，拷贝到的指定目录文件对象
     * @param isOverride，是否覆盖指定目录对象true：覆盖，false：不覆盖
     * @return，true:拷贝成功，false:拷贝失败
     * @throws FileNotFoundException
     * @throws FileAlreadyExistsException
     */
    public static boolean copyToDir(File sourceFile, String targetDirPath, boolean isOverride)
        throws FileNotFoundException, FileAlreadyExistsException{
        //根据路径转换为文件对象
        return copyToDir(sourceFile, new File(targetDirPath), isOverride);
    }

    /**
     * 拷贝文件/空目录（没有嵌套）到指定的目录下面
     * @param sourceFilePath，源文件对象
     * @param targetDir，拷贝到的指定目录文件对象
     * @param isOverride，是否覆盖指定目录对象true：覆盖，false：不覆盖
     * @return，true:拷贝成功，false:拷贝失败
     * @throws FileNotFoundException
     * @throws FileAlreadyExistsException
     */
    public static boolean copyToDir(String sourceFilePath, File targetDir, boolean isOverride)
        throws FileNotFoundException, FileAlreadyExistsException{
        //根据路径转化为文件对象
        return copyToDir(new File(sourceFilePath), targetDir, isOverride);
    }

    /**
     * 拷贝文件/空目录（没有嵌套）到指定的目录下面
     * @param sourceFilePath，源文件对象
     * @param targetDirPath，拷贝到的指定目录文件对象
     * @param isOverride，是否覆盖指定目录对象true：覆盖，false：不覆盖
     * @return，true:拷贝成功，false:拷贝失败
     * @throws FileNotFoundException
     * @throws FileAlreadyExistsException
     */
    public static boolean copyToDir(String sourceFilePath, String targetDirPath, boolean isOverride)
        throws FileNotFoundException, FileAlreadyExistsException{
        //根据路径转化为文件对象
        return copyToDir(new File(sourceFilePath), new File(targetDirPath), isOverride);
    }

}
