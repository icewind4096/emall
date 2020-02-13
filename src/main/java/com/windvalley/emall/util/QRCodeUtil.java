package com.windvalley.emall.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@Slf4j
public class QRCodeUtil {
    public static final String FORMATNAME = "png";

    //长宽如果不需要留白边，长宽要根据版本调整,否则有白边出现
    public static boolean encode(String content, String filePath, String fileName, String formatName){
    //建立二维码存放目录
        log.info(">>>建立二维码存放目录在:{}", filePath);
        makeDirectory(filePath);
    //产生二维码
        return _encode(content, filePath, fileName, 200, 200, formatName);
    }

    private static boolean _encode(String content, String filePath, String fileName, int height, int width, String formatName) {
        Map hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
        //必须使用utf-8而不是UTF-8,否则含有中文，二维码图形无法识别
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig(Color.BLACK.getRGB(), Color.WHITE.getRGB());
            log.info("准备输出二维码文件>>>{}{}", filePath, fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, formatName, new File(filePath, fileName).toPath(), matrixToImageConfig);
            log.info("输出二维码文件成功位置在>>>{}{}", filePath, fileName);
            return true;
        } catch (WriterException | IOException e) {
            log.info("字符串产生二维码错误", e);
            return false;
        }
    }

    private static void makeDirectory(String path) {
        File directory = new File(path);
        if (directory.isDirectory() == false){
            directory.setWritable(true);
            directory.mkdirs();
        }
    }
}
