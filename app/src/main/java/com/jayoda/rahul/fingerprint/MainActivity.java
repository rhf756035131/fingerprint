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

public class MainActivity extends AppCompatActivity implements BlueToothMsg.Callback{

    private Button B_all_user, B_all_date;
    private BlueToothMsg BTService;
    private BlueToothMsg.MsgBinder myBinder;
    private final String TAG = "Main.BTService";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        Log.d(TAG, "unbindService");
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
            BTService.setCallback(MainActivity.this);
            myBinder.ConnectBluetooth();

        }
    };
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
