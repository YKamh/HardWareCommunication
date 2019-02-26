package fangpai.cloudabull.com.usblibrary.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import fangpai.cloudabull.com.usblibrary.utils.LogUtils;
import fangpai.cloudabull.com.usblibrary.utils.SystemUtil;
import fangpai.cloudabull.com.usblibrary.bean.UsbObtainBean;
import fangpai.cloudabull.com.usblibrary.database.USBDataBase;
import fangpai.cloudabull.com.usblibrary.listener.IUSBListener;
import fangpai.cloudabull.com.usblibrary.thread.ThreadUtils;
import fangpai.cloudabull.com.usblibrary.utils.GsonUtils;
import fangpai.cloudabull.com.usblibrary.utils.UsbSaveType;
import fangpai.cloudabull.com.usblibrary.utils.UsbUseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.hardware.usb.UsbConstants.USB_DIR_IN;
import static android.hardware.usb.UsbConstants.USB_DIR_OUT;


/**
 * Created by Administrator on 2018/6/28.
 */

public class MyUSBPrinter {

    private static final String ACTION_USB_PERMISSION = "com.usb.printer.USB_PERMISSION";//static
    private static MyUSBPrinter mInstance;
    private Context mContext;//static
    private ArrayList<UsbDevice> mListUSB = new ArrayList<>();//static
    private PendingIntent mPermissionIntent;
    private UsbManager mUsbManager;//static
    private UsbDeviceConnection mUsbDeviceConnection;//static

    public static class UsbDeviceBroadcastReceiver extends BroadcastReceiver {

        public UsbDeviceBroadcastReceiver() {
            super();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        getInstance().mListUSB.add(usbDevice);
                        LogUtils.i("xmUsbDevicemUsbDevice++", "getProductId--->:" + usbDevice.getProductId());
                        LogUtils.i("xmUsbDevicemUsbDevice++", "getVendorId--->:" + usbDevice.getVendorId());
                        LogUtils.i("xmUsbDevicemUsbDevice++", "getInstance().mListUSB:" + getInstance().mListUSB.size());
                        switch (getInstance().usbUseType){
                            case USE_SCAN_PRINTER:
                                getInstance().scanPrinter();
                                break;
                            case USE_ADD_PRINTER:
                                getInstance().addPrinter(getInstance().usbSaveType);
                                break;
                        }
                    } else {
                        if (usbDevice != null) {
                            Toast.makeText(context, "Permission denied for device " + usbDevice, Toast.LENGTH_SHORT).show();
                            LogUtils.i("Return Status", "拒绝访问的设备:" + usbDevice.toString());
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {

                Toast.makeText(context, "Device closed", Toast.LENGTH_SHORT).show();
                LogUtils.i("Return Status", "设备关闭");
                if (getInstance().mUsbDeviceConnection != null) {
                    getInstance().mUsbDeviceConnection.close();
                }
            }
        }
    }

    private MyUSBPrinter() {

    }

    public static MyUSBPrinter getInstance() {
        if (SystemUtil.getDeviceBrand().equals(SystemUtil.SYSTEM_VENDOR)){
            return mInstance = new MyUSBPrinter();
        }else{
            throw new NullPointerException("The Mainboard must be made in FounPad");
        }
    }

    /**
     * 初始化打印机，需要与destroy对应
     *
     * @param context 上下文
     */
    public void initPrinter(Context context, UsbUseType usbUseType) {
        this.usbUseType = usbUseType;
        getInstance().init(context.getApplicationContext());
    }


    /**
     * 初始化
     * 添加打印机（添加打印机专用）
     * @param context 上下文
     */
    public void initPrinter(Context context, UsbUseType usbUseType, UsbSaveType usbSaveType) {
        this.usbUseType = usbUseType;
        this.usbSaveType = usbSaveType;
        getInstance().init(context.getApplicationContext());
    }

    /**
     * *********  发送USB广播  *********
     */
    public void sendBost() {
        Intent intent = new Intent();
        intent.setAction(ACTION_USB_PERMISSION);
        mContext.sendBroadcast(intent);

    }

    private UsbSaveType usbSaveType = UsbSaveType.ADD_ESC_PRINT;//添加的类型，小票/标签
    private UsbUseType usbUseType = UsbUseType.USE_PRINT_PRINTER;


    /**
     * 初始化打印机，发广播
     *
     * @param context
     */
    public void init(Context context) {

        mListUSB = new ArrayList<>();
        mContext = context;
        sendBost();//发送USB广播
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);

        // 列出所有的USB设备，并且都请求获取USB权限
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        for (UsbDevice device : deviceList.values()) {
            mUsbManager.requestPermission(device, mPermissionIntent);
        }

    }

    public void destroy() {
        LogUtils.d("Return Status", "destroy: " + mContext);
        if (mListUSB != null) {
            mListUSB.clear();
        }
        mContext = null;
        mUsbManager = null;
    }

    /**
     * 添加打印机设备
     * @param usbSaveType   打印机类别（小票 or 标签）
     */
    public void addPrinter(final UsbSaveType usbSaveType){
        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (int g = 0; g < mListUSB.size(); g++) {
                    UsbDevice mUsbDevice = mListUSB.get(g);
                    if (mUsbDevice != null) {
                        UsbInterface usbInterface = mUsbDevice.getInterface(0);
                        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                            final UsbEndpoint ep = usbInterface.getEndpoint(i);
                            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && ep.getMaxPacketSize() < 512) {
                                if (ep.getDirection() == USB_DIR_OUT && SystemUtil.getDeviceBrand().equals(SystemUtil.SYSTEM_VENDOR)) {
                                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                                    if (mUsbDeviceConnection != null) {
                                        mUsbDeviceConnection.claimInterface(usbInterface, true);
                                        LogUtils.d("mUsbDevicemUsbDevice1-", mUsbDevice.getVendorId() + "," + mUsbDevice.getProductId());
                                        UsbObtainBean.UsbDataBean newUsbDataBean = new UsbObtainBean.UsbDataBean(mUsbDevice.getVendorId(), mUsbDevice.getProductId());
                                        String s = GsonUtils.toJsonString(newUsbDataBean);
                                        UsbObtainBean usb_esc_tsc = USBDataBase.getUSB_ESC_TSC(mContext);
                                        List<UsbObtainBean.UsbDataBean> escList = usb_esc_tsc.getEscList();
                                        List<UsbObtainBean.UsbDataBean> tscList = usb_esc_tsc.getTscList();

                                        List<UsbObtainBean.UsbDataBean> sumList = new ArrayList<>();
                                        sumList.addAll(escList);
                                        sumList.addAll(tscList);

                                        for (int k = 0; k < sumList.size(); k++) {
                                            UsbObtainBean.UsbDataBean usbDataBean = sumList.get(k);
                                            if (newUsbDataBean.equals(usbDataBean)) {
                                                break;//判断存在结束当前循环
                                            } else {
                                                if (k == sumList.size() - 1) {
                                                    switch (usbSaveType){
                                                        case ADD_ESC_PRINT:
                                                            escList.add(newUsbDataBean);
                                                            break;
                                                        case ADD_TSC_PRINT:
                                                            tscList.add(newUsbDataBean);
                                                            break;
                                                    }
                                                    USBDataBase.setUSB_ESC_TSC(mContext, GsonUtils.toJsonString(usb_esc_tsc));
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        if (mUsbDeviceConnection != null && g == mListUSB.size() - 1) {
                            mUsbDeviceConnection.releaseInterface(usbInterface);
                        }
                    } else {

                    }
                }//循环
            }
        });
    }

    /**
     * 扫描USB打印机  vid值,用于判断当前打印机的VID和PID是否在列表中
     */
    public void scanPrinter(){
        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                for (int g = 0; g < mListUSB.size(); g++) {
                    UsbDevice mUsbDevice = mListUSB.get(g);
                    if (mUsbDevice != null) {
                        UsbInterface usbInterface = mUsbDevice.getInterface(0);
                        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                            final UsbEndpoint ep = usbInterface.getEndpoint(i);
                            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && ep.getMaxPacketSize() < 512) {
                                if (ep.getDirection() == USB_DIR_OUT && SystemUtil.getDeviceBrand().equals(SystemUtil.SYSTEM_VENDOR)) {
                                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                                    if (mUsbDeviceConnection != null) {
                                        mUsbDeviceConnection.claimInterface(usbInterface, true);
                                        LogUtils.d("mUsbDevicemUsbDevice1", mUsbDevice.getVendorId() + "," + mUsbDevice.getProductId());
                                        final UsbObtainBean.UsbDataBean newUsbDataBean = new UsbObtainBean.UsbDataBean(mUsbDevice.getVendorId(), mUsbDevice.getProductId());

                                        UsbObtainBean usb_esc_tsc = USBDataBase.getUSB_ESC_TSC(mContext);

                                        List<UsbObtainBean.UsbDataBean> escList = usb_esc_tsc.getEscList();
                                        List<UsbObtainBean.UsbDataBean> tscList = usb_esc_tsc.getTscList();

                                        List<UsbObtainBean.UsbDataBean> sumList = new ArrayList<>();
                                        sumList.addAll(escList);
                                        sumList.addAll(tscList);

                                        for (int k = 0; k < sumList.size(); k++) {
                                            UsbObtainBean.UsbDataBean usbDataBean = sumList.get(k);
                                            if (newUsbDataBean.equals(usbDataBean)) {
                                                //该设备的VID值存在
                                                break;//判断存在结束当前循环
                                            } else {
                                                if (k == sumList.size() - 1) {
                                                    //该设备的VID值不存在
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mContext,"VID = "+newUsbDataBean.getVid()+",PID = "+newUsbDataBean.getPid()+"的设备连接失败，请到设置界面设置打印机",Toast.LENGTH_LONG).show();
                                                            LogUtils.d("mUsbDevicemUsbDevice1", "VID = "+newUsbDataBean.getVid()+",PID = "+newUsbDataBean.getPid()+"的设备连接失败，请到设置界面设置打印机");

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                    }
                                }
                            } else {
                                LogUtils.i("mUsbDevicemUsbDevice0", "g="+g+"  ------m="+(mListUSB.size() - 1));
                                LogUtils.i("mUsbDevicemUsbDevice0", "i="+i+"  ------e="+(usbInterface.getEndpointCount() - 1));
                                if (g == mListUSB.size() - 1 && i == usbInterface.getEndpointCount() - 1) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            initPrinter(mContext,UsbUseType.USE_PRINT_PRINTER);
                                        }
                                    });
                                }
                            }
                        }
                        if (mUsbDeviceConnection != null && g == mListUSB.size() - 1) {
                            mUsbDeviceConnection.releaseInterface(usbInterface);
                        }
                    } else {

                    }
                }//循环
            }
        });
    }

    /**
     * 打印
     * @param check_bytes     检测byte[]
     * @param esc_open        是否打开小票打印
     * @param esc_bytes       小票打印数据（即byte[]）
     * @param ticket_num      小票打印数量
     * @param tsc_open        是否打印标签
     * @param tsc_byteDatas   标签打印数据
     * @param listener        监听打印是否成功
     */
    public void print(final byte[] check_bytes, final boolean esc_open, final byte[] esc_bytes, final int ticket_num,
                      final boolean tsc_open, final byte[][] tsc_byteDatas, final IUSBListener listener){
        ThreadUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                fei:
                for (int g = 0; g < mListUSB.size(); g++) {
                    UsbDevice mUsbDevice = mListUSB.get(g);
                    LogUtils.d("USB设备", "2VendorId = " + mUsbDevice.getVendorId());
                    LogUtils.d("USB设备", "2ProductId = " + mUsbDevice.getProductId());

                    if (mUsbDevice != null) {
                        int interfaceCount = mUsbDevice.getInterfaceCount();
                        LogUtils.d("Return Status", "interfaceCount: " + interfaceCount);

                        UsbInterface usbInterface = mUsbDevice.getInterface(0);//原

                        LogUtils.d("Return Status", "mUsbDevice: " + mUsbDevice.toString());
                        LogUtils.d("Return Status", "usbInterface: " + usbInterface.toString());
                        LogUtils.d("Return Status", "number: " + usbInterface.getEndpointCount());

                        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                            final UsbEndpoint ep = usbInterface.getEndpoint(i);
                            LogUtils.d("Return Status1", "----- " + i + " ----->: " + ep.getMaxPacketSize());
                            LogUtils.d("Return Status2", "----- " + i + " ----->: " + ep.getType());
                            LogUtils.d("Return Status3", "----- " + i + " ----->: " + UsbConstants.USB_ENDPOINT_XFER_BULK);
                            LogUtils.d("Return Status4", "----- " + i + " ----->: " + ep.getDirection());
                            LogUtils.d("Return Status5", "----- " + i + " ----->: " + USB_DIR_OUT);
                            LogUtils.d("Return Status6", "----- " + i + " ----->: " + USB_DIR_IN);
                            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && ep.getMaxPacketSize() < 512) {//&& ep.getMaxPacketSize() == 64
                                LogUtils.d("USB设备", "getMaxPacketSize----- " + i + " ----->: " + ep.getMaxPacketSize());
                                if (ep.getDirection() == USB_DIR_OUT && SystemUtil.getDeviceBrand().equals(SystemUtil.SYSTEM_VENDOR)) {
                                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                                    LogUtils.d("USB设备", i + "---" + "3VendorId = " + mUsbDevice.getVendorId());
                                    LogUtils.d("USB设备", i + "---" + "3ProductId = " + mUsbDevice.getProductId());
                                    if (mUsbDeviceConnection != null) {
                                        mUsbDeviceConnection.claimInterface(usbInterface, true);
                                        LogUtils.i("Return Status", "设备已连接");

                                        UsbObtainBean usb_esc_tsc = USBDataBase.getUSB_ESC_TSC(mContext);
                                        List<UsbObtainBean.UsbDataBean> escList = usb_esc_tsc.getEscList();
                                        List<UsbObtainBean.UsbDataBean> tscList = usb_esc_tsc.getTscList();
                                        UsbObtainBean.UsbDataBean newUsbDataBean = new UsbObtainBean.UsbDataBean(mUsbDevice.getVendorId(), mUsbDevice.getProductId());
                                        if (esc_open) {
                                            //小票打印
                                            for (int k = 0; k < escList.size(); k++) {
                                                UsbObtainBean.UsbDataBean usbDataBean = escList.get(k);
                                                if (usbDataBean.equals(newUsbDataBean)) {

                                                    LogUtils.i("Return Status", "b-->ep----->" + ep.toString());
                                                    int isB = 0;
//                                                    byte[] check = PrinterCmdUtils.transfer();
                                                    int transfer = mUsbDeviceConnection.bulkTransfer(ep, check_bytes, check_bytes.length, 100000);
                                                    LogUtils.d("transfertransfer", "run: transfer=====>>>>> " + transfer);
                                                    for (int j = 0; j < ticket_num; j++) {
                                                        int b = mUsbDeviceConnection.bulkTransfer(ep, esc_bytes, esc_bytes.length, 100000);
                                                        LogUtils.d("ReturnStatus", "run: 小票=====>>>>> " + b);
                                                        isB = b;
                                                    }
                                                    listener.onUsbListener(isB + "");
                                                }
                                            }
                                        }
                                        if (tsc_open) {
                                            //标签打印
                                            for (int a = 0; a < tscList.size(); a++) {
                                                UsbObtainBean.UsbDataBean usbDataBean = tscList.get(a);
                                                if (usbDataBean.equals(newUsbDataBean)) {
                                                    for (int j = 0; j < tsc_byteDatas.length; j++) {
                                                        byte[] tsc_bytes = tsc_byteDatas[j];
                                                        int b = mUsbDeviceConnection.bulkTransfer(ep, tsc_bytes, tsc_bytes.length, 100000);
                                                        LogUtils.d("ReturnStatus", "run: 标签=====>>>>> " + b);
                                                    }

                                                }
                                            }
                                        }

                                        if (mUsbDeviceConnection != null && g == mListUSB.size() - 1) {
                                            mUsbDeviceConnection.releaseInterface(usbInterface);
                                        }
                                        LogUtils.i("Return Status", "b-->小票打印方法3");
//                                        break fei;
                                    }
                                }
                            } else {
                                if (g == mListUSB.size() - 1 && i == usbInterface.getEndpointCount() - 1) {
                                    listener.onUsbListener("-1");
                                    LogUtils.i("USB设备", "漏打2");
                                }
                            }
                        }

                    } else {
                        LogUtils.i("Return Status", "没有USB打印设备");
                    }

                }//测试
            }
        });
    }





}
