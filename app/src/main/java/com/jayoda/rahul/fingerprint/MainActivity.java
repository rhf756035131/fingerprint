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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button B_all_user, B_all_date;
    static BlueToothMsg.MsgBinder myBinder;
    static byte[] revice_closestool_date = new byte[256];

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.d(BlueToothMsg.TAG, "------onServiceDisconnected---------");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(BlueToothMsg.TAG, "------onServiceConnected---------");
            myBinder = (BlueToothMsg.MsgBinder) service;
            BlueToothMsg myService = myBinder.getService();
            myService.setCallback(new BlueToothMsg.Callback() {
                @Override
                public void onDataChange(String data) {
                    Message msg = new Message();
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
                @Override
                public void onDataChange(byte[] data) {
                    System.arraycopy(data,0,revice_closestool_date,0,256);
                }
            });
            myBinder.connectbluetooth();
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        B_all_user =(Button)findViewById(R.id.user_manage);
        B_all_date =(Button)findViewById(R.id.all_date);
        B_all_user.setOnClickListener(new BClickListener());
        B_all_date.setOnClickListener(new BClickListener());
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
    }
    class BClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.user_manage:
                    Intent FMin=new Intent(MainActivity.this,FingerprintManageActivity.class);
                    startActivity(FMin);
                    break;
                case R.id.all_date:
                    Intent peportin=new Intent(MainActivity.this,ReportActivity.class);
                    startActivity(peportin);
                    break;
                default:
                    break;
            }
        }
    }
}
