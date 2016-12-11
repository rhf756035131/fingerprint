package com.jayoda.rahul.fingerprint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class FingerprintManageActivity extends AppCompatActivity {

    private BlueToothMsg myService = MainActivity.myBinder.getService();
    private  int conut=0;
    private  boolean isInputFingerPrint=false;
    public static final String TAG = "BlueToothService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manage);
        myService.setalldateCallback(new BlueToothMsg.AllDateCallback() {
            @Override
            public void onChangeAllDate(byte[] data) {
                if(data[6]==0x00&&data[5]==BT_command.cmd_collect_finger) {
                    setIsInputFingerPrint(true);
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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "conut="+conut);
                setIsInputFingerPrint(false);
                MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram);
                try{
                    sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                conut++;
            }
            MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
            try{
                sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
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
}
