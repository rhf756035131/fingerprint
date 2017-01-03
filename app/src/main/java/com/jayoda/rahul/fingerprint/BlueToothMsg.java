package com.jayoda.rahul.fingerprint;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by rahul on 16-12-6.
 */


public class BlueToothMsg extends Service {

    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private readThread mreadThread = null;
    private clientThread clientConnectThread = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private byte[] revice_date = new byte[256];
    private int revice_date_length = 0;
    private byte command = 0x00;

    private MsgBinder m_Binder = new MsgBinder();
    private Callback callback;
    private final String TAG = "BTService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "BlueToothMsg onCreate");
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "BlueToothMsg onBind");
        return m_Binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //开启客户端
    private class clientThread extends Thread {
        @Override
        public void run() {
            try {
                //创建一个Socket连接：只需要服务器在注册时的UUID号
                Log.d(TAG, "clientThread");
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                callback.onDataChange("请稍候，正在连接马桶:" + BlueToothinfo.BlueToothAddress);
                socket.connect();
                callback.onDataChange("已经连接上马桶！可以发送命令。");
                //启动接受数据
                mreadThread = new readThread();
                mreadThread.start();
            } catch (IOException e) {
                Log.e("connect", "", e);
                callback.onDataChange("连接马桶异常！断开连接重新试一试。");
            }
        }
    }

    //发送数据
    private void sendMessageHandle(byte[] msg) {
        if (socket == null) {
            // Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream os = socket.getOutputStream();
            os.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    final protected static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    //读取数据
    private class readThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[100];
            int bytes;
            InputStream mmInStream = null;

            try {
                mmInStream = socket.getInputStream();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (true) {
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                            add_revice_date(buffer[i]);
                        }
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
                Log.d(TAG, "接收数据叠加=" + byteArrayToHexString(revice_date));
                if (checkHead(getRevice_date())&&checkdata(getRevice_date())) {
                    Log.d(TAG, "接收数据完成=" + byteArrayToHexString(revice_date));
                    callback.onDataChange(revice_date);
                    Log.d(TAG, "清空数据");
                    del_revice_date();
                }
            }
        }
    }

    private byte getCommand() {
        //Log.d(TAG, "command=" + command);
        return command;
    }

    private void setCommand(byte cmd) {
        command = cmd;
    }

    private void add_revice_date(byte buf_data) {
        revice_date[revice_date_length] = buf_data;
        revice_date_length++;

    }

    private void del_revice_date() {
        Arrays.fill(revice_date, (byte) 0);
        revice_date_length = 0;
    }

    private byte[] getRevice_date() {
        return revice_date;
    }

    private int getRevice_date_length() {
        return revice_date_length;
    }

    private void analyse_cmd(byte[] buf_data, byte[] packages) {
        for (int i = 0; i < revice_date_length - 4; i++) {
            if ((buf_data[i] == (byte) 0x93) && (buf_data[i + 1] == (byte) 0x8e) && (buf_data[i + 3] == (byte) 0x00) && (buf_data[i + 4] == (byte) 0x08)) {
                for (int j = 0; j < (buf_data[i + 2] + 3); j++) {
                    packages[j] = buf_data[i + j];
                }

            }

        }

    }

    private boolean checkdata(byte[] buf_data) {
        int mun = 0;
        int i ;
        for (i = 2; i < (int) buf_data[2] + 2; i++) {
            mun = mun + buf_data[i] & 0xff;
        }
        return ((byte) mun == buf_data[buf_data[2] + 2]);
    }
    private boolean checkHead(byte[] buf_data) {
        if((BT_command.header[0]==buf_data[0])&&(BT_command.header[1]==buf_data[1])&&(buf_data[2]!=0)){
            return true;
        }
        return false;
    }
    public class MsgBinder extends Binder {
        public void ConnectBluetooth() {
            String address = BlueToothinfo.BlueToothAddress;
            if (!address.equals("null")) {
                device = mBluetoothAdapter.getRemoteDevice(address);
                clientConnectThread = new clientThread();
                clientConnectThread.start();
            } else {
                callback.onDataChange("address is null");
            }
        }

        public void sendCommand(byte command) {
            byte[] buffer = new byte[7];
            buffer[0] = BT_command.header[0];
            buffer[1] = BT_command.header[1];
            buffer[2] = 0x04;
            buffer[3] = BT_command.machine_code[0];
            buffer[4] = BT_command.machine_code[1];
            buffer[5] = command;
            buffer[6] = (byte) (buffer[2] + buffer[3] + buffer[4] + buffer[5]);
            //callback.onDataChange(String.valueOf(buffer[6]));
            del_revice_date();
            setCommand(command);
            Log.d(TAG, "发送指令=" + byteArrayToHexString(buffer));
            sendMessageHandle(buffer);

        }

        public void sendCommand(byte command, byte[] args) {
            byte[] buffer = new byte[7 + args.length];
            byte checksum = 0;
            buffer[0] = BT_command.header[0];
            buffer[1] = BT_command.header[1];
            buffer[2] = (byte) (args.length + 4);
            buffer[3] = BT_command.machine_code[0];
            buffer[4] = BT_command.machine_code[1];
            buffer[5] = command;
            checksum = (byte) (buffer[2] + buffer[3] + buffer[4] + buffer[5]);
            for (int i = 0; i < args.length; i++) {
                checksum = (byte) (checksum + args[i]);
                buffer[i + 6] = args[i];
            }
            buffer[6 + args.length] = checksum;
            del_revice_date();
            setCommand(command);
            sendMessageHandle(buffer);
            Log.d(TAG, "发送指令=" + byteArrayToHexString(buffer));
            //callback.onDataChange(String.valueOf(buffer[6+args.length]));
        }

        public BlueToothMsg getService() {
            return BlueToothMsg.this;
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onDataChange(String data);
        void onDataChange(byte[] data);
    }
}
