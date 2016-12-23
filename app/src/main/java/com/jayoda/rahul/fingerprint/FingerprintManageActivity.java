package com.jayoda.rahul.fingerprint;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private  final int ChangeUI=2;
    private  final int GetIDSuccess=3;
    private  final int GetIDFail=4;
    private String username;
    private  ProgressDialog proDia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manage);

        Scan_left = (Button) findViewById(R.id.scan_left);
        Scan_left.setOnClickListener(new BClickListener());

        Scan_right = (Button) findViewById(R.id.scan_right);
        Scan_right.setOnClickListener(new BClickListener());

        Scan_prompt=(TextView)findViewById(R.id.prompt_scan);
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
       // proDia.setTitle("搜索网络");
        proDia=new ProgressDialog(FingerprintManageActivity.this);
        proDia.setMessage("请等待....");
        proDia.onStart();
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
            myBinder.sendCommand(BT_command.cmd_get_ID);
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
        HandlerMassage mHandlerMassage=new HandlerMassage();
        Message msg = new Message();
        switch (data[5]) {
            case BT_command.cmd_collect_finger://终端返回采集图像命令
                if (data[6] == 0x00) {
                    mHandlerMassage.pomprtText=String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.release_finger);
                    //把检查到的指纹生成特征，保存到ramBuffer中
                    byte[] buffer=new byte[]{(byte)count};
                    myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram,buffer);
                    Log.d(TAG, "采集到指纹 count="+count);
                }else{
                    if(InputFingerPrintCount<InputFingerPrintCountMax){
                        InputFingerPrintCount++;
                        myBinder.sendCommand(BT_command.cmd_collect_finger);
                    }else{
                        mHandlerMassage.pomprtText=String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                                + getResources().getString(R.string.fail);
                        mHandlerMassage.leftEnable=1;
                        mHandlerMassage.rightEnable=-1;
                        //Scan_left.setEnabled(true);
                        //Scan_right.setEnabled(false);
                    }
                }
                msg.what=ChangeUI;
                msg.obj = mHandlerMassage;
                break;
            case BT_command.cmd_fingerTemp_push_ram://终端返回保存指纹特征到ramBuffer中的命令
                if (data[6] == 0x00) {
                    if (count < maxCount) {
                        //保存成功,保存三次后，生成指纹id并存储起来
                        count++;
                        myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
                    }
                }else {
                    mHandlerMassage.pomprtText=String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.fail);
                    mHandlerMassage.leftEnable=1;
                    mHandlerMassage.rightEnable=-1;
                    //Scan_left.setEnabled(true);
                    //Scan_right.setEnabled(false);
                }
                msg.what=ChangeUI;
                msg.obj = mHandlerMassage;
                break;
            case BT_command.cmd_fingerTemp_merge:
                mHandlerMassage.leftEnable=1;
                mHandlerMassage.rightEnable=1;
                //Scan_left.setEnabled(true);
                //Scan_right.setEnabled(true);
                if (data[6] == 0x00) {
                    mHandlerMassage.pomprtText=String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.success);
                    if(count<maxCount){
                        mHandlerMassage.leftText=getResources().getString(R.string.again);
                        mHandlerMassage.rightText=getResources().getString(R.string.continuation);
                        //Scan_left.setText(R.string.again);
                       // Scan_right.setText(R.string.continuation);
                    }else {
                        myBinder.sendCommand(BT_command.cmd_device_ack);
                        mHandlerMassage.leftEnable=-1;
                        mHandlerMassage.rightText=getResources().getString(R.string.done);
                        //Scan_left.setEnabled(false);
                        //Scan_right.setText(R.string.done);
                    }
                }else{
                    mHandlerMassage.pomprtText=String.format(getResources().getString(R.string.fingerprint_progress), count + 1, maxCount + 1)
                            + getResources().getString(R.string.fail);
                    count--;
                }
                msg.what=ChangeUI;
                msg.obj = mHandlerMassage;
                break;
            case BT_command.cmd_get_ID:
                if (data[6] == 0x00) {
                    msg.what=GetIDSuccess;
                }else{
                    msg.what=GetIDFail;
                }
                    break;
            default:
                break;
        }
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ChangeUI:
                    HandlerMassage catchHandlerMassage = (HandlerMassage) msg.obj;
                    if (!"null".equals(catchHandlerMassage.pomprtText)) {
                        Scan_prompt.setText(catchHandlerMassage.pomprtText);
                    }
                    if (!"null".equals(catchHandlerMassage.leftText)) {
                        Scan_left.setText(catchHandlerMassage.leftText);
                    }
                    if (!"null".equals(catchHandlerMassage.rightText)) {
                        Scan_right.setText(catchHandlerMassage.rightText);
                    }
                    if(-1==catchHandlerMassage.leftEnable){
                        Scan_left.setEnabled(false);
                    }
                    if(1==catchHandlerMassage.leftEnable){
                        Scan_left.setEnabled(true);
                    }
                    if(-1==catchHandlerMassage.rightEnable){
                        Scan_right.setEnabled(false);
                    }
                    if(1==catchHandlerMassage.rightEnable){
                        Scan_right.setEnabled(true);
                    }
                    break;
                case GetIDSuccess:
                    final EditText et = new EditText(FingerprintManageActivity.this);
                    new AlertDialog.Builder(FingerprintManageActivity.this).setTitle("请输入名字").setIcon(
                            android.R.drawable.ic_dialog_info).setView(et).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //获取名字
                            username=et.getText().toString().trim();
                        }
                    }).setNegativeButton("取消", null).show();
                    proDia.dismiss();
                    break;
                case GetIDFail:
                    new AlertDialog.Builder(FingerprintManageActivity.this).setTitle("获取ID失败").setIcon(
                            android.R.drawable.ic_dialog_info).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //退出指纹录入
                            finish();
                        }
                    }).show();
                    proDia.dismiss();
                    break;
                default:
                    Toast.makeText(FingerprintManageActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    @Override
    protected void onStart() {

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
    class HandlerMassage{
       public String  pomprtText="null";
        public String leftText="null";
        public String rightText="null";
        public int rightEnable=0;
        public int leftEnable=0;
    }
}
