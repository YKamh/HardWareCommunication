package fangpai.cloudabull.com.serialportlibrary;

import android.content.Context;

import fangpai.cloudabull.com.serialportlibrary.serial.PrintSerialPort;
import fangpai.cloudabull.com.serialportlibrary.serial.listener.OnDataReceivedListener;
import fangpai.cloudabull.com.serialportlibrary.serial.listener.OnSerialPortInformationListener;


/**
 *  串口打印工具类
 * @author Administrator
 * @date 2018/5/21
 *
 */

public class PrintUtils implements OnSerialPortInformationListener {

    private static PrintUtils instance;
    private PrintSerialPort printSerialPort;
    private OnDataReceivedListener listener;

    public PrintUtils(Context context) {
        printSerialPort = PrintSerialPort.getInstance(context);
        printSerialPort.setOnSerialPortInformationListener(this);
    }

    /**
     * 绑定串口回调监听
     * @param listener
     */
    public void setListener(OnDataReceivedListener listener) {
        this.listener = listener;
    }

    public static synchronized PrintUtils getInstance(Context context){
        if (instance == null) {
            instance = new PrintUtils(context);
        }
        return instance;
    }

    /**
     * 打开串口
     * @param path  串口
     * @param baudRate  波特率
     */
    public void openPort(String path, int baudRate){
        printSerialPort.openPort(path, baudRate);
    }

    public void closePort(){
        printSerialPort.closePort();
    }

    /**
     * 发送十六进制指令
     * @param cmd
     */
    public void sendHexCMD(String cmd) {
        printSerialPort.sendHex(cmd);
    }

    public void sendHexCMD(byte[] cmd) {
        printSerialPort.sendHex(cmd);
    }

    @Override
    public void onReceiveData(byte[] datas) {
        listener.onDataReceived(new String(datas));
    }

    @Override
    public void onSendData(byte[] datas) {

    }
}
