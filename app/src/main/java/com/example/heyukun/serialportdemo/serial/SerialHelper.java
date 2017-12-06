package com.example.heyukun.serialportdemo.serial;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;


/**
 * Created by hyk on 2017/12/5.
 */

public class SerialHelper {
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private static String sPort = "/dev/ttyS4";
    private int iBaudRate = 115200;

    private SerialRevCallBack mSerialRevCallBack;

    private StringBuilder receiveSB;
    //正转
    private static final String FWD = "CC 07 7C 01 00 00 00 01";
    //反转
    private static final String REV = "CC 07 7C 02 00 00 00 01";
    //停转
    private static final String STOP = "CC 07 7C 03 00 00 00 01";
    //控制灯
    private static final String LED = "CC 07 7C 04 00 ";

    private static final String CRASH_STR = "77C0C701";
    private static final String INFRARED_STR = "77C0C704";


    SerialHelper() {
        try {
            mSerialPort = new SerialPort(new File(sPort), iBaudRate, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        receiveSB = new StringBuilder("");
        if (mReadThread == null) {
            mReadThread = new ReadThread();
        }
        if (!mReadThread.isAlive()) {
            mReadThread.start();
        }
    }

    private static final class SerialHelperHolder {
        private static final SerialHelper INSTANCE = new SerialHelper();
    }

    public static SerialHelper getInstance() {
        return SerialHelperHolder.INSTANCE;
    }

    //监听返回数据
    public void setOnSerialRevListener(SerialRevCallBack serialRevListener) {
        this.mSerialRevCallBack = serialRevListener;
    }


    /**
     * 读串口线程
     */
    class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mInputStream != null) {
                    byte[] buffer = new byte[512];
                    int size = 0;
                    try {
                        size = mInputStream.read(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (size > 0) {
                        byte[] buffer2 = new byte[size];
                        for (int i = 0; i < size; i++) {
                            buffer2[i] = buffer[i];
                        }

                        receiveSB.append(SerialDataUtils.ByteArrToHex(buffer2).trim().replaceAll(" ", ""));
                        Log.d("Serial=", receiveSB.toString());
                        if (mSerialRevCallBack != null) {
                            RevType type = RevType.NONE;
                            if (receiveSB.toString().contains(CRASH_STR)) {
                                type = RevType.CRASH;
                            } else if (receiveSB.toString().contains(INFRARED_STR)) {
                                type = RevType.INFRARED;
                            }
                            mSerialRevCallBack.OnSuccess(type);
                        }
                        receiveSB.delete(0, receiveSB.length() - 1);
                    }
                    try {
                        //延时50ms
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
    }


    /**
     * 发串口数据
     */
    private void send(final String string) {
        try {
            //去掉空格
            String s = string;
            s = s.replace(" ", "");
            byte[] bytes = SerialDataUtils.HexToByteArr(s);
            mOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 反向转动
     */
    public void turnBack() {
        send(REV);
    }

    /**
     * 正转
     */
    public void turnForward() {
        send(FWD);
    }

    /**
     * 停止转动
     */
    public void turnStop() {
        send(STOP);
    }

    /**
     * 改变Led灯的颜色
     *
     * @param ledColor 颜色
     */
    public void changeLedColor(LedColor ledColor) {
        StringBuilder stringBuilder = new StringBuilder(LED);
        switch (ledColor) {
            case BLACK:
                stringBuilder.append("00 00 00 01");
                break;
            case RED:
                stringBuilder.append("ff 00 00 01");
                break;
            case GREEN:
                stringBuilder.append("00 ff 00 01");
                break;
            case BLUE:
                stringBuilder.append("00 00 ff 01");
                break;
            case YELLOW:
                stringBuilder.append("ff ff 00 01");
                break;
            case PURPLE:
                stringBuilder.append("ff 00 ff 01");
                break;
            case CYAN:
                stringBuilder.append("00 ff ff 01");
                break;
            case WHITE:
                stringBuilder.append("ff ff ff 01");
                break;
            default:
                break;
        }
        send(stringBuilder.toString());
    }


    public enum LedColor {
        BLACK,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        PURPLE,
        CYAN,
        WHITE
    }
}
