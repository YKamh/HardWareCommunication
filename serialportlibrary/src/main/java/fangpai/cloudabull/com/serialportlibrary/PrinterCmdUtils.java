package fangpai.cloudabull.com.serialportlibrary;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/6/28.
 */

public class PrinterCmdUtils {

    /**
     * 这些数据源自爱普生指令集,为POS机硬件默认
     */

    public static final byte ESC = 27;//换码
    public static final byte FS = 28;//文本分隔符
    public static final byte GS = 29;//组分隔符
    public static final byte DLE = 16;//数据连接换码
    public static final byte EOT = 4;//传输结束
    public static final byte ENQ = 5;//询问字符
    public static final byte SP = 32;//空格
    public static final byte HT = 9;//横向列表
    public static final byte LF = 10;//打印并换行（水平定位）
    public static final byte CR = 13;//归位键
    public static final byte FF = 12;//走纸控制（打印并回到标准模式（在页模式下） ）
    public static final byte CAN = 24;//作废（页模式下取消打印数据 ）


//------------------------打印机初始化-----------------------------
    /**
     * 打印机初始化
     *
     * @return
     */
    public static byte[] init_printer() {
        byte[] result = new byte[2];
        result[0] = ESC;
        result[1] = 64;
        return result;
    }
//------------------------换行-----------------------------
    /**
     * 换行
     *
     * @param lineNum 要换几行
     * @return
     */
    public static byte[] nextLine(int lineNum) {
        byte[] result = new byte[lineNum];
        for (int i = 0; i < lineNum; i++) {
            result[i] = LF;
        }

        return result;
    }

    /**
     * 实时状态传送
     *
     * @return
     */
    public static byte[] transfer() {
        byte[] result = new byte[3];
        result[0] = DLE;
        result[1] = EOT;
        result[2] = 1;
        return result;
    }
//------------------------下划线-----------------------------
    /**
     * 绘制下划线（1点宽）
     *
     * @return
     */
    public static byte[] underlineWithOneDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 1;
        return result;
    }

    /**
     * 绘制下划线（2点宽）
     *
     * @return
     */
    public static byte[] underlineWithTwoDotWidthOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 2;
        return result;
    }

    /**
     * 取消绘制下划线
     *
     * @return
     */
    public static byte[] underlineOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 45;
        result[2] = 0;
        return result;
    }

//------------------------加粗-----------------------------

    /**
     * 选择加粗模式
     *
     * @return
     */
    public static byte[] boldOn() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0xF;
        return result;
    }

    /**
     * 取消加粗模式
     *
     * @return
     */
    public static byte[] boldOff() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 69;
        result[2] = 0;
        return result;
    }

//------------------------对齐-----------------------------

    /**
     * 左对齐
     *
     * @return
     */
    public static byte[] alignLeft() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 0;
        return result;
    }

    /**
     * 居中对齐
     *
     * @return
     */
    public static byte[] alignCenter() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 1;
        return result;
    }

    /**
     * 右对齐
     *
     * @return
     */
    public static byte[] alignRight() {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 97;
        result[2] = 2;
        return result;
    }

    /**
     * 水平方向向右移动col列
     *
     * @param col
     * @return
     */
    public static byte[] set_HT_position(byte col) {
        byte[] result = new byte[4];
        result[0] = ESC;
        result[1] = 68;
        result[2] = col;
        result[3] = 0;
        return result;
    }

    /**
     * 字体变大为标准的n倍
     *
     * @param num 1:正常大小 2:两倍高 3:两倍宽 4:两倍大小 5:三倍高 6:三倍宽 7:三倍大小
     * @return
     */
    public static byte[] fontSizeSetBig(int num) {
        byte realSize = 0;
        switch (num) {
            case 1:
                realSize = 0;
                break;
            case 2:
                realSize = 1;
                break;
            case 3:
                realSize = 16;
                break;
            case 4:
                realSize = 17;
                break;
            case 5:
                realSize = 2;
                break;
            case 6:
                realSize = 32;
                break;
            case 7:
                realSize = 34;
                break;
            default:
                break;
        }
        byte[] result = new byte[3];
        result[0] = 29;
        result[1] = 33;
        result[2] = realSize;
        return result;
    }

//------------------------字体变小-----------------------------

    /**
     * 字体取消倍宽倍高
     *
     * @param num
     * @return
     */
    public static byte[] fontSizeSetSmall(int num) {
        byte[] result = new byte[3];
        result[0] = ESC;
        result[1] = 33;

        return result;
    }

//------------------------切纸-----------------------------

    /**
     * 进纸并全部切割
     *
     * @return
     */
    public static byte[] feedPaperCutAll() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 65;
        result[3] = 0;
        return result;
    }

    /**
     * 进纸并切割（左边留一点不切）
     *
     * @return
     */
    public static byte[] feedPaperCutPartial() {
        byte[] result = new byte[4];
        result[0] = GS;
        result[1] = 86;
        result[2] = 66;
        result[3] = 0;
        return result;
    }

    //------------------------切纸-----------------------------
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }


    public static byte[] byteMerger(byte[][] byteList) {

        int length = 0;
        for (int i = 0; i < byteList.length; i++) {
            length += byteList[i].length;
        }
        byte[] result = new byte[length];

        int index = 0;
        for (int i = 0; i < byteList.length; i++) {
            byte[] nowByte = byteList[i];
            for (int k = 0; k < byteList[i].length; k++) {
                result[index] = nowByte[k];
                index++;
            }
        }
        return result;
    }

    /**
     * 测试打印
     *
     * @param msg
     * @return
     */
    public static byte[] printerText(String msg) {
        try {
            byte[] text = msg.getBytes("gb2312");

            byte[][] cmdBytes = {
                    text, nextLine(1)
            };

            return byteMerger(cmdBytes);

        } catch (UnsupportedEncodingException e) {

// TODO Auto-generated catch block

            e.printStackTrace();
        }
        return null;

    }

    /**
     * 根据Unicode编码判断中文汉字和符号
     * 判断中文汉字和符号
     *
     * @param c
     * @return
     */
    public static boolean isZhongWen(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            Log.d("222isZhongWen", "isZhongWen: " + c);
            return true;
        }
        return false;
    }

    // 根据UnicodeBlock方法判断中文标点符号
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isChinesePunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
                || ub == Character.UnicodeBlock.VERTICAL_FORMS) {
            return true;
        } else {
            return false;
        }
    }


    //居中2
    public static String juZhong2(String str, int num) {
        char[] chars = str.toCharArray();//字符數
        StringBuffer sb = new StringBuffer();
        int zhongwenNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (isZhongWen(chars[i])) {
                zhongwenNum++;
            }
        }
        int left = (num - 2 * zhongwenNum - (chars.length - zhongwenNum)) / 2;
        int you = num - left - 2 * zhongwenNum - (chars.length - zhongwenNum);
        for (int i = 0; i < left; i++) {
            sb.append(" ");
        }
        sb.append(str);
        for (int i = 0; i < you; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    //居左2
    public static String zuoBian2(String str, int num) {//最多9个中文 18
        char[] chars = str.toCharArray();//字符數
        StringBuffer sb = new StringBuffer();
        int zhongwenNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (isZhongWen(chars[i])) {
                zhongwenNum++;
            }
        }

        int chaochu = zhongwenNum * 2 + (chars.length - zhongwenNum) - num;
        //超出num 的字节数
        if (chaochu > 0) {
            int k = num / 2 + 1;
            for (int i = 0; i < k; i++) {
                if (i == k - 2 || i == k - 3) {
                    sb.append(".");
                } else {
                    sb.append(chars[i]);
                }
            }
            String s = sb.toString();
            char[] charArray = s.toCharArray();
            zhongwenNum = 0;
            for (int i = 0; i < charArray.length; i++) {
                if (isZhongWen(charArray[i])) {
                    zhongwenNum++;
                }
            }
            int you = num - 2 * zhongwenNum - (charArray.length - zhongwenNum);
            for (int i = 0; i < you; i++) {
                sb.append(" ");
            }

        } else {
            int you = num - 2 * zhongwenNum - (chars.length - zhongwenNum);
            sb.append(str);
            for (int i = 0; i < you; i++) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    //居右2
    public static String youBian2(String str, int num) {//最多7个中文 12345.7元
        char[] chars = str.toCharArray();//字符數
        Log.d("youBian2youBian2", "youBian2: " + chars.length + "," + num);
        StringBuffer sb = new StringBuffer();
        int zhongwenNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (isZhongWen(chars[i])) {
                zhongwenNum++;
            }
        }
        int left = num - 2 * zhongwenNum - (chars.length - zhongwenNum);
        Log.d("youBian2youBian2", "left: " + left);
        for (int i = 0; i < left; i++) {
            sb.append(" ");
        }
        sb.append(str);
        return sb.toString();
    }

    public static String juZhong(String str, int num) {
        char[] chars = str.toCharArray();//字符數
        StringBuffer sb = new StringBuffer();
        int zhongwenNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (isZhongWen(chars[i])) {
                zhongwenNum++;
            }
        }
        int left = (num - 2 * zhongwenNum - (chars.length - zhongwenNum)) / 2;
        for (int i = 0; i < left; i++) {
            sb.append(" ");
        }
        sb.append(str);
        return sb.toString();
    }

    public static String youBian(String str, int num) {
        char[] chars = str.toCharArray();//字符數
        StringBuffer sb = new StringBuffer();
        int zhongwenNum = 0;
        for (int i = 0; i < chars.length; i++) {
            if (isZhongWen(chars[i])) {
                zhongwenNum++;
            }
        }
        int left = num - 2 * (zhongwenNum + 1) - (chars.length - zhongwenNum);
        for (int i = 0; i < left; i++) {
            sb.append(" ");
        }
        sb.append(str);
        return sb.toString();
    }

    public static byte[] getBitmapData(boolean is_show, String img_url) {

        Bitmap logo_bitmap;
        if (is_show && !img_url.equals("")) {
//            logo_bitmap = Mydownload.getBitmap(img_url, 120, 120);
            logo_bitmap = null;
        } else {
            logo_bitmap = null;
        }

        byte[] printBitmap = printBitmap(logo_bitmap);
        if (logo_bitmap != null && logo_bitmap.isRecycled()) {
            logo_bitmap.recycle();
        }
        return printBitmap;
    }

    public static byte[] printBitmap(Bitmap bmp) {
        if (bmp == null) {
            byte[] bytes = "".getBytes();
            return bytes;
        }
        bmp = compressPic(bmp);
        byte[] bmpByteArray = draw2PxPoint(bmp);
        return bmpByteArray;
    }

    /**
     * 对图片进行压缩（去除透明度）
     *
     * @param bitmapOrg
     */
    private static Bitmap compressPic(Bitmap bitmapOrg) {
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        // 定义预转换成的图片的宽度和高度
        int newWidth = 150;
        int newHeight = 150;
        Bitmap targetBmp = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas targetCanvas = new Canvas(targetBmp);
        targetCanvas.drawColor(0xffffffff);
        targetCanvas.drawBitmap(bitmapOrg, new Rect(0, 0, width, height), new Rect(0, 0, newWidth, newHeight), null);
        return targetBmp;
    }


    /*************************************************************************
     * 假设一个360*360的图片，分辨率设为24, 共分15行打印 每一行,是一个 360 * 24 的点阵,y轴有24个点,存储在3个byte里面。
     * 即每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     **************************************************************************/
    private static byte[] draw2PxPoint(Bitmap bmp) {
        //先设置一个足够大的size，最后在用数组拷贝复制到一个精确大小的byte数组中
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] tmp = new byte[size];
        int k = 0;
        // 设置行距为0
        tmp[k++] = 0x1B;
        tmp[k++] = 0x33;
        tmp[k++] = 0x00;
        // 居中打印
        tmp[k++] = 0x1B;
        tmp[k++] = 0x61;
        tmp[k++] = 1;
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            tmp[k++] = 0x1B;
            tmp[k++] = 0x2A;// 0x1B 2A 表示图片打印指令
            tmp[k++] = 33; // m=33时，选择24点密度打印
            tmp[k++] = (byte) (bmp.getWidth() % 256); // nL
            tmp[k++] = (byte) (bmp.getWidth() / 256); // nH
            for (int i = 0; i < bmp.getWidth(); i++) {
                for (int m = 0; m < 3; m++) {
                    for (int n = 0; n < 8; n++) {
                        byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        tmp[k] += tmp[k] + b;
                    }
                    k++;
                }
            }
            tmp[k++] = 10;// 换行
        }
        // 恢复默认行距
        tmp[k++] = 0x1B;
        tmp[k++] = 0x32;
        byte[] result = new byte[k];
        System.arraycopy(tmp, 0, result, 0, k);
        return result;
    }

    /**
     * 图片二值化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param bit 位图
     * @return
     */
    private static byte px2Byte(int x, int y, Bitmap bit) {
        if (x < bit.getWidth() && y < bit.getHeight()) {
            byte b;
            int pixel = bit.getPixel(x, y);
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }

    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        // 灰度转化公式
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);
        return gray;
    }


}
