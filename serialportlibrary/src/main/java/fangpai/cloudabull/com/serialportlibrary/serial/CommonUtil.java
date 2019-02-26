package fangpai.cloudabull.com.serialportlibrary.serial;

import android.util.Log;

/**
 * Created by Administrator on 2018/5/16.
 * 通用工具
 */
public class CommonUtil {
    private static final String TAG = CommonUtil.class.getSimpleName();
    private final static byte[] hex = "0123456789ABCDEF".getBytes();
    /**
     * 数组转换成十六进制字符串
     * @param bArray
     * @return HexString
     */
    public static  String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * 十六进制转换字符串
     *
     * @return String 对应的字符串  str Byte字符串(Byte之间无分隔符 如:[616C6B])
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /**
     * 转hex字符串转字节数组
     *
     * @param inHex
     * @return
     */
    public static byte[] hexToByteArr(String inHex)// hex字符串转字节数组
    {
        int hexlen = inHex.length();
        byte[] result;
        if (isOdd(hexlen) == 1) {// 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {// 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    // Hex字符串转byte
    private static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    private static int isOdd(int num) {
        return num & 0x1;
    }

    /**
     * 十六进制字符串装二进制字符串
     *
     * @param hexString
     * @return
     */
    public static String hexStrToBinStr(String hexString) {
        StringBuilder bin = new StringBuilder();
        StringBuilder binFragment;
        int iHex;
        hexString = hexString.trim();
        hexString = hexString.replaceFirst("0x", "");

        for (int i = 0; i < hexString.length(); i++) {
            iHex = Integer.parseInt("" + hexString.charAt(i), 16);
            binFragment = new StringBuilder(Integer.toBinaryString(iHex));
            if (binFragment.length() < 4) {
                int len = 4 - binFragment.length();
                for (int j = 0; j < len; j++) {//如果是0开头,默认是会去掉的,但是实际是需要有0的,所以需要手动补足
                    //如: 转换出来的是11  但是实际需要的是0011,所以需要补足4位
                    binFragment.insert(0, "0");
                }
            }
            bin.append(binFragment);
            Log.i(TAG, "十六进制: " + hexString.charAt(i) + "\t二进制: " + binFragment + "\t拼接: " + bin);

        }
        return bin.toString();


        //return Long.toBinaryString(Long.parseLong(hexString, 16));
    }
}
