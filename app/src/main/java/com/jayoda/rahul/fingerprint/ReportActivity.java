package com.jayoda.rahul.fingerprint;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ReportActivity extends PreferenceActivity{

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
    private BlueToothMsg myService = MainActivity.myBinder.getService();

    public  final String TAG = "BlueToothService";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.activity_report);
        initPreferences();
    }

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
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        MainActivity.myBinder.sendCommand(BT_command.cmd_read_single_date);
        myService.setalldateCallback(new BlueToothMsg.AllDateCallback() {
                                         @Override
                                         public void onChangeAllDate(byte[] buf_data) {
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
                                         }
                                     }
        );
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
