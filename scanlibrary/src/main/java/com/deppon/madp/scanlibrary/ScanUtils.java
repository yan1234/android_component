package com.deppon.madp.scanlibrary;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.style.ImageSpan;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 扫码处理工具集，主要用于扫码解析，二维码生成等基本操作
 * 这个工具类为扫码界面提供服务，将基本的条码解析，生成抽象处理处理
 * 为后面的替换底层的扫码处理接口提供中间层，
 * 比如目前采用Zbar进行处理，后期需要适配Zxing，直接替换该工具中实现即可
 *
 * Created by yanling on 2015/10/26.
 */
public class ScanUtils {

    public static final String TAG = ScanUtils.class.getSimpleName();

    /**
     * 解析条码
     * @param data，扫码的预览数据
     * @param width，宽度
     * @param height，高度
     * @param scanRect, 扫描框矩形
     * @return，返回结果信息(后期扩展可以返回条码类别+条码结果），如果解析失败返回null
     */
    public static String decode(byte[] data, int width, int height, Rect scanRect){
        //配置解析参数
        ImageScanner imageScanner = new ImageScanner();
        imageScanner.setConfig(0, Config.X_DENSITY, 3);
        imageScanner.setConfig(0, Config.Y_DENSITY, 3);
        Image scan = new Image(width, height, "Y800");
        //将相机数据翻转90度
        scan.setCrop(scanRect.top, scanRect.left, scanRect.bottom, scanRect.right);
        //设置数据源
        scan.setData(data);

        int result = imageScanner.scanImage(scan);

        if (result != 0){
            SymbolSet symbolSet = imageScanner.getResults();
            for (Symbol sym : symbolSet){
                String barcode = sym.getData();
                return barcode;
            }
        }
        return null;
    }

    /**
     * 生成指定内容和大小的二维码图片（编码格式为utf-8)
     * @param content，二维码内容
     * @param width，生成图片的宽度
     * @param height，生成图片的高度
     * @return，返回二维码图片
     */
    public static Bitmap encodeQRCode(String content, int width, int height){
        //定义二维码生成对象
        MultiFormatWriter writer = new MultiFormatWriter();
        Bitmap bitmap = null;
        //配置生成格式的参数
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        //设置编码为utf-8
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            //将对应的内容进行转换
            BitMatrix matrix = writer.encode(content,
                    BarcodeFormat.QR_CODE,
                    width, height,
                    hints);
            //开始生成图片
            int[] rawData = new int[width * height];
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int color = Color.WHITE;
                    if (matrix.get(i, j)) {
                        color = Color.BLACK;
                    }
                    rawData[i + (j * height)] = color;
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(rawData, 0, width, 0, 0, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
            Log.e(TAG, "条码生成错误>>>>>"+e.getMessage());
        }
        return bitmap;
    }

    static {
        System.loadLibrary("iconv");
    }
}
