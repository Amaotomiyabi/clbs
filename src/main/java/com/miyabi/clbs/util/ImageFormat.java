package com.miyabi.clbs.util;

/**
 * com.miyabi.clbs.util
 *
 * @Author amotomiyabi
 * @Date 2020/04/28/
 * @Description 图片格式工具
 */

import java.util.Arrays;
import java.util.UUID;

/**
 * 常用文件的文件头如下：(以前六位为准)
 * JPEG (jpg)，文件头：FFD8FF
 * PNG (png)，文件头：89504E47
 * GIF (gif)，文件头：47494638
 * TIFF (tif)，文件头：49492A00
 * Windows Bitmap (bmp)，文件头：424D
 * CAD (dwg)，文件头：41433130
 * Adobe Photoshop (psd)，文件头：38425053
 * Rich Text Format (rtf)，文件头：7B5C727466
 * XML (xml)，文件头：3C3F786D6C
 * HTML (html)，文件头：68746D6C3E
 * Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
 * Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
 * Outlook (pst)，文件头：2142444E
 * MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
 * MS Access (mdb)，文件头：5374616E64617264204A
 * WordPerfect (wpd)，文件头：FF575043
 * Postscript (eps.or.ps)，文件头：252150532D41646F6265
 * Adobe Acrobat (pdf)，文件头：255044462D312E
 * Quicken (qdf)，文件头：AC9EBD8F
 * Windows Password (pwl)，文件头：E3828596
 * ZIP Archive (zip)，文件头：504B0304
 * RAR Archive (rar)，文件头：52617221
 * Wave (wav)，文件头：57415645
 * AVI (avi)，文件头：41564920
 * Real Audio (ram)，文件头：2E7261FD
 * Real Media (rm)，文件头：2E524D46
 * MPEG (mpg)，文件头：000001BA
 * MPEG (mpg)，文件头：000001B3
 * Quicktime (mov)，文件头：6D6F6F76
 * Windows Media (asf)，文件头：3026B2758E66CF11
 * MIDI (mid)，文件头：4D546864
 *
 * @author amotomiyabi
 */
public class ImageFormat {
    public static final String SAVE_PATH = "C:\\Users\\amotomiyabi\\Desktop\\testImage\\";
    private static final String TYPE_JPG = ".jpg";
    private static final String TYPE_GIF = ".gif";
    private static final String TYPE_PNG = ".png";
    private static final String TYPE_BMP = ".bmp";
    private static final String TYPE_WEBP = ".webp";
    private static final String TYPE_TIF = ".tif";

    public static String bytesToHex(byte[] bytes) {
        var strHex = new StringBuilder();
        for (var b : bytes
        ) {
            int temp = b & 0xFF;
            if (temp < 16) {
                strHex.append("0");
            }
            strHex.append(Integer.toHexString(temp));
        }
        return strHex.toString();
    }

    public static String judgeFormat(String strHex) {
        return switch (strHex) {
            case "89504E4" -> TYPE_PNG;
            case "4749463" -> TYPE_GIF;
            case "5249464" -> TYPE_WEBP;
            case "49492A0" -> TYPE_TIF;
            default -> TYPE_JPG;
        };
    }

    public static String judgeFormat(byte[] bytes) {
        return judgeFormat(bytesToHex(bytes));
    }

    public static String getSavePath(byte[] imgBytes) {
        return SAVE_PATH + UUID.randomUUID().toString() +
                ImageFormat.judgeFormat(Arrays.copyOf(imgBytes, 3));
    }

    public static String getSavePath(String fileName) {
        return SAVE_PATH + UUID.randomUUID().toString() + fileName.substring(fileName.lastIndexOf("."));
    }
}
