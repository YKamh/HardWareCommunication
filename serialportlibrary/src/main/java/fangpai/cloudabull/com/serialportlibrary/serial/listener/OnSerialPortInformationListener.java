package fangpai.cloudabull.com.serialportlibrary.serial.listener;

/**
 * Created by Administrator on 2018/5/15.
 * 监听串口消息
 */

public interface OnSerialPortInformationListener {

    /**
     * 串口接收数据
     * @param datas
     */
    void onReceiveData(byte[] datas);

    /**
     * 查看串口发送数据
     * @param datas
     */
    void onSendData(byte[] datas);

}
