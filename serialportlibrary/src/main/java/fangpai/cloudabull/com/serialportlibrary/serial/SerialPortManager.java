package fangpai.cloudabull.com.serialportlibrary.serial;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import fangpai.cloudabull.com.serialportlibrary.serial.listener.OnOpenSerialPortListener;

/**
 * Created by Administrator on 2018/5/15.
 */

public abstract class SerialPortManager extends SerialPort {

    private static final String TAG = SerialPortManager.class.getSimpleName();
    private HandlerThread mSendingHandlerThread;
    private Handler mSendingHandler;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;
    private OnOpenSerialPortListener mOnOpenSerialPortListener;
    private FileDescriptor fileDescriptor;//一个打开的文件通过唯一的描述符进行引用，该描述符是打开文件的元数据到文件本身的映射。
    private SerialPortReadThread mSerialPortReadThread;

    boolean openSerialPort(String path, int baudRate) {

        File file = new File(path);
        if (!file.canRead() || !file.canWrite()) {//不可读 或者不可写
            boolean success = chmod777(file);
            Log.e(TAG, "修改" + file.getAbsolutePath() + "权限:" + success);
            if (!success) {
                Log.e(TAG, "openSerialPort 没有读写权限");
                if (null != mOnOpenSerialPortListener) {
                    mOnOpenSerialPortListener.onFailure(file, OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION);
                }
                return false;
            }
        }

        try {
            fileDescriptor = open(file.getAbsolutePath(), baudRate, 0);
            inputStream = new FileInputStream(fileDescriptor);
            outputStream = new FileOutputStream(fileDescriptor);
            Log.e(TAG, "openSerialPort 打开成功: " + fileDescriptor);
            if (null != mOnOpenSerialPortListener) {
                mOnOpenSerialPortListener.onSuccess(file);
            }
            startSendThread();
            startReadThread();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 开启发送消息的线程
     */
    private void startSendThread() {
        // 开启发送消息的线程
        mSendingHandlerThread = new HandlerThread("mSendingHandlerThread");
        mSendingHandlerThread.start();
        // Handler
        mSendingHandler = new Handler(mSendingHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                byte[] sendBytes = (byte[]) msg.obj;

                if (null != outputStream && null != sendBytes && 0 < sendBytes.length) {
                    try {
                        outputStream.write(sendBytes);
                        onSendData(sendBytes);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }


    /**
     * 关闭串口
     */
    public void closeSerialPort() {

        if (null != fileDescriptor) {
            close();
            fileDescriptor = null;
        }
        // 停止发送消息的线程
        stopSendThread();
        // 停止接收消息的线程
        stopReadThread();

        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }

        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        mOnOpenSerialPortListener = null;

    }

    /**
     * 停止发送消息线程
     */
    private void stopSendThread() {
        mSendingHandler = null;
        if (null != mSendingHandlerThread) {
            mSendingHandlerThread.interrupt();
            mSendingHandlerThread.quit();
            mSendingHandlerThread = null;
        }
    }

    /**
     * 开启接收消息的线程
     */
    private void startReadThread() {
        Log.e(TAG, "启动读取线程: mSerialPortReadThread 是否为空: " + (mSerialPortReadThread == null));
        if (mSerialPortReadThread == null) {
            mSerialPortReadThread = new SerialPortReadThread(inputStream) {
                @Override
                public void onReceivedData(byte[] bytes) {
                    onReceiveData(bytes);
                    //获取扫描的字节将她转换为16进制
//                        String he = Bytes2HexString(buffe0r);

                }
            };
        }
        Log.e(TAG, "启动读取线程: mSerialPortReadThread 是否活跃: :" + (mSerialPortReadThread.isAlive()));
        if (!mSerialPortReadThread.isAlive()){
            mSerialPortReadThread.start();
            Log.e(TAG, "读取线程已启动: " + mSerialPortReadThread.getName() + "\tisAlive: " + mSerialPortReadThread.isAlive());
        }
    }


    /**
     * 停止接收消息的线程
     *
     */
    private void stopReadThread() {
        if (null != mSerialPortReadThread) {
            mSerialPortReadThread.release();
        }
    }

    /**
     * 发送数据
     *
     * @param sendBytes 发送数据
     * @return 发送是否成功
     */
    public boolean sendBytes(byte[] sendBytes) {
        if (null != fileDescriptor && null != inputStream && null != outputStream) {
            if (null != mSendingHandler) {
                Message message = Message.obtain();
                message.obj = sendBytes;
                return mSendingHandler.sendMessage(message);
            }
        }
        return false;
    }

    /**
     * 添加打开串口监听
     *
     * @param listener
     * @return SerialPortManager
     */
    public SerialPortManager setOnOpenSerialPortListener(OnOpenSerialPortListener listener) {
        mOnOpenSerialPortListener = listener;
        return this;
    }

    /**
     * 串口接收数据
     * @param datas
     */
    public abstract void onReceiveData(byte[] datas);

    /**
     * 查看串口发送数据
     * @param datas
     */
    public abstract void onSendData(byte[] datas);


}
