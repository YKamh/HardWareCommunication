package fangpai.cloudabull.com.serialportlibrary.serial;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;



import java.io.File;

import fangpai.cloudabull.com.serialportlibrary.serial.listener.OnOpenSerialPortListener;
import fangpai.cloudabull.com.serialportlibrary.serial.listener.OnSerialPortInformationListener;
import fangpai.cloudabull.com.serialportlibrary.utils.SystemUtil;

/**
 *
 * @author Administrator
 * @date 2018/5/16
 */

public class PrintSerialPort extends SerialPortManager implements OnOpenSerialPortListener {

    private Context mContext;
    private String TAG = getClass().getSimpleName();
    private OnSerialPortInformationListener listener;

    private static PrintSerialPort mInstance = null;


    private PrintSerialPort(Context context) {
        this.mContext = context;
    }

    private PrintSerialPort(String path, int baudRate, Context context) {
        mContext = context;
        setOnOpenSerialPortListener(this);
        openSerialPort(path, baudRate);
    }

    public static PrintSerialPort getInstance(Context context){
        if (SystemUtil.getDeviceBrand().equals(SystemUtil.SYSTEM_VENDOR)){
            return mInstance = new PrintSerialPort(context);
        }else{
            throw new NullPointerException("The Mainboard must be made in FounPad");
        }
    }

    public void openPort(String path, int baudRate){
        setOnOpenSerialPortListener(this);
        openSerialPort(path, baudRate);
    }

    public void closePort(){
        closeSerialPort();
    }


    @Override
    public void onReceiveData(byte[] datas) {
        for (int i = 0; i < datas.length; i++) {
            Log.d("xxxxxxxxxx",i+"onReceiveData："+datas[i]);
        }
        Log.d("xxxxxxxxxx","onReceiveData："+CommonUtil.bytesToHexString(datas));
        listener.onReceiveData(datas);
    }

    @Override
    public void onSendData(byte[] datas) {
        for (int i = 0; i < datas.length; i++) {
            Log.d("xxxxxxxxxx",i+"onSendData："+datas[i]);
        }
        listener.onSendData(datas);
    }

    @Override
    public void onSuccess(File device) {
        Log.d("xxxxxxxxxx","onSuccess：串口打开成功");
    }

    @Override
    public void onFailure(File device, Status status) {
        Log.d("xxxxxxxxxx","onFailure：串口打开失败");
    }


    /**
     * 发送十六进制指令
     *
     * @param t 指令
     */
    public void sendHex(String t) {
        Log.d(TAG, "sendHex():" + t);
        final byte[] bOutArray = CommonUtil.hexToByteArr(t);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBytes(bOutArray);

            }
        }, 200);

    }

    public void sendHex(final byte[] data){
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                sendBytes(data);

            }
        }, 200);
    }

    public void setOnSerialPortInformationListener(OnSerialPortInformationListener listener) {
        this.listener = listener;
    }


}
