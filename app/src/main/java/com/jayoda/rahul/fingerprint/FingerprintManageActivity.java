package com.jayoda.rahul.fingerprint;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FingerprintManageActivity extends AppCompatActivity {

    private BlueToothMsg myService = MainActivity.myBinder.getService();
    private  int count=0;
    private  final int maxCount=2; //采集３次
    private  int InputFingerPrintCount=0;
    private  final int InputFingerPrintCountMax=10;
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
                    case BT_command.cmd_collect_finger://终端返回采集图像命令
                        if (data[6] == 0x00) {
                            //把检查到的指纹生成特征，保存到ramBuffer中
                            MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_push_ram);
                        }else{
                            if(InputFingerPrintCount<InputFingerPrintCountMax){
                                InputFingerPrintCount++;
                                MainActivity.myBinder.sendCommand(BT_command.cmd_collect_finger);
                            }else{
                                Scan_left.setEnabled(true);
                                Scan_right.setEnabled(true);
                            }
                        }
                        break;
                    case BT_command.cmd_fingerTemp_push_ram://终端返回保存指纹特征到ramBuffer中的命令
                        if (data[6] == 0x00) {
                            if (count < maxCount) {
                                //保存成功,保存三次后，生成指纹id并存储起来
                                count++;
                                MainActivity.myBinder.sendCommand(BT_command.cmd_fingerTemp_merge);
                            }
                        }else {
                            Scan_left.setEnabled(true);
                            Scan_right.setEnabled(true);
                        }
                        break;
                    case BT_command.cmd_fingerTemp_merge:
                        Scan_left.setEnabled(true);
                        Scan_right.setEnabled(true);
                        if (data[6] == 0x00) {
                            if(count<maxCount){
                                Scan_left.setText(R.string.again);
                                Scan_right.setText(R.string.continuation);
                            }else {
                                Scan_left.setEnabled(false);
                                Scan_right.setText(R.string.done);
                            }
                        }else{
                            count--;
                        }
                        break;
                    default:
                            break;
                }

            }
        });
    }

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
                    }else{
                        count--;
                        MainActivity.myBinder.sendCommand(BT_command.cmd_collect_finger);
                        Scan_left.setEnabled(false);
                        Scan_right.setEnabled(false);
                    }
                    break;
                case R.id.scan_right:
                    if(getResources().getString(R.string.done).equals(ButtonText)){
                        finish();
                    }else{
                        MainActivity.myBinder.sendCommand(BT_command.cmd_collect_finger);
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
