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
import android.view.Menu;
import android.view.MenuItem;
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
            BTService=null;
            myBinder=null;
            Log.d(TAG, "------onServiceDisconnected---------");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "------onServiceConnected---------");
            myBinder = (BlueToothMsg.MsgBinder) service;
            BTService = myBinder.getService();
            Log.d(TAG, "MainActivity BTService="+BTService.toString());
            BTService.setCallback(MainActivity.this);
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
        Message msg = new Message();
        switch (data[5]) {
            case BT_command.cmd_del_finger:
                if (data[6] == 0x00) {
                    Log.d(TAG, "删除指纹成功");
                    msg.obj = "成功删除指纹";
                }else {
                    Log.d(TAG, "删除指纹失败");
                    msg.obj = "删除指纹失败";
                }
                break;
            default:
                Log.d(TAG, "指令错误");

                break;
        }
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "接收到handleMessage");
            Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_user:
                Intent FMin=new Intent(MainActivity.this,FingerprintManageActivity.class);
                startActivity(FMin);
                break;
            case R.id.action_data:
                Intent ReportIn=new Intent(MainActivity.this,ReportActivity.class);
                startActivity(ReportIn);
                break;
            case R.id.action_time:
                Intent tmset=new Intent(MainActivity.this, DeviceTimeSetting.class);
                startActivity(tmset);
                break;
            case R.id.action_del:
                myBinder.sendCommand(BT_command.cmd_del_finger);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
    }
    class BClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                default:
                    break;
            }
        }
    }
}
