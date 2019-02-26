package fangpai.cloudabull.com.serialportlibrary.serial;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/5/15.
 */

public abstract class SerialPortReadThread extends Thread{


    public abstract void onReceivedData(byte[] bytes);//读取到的信息

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;

    public SerialPortReadThread(InputStream inputStream) {
        mInputStream = inputStream;

    }

    @Override
    public void run() {
//        super.run();
        Log.i(TAG, "run: isInterrupted: " + isInterrupted() + " " + Thread.currentThread().getName());
        while (!isInterrupted()) {
            Log.i(TAG, "读取: mInputStream是否为空: " + (mInputStream == null));
            try {
                if (null == mInputStream) {
                    return;
                }
                Thread.sleep(100);//等待100毫秒,让后面的数据一次性读完,不然会分成多次
                byte[] mReadBuffer = new byte[64];
                int size = mInputStream.read(mReadBuffer);
                byte[] readBytes = new byte[size];

                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);
                String content = CommonUtil.bytesToHexString(readBytes);
                Log.i(TAG, "读取 run: readBytes = " + content + "\t读取大小" + size);
                if (size > 0) {
                    onReceivedData(readBytes);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "读取异常: " + e.getMessage());
                return;
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        Log.e(TAG, "release 释放读取线程");
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



}
