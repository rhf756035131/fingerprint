package com.jayoda.rahul.fingerprint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FingerprintManageActivity extends AppCompatActivity {

    private BlueToothMsg myService = MainActivity.myBinder.getService();
    private  int conut=0;
    private  final int maxcount=2; //采集３次
    private  boolean isInputFingerPrint=false;
    public static final String TAG = "BlueToothService";
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

        myService.setalldateCallback(new BlueToothMsg.AllDateCallback() {
            @Override
            public void onChangeAllDate(byte[] data) {
                switch (data[5]) {
                    case BT_command.cmd_collect_finger:
                        if (data[6] == 0x00) {
                            setIsInputFingerPrint(true);
                            MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram);
                        }
                        break;
                    case BT_command.cmd_fingerTemp_push_ram:
                        if (data[6] == 0x00) {
                            if (conut >= maxcount) {
                                MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
                                conut++;
                            }
                        } else {

                        }
                    case BT_command.cmd_fingerTemp_merge:
                        if (data[6] == 0x00) {

                        }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        setIsInputFingerPrint(false);
        fingerThread mfingerThread = new fingerThread();
        mfingerThread.start();
        super.onStart();
    }
    private void setIsInputFingerPrint(boolean isinput){
        isInputFingerPrint=isinput;
    }
    private boolean getIsInputFingerPrint( ){
        return isInputFingerPrint;
    }
    private class fingerThread extends Thread{
        @Override
        public void run() {
            while (conut<3) {
                while (!getIsInputFingerPrint()) {
                    MainActivity.myBinder.sendCommand(BT_command.cmd_collect_finger);
                    try{
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "conut="+conut);
                setIsInputFingerPrint(false);
                MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram);
                try{
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                conut++;
            }
            MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
            try{
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_push_datebase);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    class BClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String ButtonText=((Button)v).getText().toString();
            switch (v.getId()) {
                case R.id.scan_left:
                    if(getResources().getString(R.string.cancel).equals(ButtonText)){
                        Log.d(TAG, "string equals");
                    }else{
                        Log.d(TAG, "string no equals");
                    }
                    break;
                case R.id.scan_right:
                    if(getResources().getString(R.string.start).equals(ButtonText)){
                        Log.d(TAG, "string equals");
                    }else if(getResources().getString(R.string.continuation).equals(ButtonText)){

                    }else{
                        Log.d(TAG, "string no equals");
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
