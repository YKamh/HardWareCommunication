package fangpai.cloudabull.com.serialportlibrary.serial.listener;

import java.io.File;

/**
 * Created by Administrator on 2018/5/15.
 * 打开串口监听
 */

public interface OnOpenSerialPortListener {

    void onSuccess(File device);

    void onFailure(File device, Status status);

    enum Status {
        //没有权限
        NO_READ_WRITE_PERMISSION,
        //打开失败
        OPEN_FAIL
    }
}
