package com.jayoda.rahul.fingerprint;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


public class DeviceTimeSetting extends AppCompatActivity {
    private String TAG = "TimeSetting";
    private static final int msgKey1 = 1;
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_time_setting);
        mTime = (TextView) findViewById(R.id.tvDeviceTimeID);
        new TimeThread().start();

        SetDeviceTimeFun();
    }

    private void SetDeviceTimeFun(){
        final TextView tv;
        tv = (TextView)this.findViewById(R.id.textView2);
        RadioGroup syncgroup = (RadioGroup)this.findViewById(R.id.synctypeGroupID);
        syncgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton)DeviceTimeSetting.this.findViewById(radioButtonId);
                Log.d(TAG, "onCheckedChanged: " + rb.getText());

                switch(rb.getId()){
                    case R.id.radioButton_Sys:
                        //tv.setText("onCheckedChanged1：" + rb.getText());
                        long phoneTime = System.currentTimeMillis();
                        CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", phoneTime);
                        tv.setText(sysTimeStr);
                        break;
                    case R.id.radioButton_Net:
                        tv.setText("onCheckedChanged2：" + rb.getText());
                        SntpClient client = new SntpClient();
                        if (client.requestTime("time.foo.com")) {
                            long now = client.getNtpTime() + SystemClock.elapsedRealtime() -
                                    client.getNtpTimeReference();
                            Date current = new Date(now);
                            Log.i("NTP tag", current.toString());
                        }
                        break;
                    case R.id.radioButton_Manual:
                        tv.setText("onCheckedChanged3：" + rb.getText());
                        break;
                    default:
                        Log.d(TAG, "onCheckedChanged: ErrorKeyID" + rb.getId());
                        break;
                }
            }
        });
    }


    public class TimeThread extends Thread {
        @Override
        public void run () {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while(true);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage (Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("yyyy-MM-dd HH:mm:ss", sysTime);
                    mTime.setText(sysTimeStr);
                    break;

                default:
                    break;
            }
        }
    };
}
