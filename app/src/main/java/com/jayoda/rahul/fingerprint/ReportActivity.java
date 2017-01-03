package com.jayoda.rahul.fingerprint;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ReportActivity extends PreferenceActivity implements BlueToothMsg.Callback{

    private Preference GLUPreference;
    private Preference BILPreference;
    private Preference SGPreference;
    private Preference PHPreference;
    private Preference KETPreference;
    private Preference BLDPreference;
    private Preference PROPreference;
    private Preference UROPreference;
    private Preference NITPreference;
    private Preference LEUPreference;
    private Preference VCPreference;
    private BlueToothMsg BTService;
    private BlueToothMsg.MsgBinder myBinder;

    public  final String TAG = "Report.BTService";
    private final int ChangeToPreference = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_report);
        initPreferences();
        bindService(new Intent(this, BlueToothMsg.class), conn, BIND_AUTO_CREATE);
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
            Log.d(TAG, "ReportActivity BTService="+BTService.toString());
            BTService.setCallback(ReportActivity.this);
            myBinder.sendCommand(BT_command.cmd_read_single_date);
        }
    };
    @Override
    public void onDataChange(String data) {
        Message msg = new Message();
        msg.obj = data;
        handler.sendMessage(msg);
    }
    @Override
    public void onDataChange(byte[] buf_data) {
        Message msg = new Message();
        msg.obj = buf_data;
        msg.what=ChangeToPreference;
        handler.sendMessage(msg);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] buf_data;
            super.handleMessage(msg);
            switch (msg.what){
                case ChangeToPreference:
                    buf_data =(byte[])msg.obj;
                    set_textview_GLU_R((buf_data[16] >> 1) & 0x07);
                    set_textview_TV_BIL_R(((buf_data[16] << 2) & 0x04) | ((buf_data[17] >> 6) & 0x03));
                    set_textview_TV_KET_R((buf_data[17] >> 3) & 0x07);
                    set_textview_TV_BLD_R((buf_data[14] >> 4) & 0x07);
                    set_textview_TV_RPO_R(((buf_data[14] << 2) & 0x04) | ((buf_data[15] >> 6) & 0x03));
                    set_textview_TV_URO_R((buf_data[15] >> 3) & 0x07);
                    set_textview_TV_NIT_R(buf_data[15] & 0x07);
                    set_textview_TV_VC_R(buf_data[16] >> 4 & 0x07);
                    set_textview_TV_LEU_R((buf_data[12] >> 3) & 0x07);
                    set_textview_TV_PH_R((buf_data[14] >> 1) & 0x07);
                    set_textview_TV_SG_R(buf_data[17] & 0x07);
                    break;
                default:
                    Toast.makeText(ReportActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }

        }
    };
    final protected static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
    private void initPreferences() {
        GLUPreference = (Preference)findPreference("GLU");
        BILPreference = (Preference)findPreference("BIL");
        SGPreference = (Preference)findPreference("SG");
        PHPreference = (Preference)findPreference("PH");
        KETPreference = (Preference)findPreference("KET");
        BLDPreference = (Preference)findPreference("BLD");
        PROPreference = (Preference)findPreference("PRO");
        UROPreference = (Preference)findPreference("URO");
        NITPreference = (Preference)findPreference("NIT");
        LEUPreference = (Preference)findPreference("LEU");
        VCPreference = (Preference)findPreference("VC");
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    private void set_textview_GLU_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                GLUPreference.setSummary("-");
                break;
            case 1:
                GLUPreference.setSummary("+-");
                break;
            case 2:
                GLUPreference.setSummary("+1");
                break;
            case 3:
                GLUPreference.setSummary("+2");
                break;
            case 4:
                GLUPreference.setSummary("+3");
                break;
            case 5:
                GLUPreference.setSummary("+4");
                break;
            default:
                GLUPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_BIL_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                BILPreference.setSummary("-");
                break;
            case 1:
                BILPreference.setSummary("+1");
                break;
            case 2:
                BILPreference.setSummary("+2");
                break;
            case 3:
                BILPreference.setSummary("+3");
                break;
            default:
                BILPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_SG_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                SGPreference.setSummary("1.000");
                break;
            case 1:
                SGPreference.setSummary("1.005");
                break;
            case 2:
                SGPreference.setSummary("1.010");
                break;
            case 3:
                SGPreference.setSummary("1.015");
                break;
            case 4:
                SGPreference.setSummary("1.020");
                break;
            case 5:
                SGPreference.setSummary("1.025");
                break;
            case 6:
                SGPreference.setSummary("1.030");
                break;
            default:
                SGPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_PH_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                PHPreference.setSummary("5.0");
                break;
            case 1:
                PHPreference.setSummary("6.0");
                break;
            case 2:
                PHPreference.setSummary("6.5");
                break;
            case 3:
                PHPreference.setSummary("7.0");
                break;
            case 4:
                PHPreference.setSummary("7.5");
                break;
            case 5:
                PHPreference.setSummary("8.0");
                break;
            case 6:
                PHPreference.setSummary("8.5");
                break;
            default:
                PHPreference.setSummary("error");
                break;
        }
    }

    private void set_textview_TV_KET_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                KETPreference.setSummary("-");
                break;
            case 1:
                KETPreference.setSummary("+-");
                break;
            case 2:
                KETPreference.setSummary("+1");
                break;
            case 3:
                KETPreference.setSummary("+2");
                break;
            case 4:
                KETPreference.setSummary("+3");
                break;
            case 5:
                KETPreference.setSummary("+4");
                break;
            default:
                KETPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_BLD_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                BLDPreference.setSummary("-");
                break;
            case 1:
                BLDPreference.setSummary("+-");
                break;
            case 2:
                BLDPreference.setSummary("+1");
                break;
            case 3:
                BLDPreference.setSummary("+2");
                break;
            case 4:
                BLDPreference.setSummary("+3");
                break;
            default:
                BLDPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_RPO_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                PROPreference.setSummary("-");
                break;
            case 1:
                PROPreference.setSummary("+-");
                break;
            case 2:
                PROPreference.setSummary("+1");
                break;
            case 3:
                PROPreference.setSummary("+2");
                break;
            case 4:
                PROPreference.setSummary("+3");
                break;
            case 5:
                PROPreference.setSummary("+4");
                break;
            default:
                PROPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_URO_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                UROPreference.setSummary("-");
                break;
            case 1:
                UROPreference.setSummary("+1");
                break;
            case 2:
                UROPreference.setSummary("+2");
                break;
            case 3:
                UROPreference.setSummary("+3");
                break;
            default:
                UROPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_NIT_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                NITPreference.setSummary("-");
                break;
            case 1:
                NITPreference.setSummary("+");
                break;
            default:
                NITPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_LEU_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                LEUPreference.setSummary("-");
                break;
            case 1:
                LEUPreference.setSummary("+-");
                break;
            case 2:
                LEUPreference.setSummary("+1");
                break;
            case 3:
                LEUPreference.setSummary("+2");
                break;
            case 4:
                LEUPreference.setSummary("+3");
                break;
            default:
                LEUPreference.setSummary("error");
                break;
        }
    }
    private void set_textview_TV_VC_R(int Reslut)
    {
        switch (Reslut)
        {
            case 0:
                VCPreference.setSummary("-");
                break;
            case 1:
                VCPreference.setSummary("+-");
                break;
            case 2:
                VCPreference.setSummary("+1");
                break;
            case 3:
                VCPreference.setSummary("+2");
                break;
            case 4:
                VCPreference.setSummary("+3");
                break;
            default:
                VCPreference.setSummary("error");
                break;
        }
    }
}
