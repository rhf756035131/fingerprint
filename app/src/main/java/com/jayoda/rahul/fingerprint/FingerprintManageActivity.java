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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FingerprintManageActivity extends AppCompatActivity implements BlueToothMsg.Callback{

    private BlueToothMsg BTService;
    private BlueToothMsg.MsgBinder myBinder;
    private  int count=0;
    private  final int maxCount=2; //采集３次
    private  int InputFingerPrintCount=0;
    private  final int InputFingerPrintCountMax=10;
    public static final String TAG = "Fingerprint.BTService";
    private ImageView Scanning_anim;
    private Button Scan_left, Scan_right;
    private TextView Scan_prompt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manage);

        Scan_left = (Button) findViewById(R.id.scan_left);
        Scan_left.setOnClickListener(new BClickListener());

        Scan_right = (Button) findViewById(R.id.scan_right);
        Scan_right.setOnClickListener(new BClickListener());

        Scan_prompt=(TextView)findViewById(R.id.prompt_scan);
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
            Log.d(TAG, "FingerprintManageActivity BTService= "+BTService.toString());
            BTService.setCallback(FingerprintManageActivity.this);
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
        switch (data[5]) {
            case BT_command.cmd_collect_finger://终端返回采集图像命令
                if (data[6] == 0x00) {
                    Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.release_finger));
                    //把检查到的指纹生成特征，保存到ramBuffer中
                    byte[] buffer=new byte[]{(byte)count};
                    myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram,buffer);
                }else{
                    if(InputFingerPrintCount<InputFingerPrintCountMax){
                        InputFingerPrintCount++;
                        myBinder.sendCommand(BT_command.cmd_collect_finger);
                    }else{
                        Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                                + getResources().getString(R.string.fail));
                        Scan_left.setEnabled(true);
                        Scan_right.setEnabled(false);
                    }
                }
                break;
            case BT_command.cmd_fingerTemp_push_ram://终端返回保存指纹特征到ramBuffer中的命令
                if (data[6] == 0x00) {
                    if (count < maxCount) {
                        //保存成功,保存三次后，生成指纹id并存储起来
                        count++;
                        myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
                    }
                }else {
                    Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.fail));
                    Scan_left.setEnabled(true);
                    Scan_right.setEnabled(false);
                }
                break;
            case BT_command.cmd_fingerTemp_merge:
                Scan_left.setEnabled(true);
                Scan_right.setEnabled(true);
                if (data[6] == 0x00) {
                    Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.success));
                    if(count<maxCount){
                        Scan_left.setText(R.string.again);
                        Scan_right.setText(R.string.continuation);
                    }else {
                        Scan_left.setEnabled(false);
                        Scan_right.setText(R.string.done);
                    }
                }else{
                    Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.fail));
                    count--;
                }
                break;
            default:
                break;
        }

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(FingerprintManageActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onStart() {
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        Log.d(TAG, "unbindService");
        super.onDestroy();
    }
    class BClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String ButtonText=((Button)v).getText().toString();
            switch (v.getId()) {
                case R.id.scan_left:
                    if(getResources().getString(R.string.cancel).equals(ButtonText)){
                        finish();
                    } else {
                        count--;
                        myBinder.sendCommand(BT_command.cmd_collect_finger);
                        Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                                + getResources().getString(R.string.press_finger));
                        InputFingerPrintCount = 0;
                        Scan_left.setEnabled(false);
                        Scan_right.setEnabled(false);
                    }
                    break;
                case R.id.scan_right:
                    if(getResources().getString(R.string.done).equals(ButtonText)){
                        finish();
                    }else{
                        myBinder.sendCommand(BT_command.cmd_collect_finger);
                        Scan_prompt.setText(String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                                + getResources().getString(R.string.press_finger));
                        InputFingerPrintCount=0;
                        Scan_left.setEnabled(false);
                        Scan_right.setEnabled(false);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
