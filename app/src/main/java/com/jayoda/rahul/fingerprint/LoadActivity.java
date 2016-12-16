package com.jayoda.rahul.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class LoadActivity extends AppCompatActivity implements BlueToothMsg.Callback{

    private BlueToothMsg BTService;
    private BlueToothMsg.MsgBinder myBinder;
    private TextView LoadText;
    private final String TAG = "LoadActivity.BTService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        LoadText=(TextView)findViewById(R.id.LoadText);
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
    }
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            BTService = null;
            Log.d(TAG, "------onServiceDisconnected---------");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "------onServiceConnected---------");
            myBinder = (BlueToothMsg.MsgBinder) service;
            BTService = myBinder.getService();
            Log.d(TAG, "MainActivity BTService="+BTService.toString());
            BTService.setCallback(LoadActivity.this);
            myBinder.ConnectBluetooth();

        }
    };

    @Override
    protected void onDestroy() {
        unbindService(conn);
        Log.d(TAG, "unbindService");
        super.onDestroy();
    }

    @Override
    public void onDataChange(String data) {
        Message msg = new Message();
        msg.obj = data;
        handler.sendMessage(msg);
    }
    @Override
    public void onDataChange(byte[] data) {
        Log.d(TAG, "------received---------");
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if("已经连接上马桶！可以发送命令。".equals(msg.obj.toString())){
                Intent MainIn=new Intent(LoadActivity.this,MainActivity.class);
                startActivity(MainIn);
            }else if("连接马桶异常！断开连接重新试一试。".equals(msg.obj.toString())) {
                LoadText.setText(R.string.fail);
            }else{
                    Toast.makeText(LoadActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                }
            }
    };

}
