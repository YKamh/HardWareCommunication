package fangpai.cloudabull.com.usblibrary.database;

import android.content.Context;
import android.content.SharedPreferences;

import fangpai.cloudabull.com.usblibrary.bean.UsbObtainBean;
import fangpai.cloudabull.com.usblibrary.utils.GsonUtils;
import fangpai.cloudabull.com.usblibrary.utils.LogUtils;

/**
 * Created by Administrator on 2018/6/22.
 */

public class USBDataBase {

    public final static String CONFIG = "USBDataBase";
    public final static String SVAE_USB_ESC_TSC = "save_usb_esc_tsc";

    public static void setUSB_ESC_TSC (Context context, String content){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SVAE_USB_ESC_TSC ,content);
        editor.commit();
    }

    public static UsbObtainBean getUSB_ESC_TSC(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        String string = sharedPreferences.getString(SVAE_USB_ESC_TSC, "{\"escList\":[{\"pid\":1803,\"vid\":1155},{\"pid\":20497,\"vid\":1046},{\"pid\":8965,\"vid\":1659},{\"pid\":1536,\"vid\":26728},{\"pid\":30084,\"vid\":6790},{\"pid\":22304,\"vid\":1155},{\"pid\":8211,\"vid\":1305}],\"tscList\":[{\"pid\":23,\"vid\":1137},{\"pid\":1280,\"vid\":26728},{\"pid\":85,\"vid\":1137},{\"pid\":22339,\"vid\":1155}]}");
//        String string = sharedPreferences.getString(SVAE_USB_ESC_TSC, "{\"escList\":[{\"pid\":20497,\"vid\":1046},{\"pid\":8965,\"vid\":1659},{\"pid\":1536,\"vid\":26728},{\"pid\":30084,\"vid\":6790},{\"pid\":22304,\"vid\":1155},{\"pid\":8211,\"vid\":1305}],\"tscList\":[{\"pid\":23,\"vid\":1137},{\"pid\":1280,\"vid\":26728},{\"pid\":22339,\"vid\":1155}]}");
        LogUtils.d("jssjjsdsscc",string);
        UsbObtainBean bean = GsonUtils.getObjFromJsonString(string, UsbObtainBean.class);
        return bean;
    }

}
