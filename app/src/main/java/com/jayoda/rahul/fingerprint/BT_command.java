package com.jayoda.rahul.fingerprint;

/**
 * Created by rahul on 16-12-6.
 */

public class BT_command {
    public static final  byte[]  header=new byte[]{(byte)0x93,(byte)0x8e};
    public static final  byte[] machine_code=new byte[]{0x00,0x08};

    public static final  byte cmd_device_ack=0x01;
    public static final  byte cmd_syn_time=0x02;
    public static final  byte cmd_read_time=0x03;
    public static final  byte cmd_read_single_date=0x04;
    public static final  byte cmd_read_all_date=0x05;
    public static final  byte cmd_del_date=0x06;
    public static final  byte cmd_error=0x07;
    public static final  byte cmd_close_bt=0x09;
    public static final  byte cmd_rename_bt=0x0a;
    public static final  byte cmd_auto_test=0x0B;
    // public static final  byte cmd_auto_test=0x0B;
    public static final  byte cmd_collect_finger=0x20;
    public static final  byte cmd_fingerTemp_push_datebase=0x40;
    public static final  byte cmd_del_finger=0x44;
    public static final  byte cmd_get_ID=0x45;
    public static final  byte cmd_check_ID=0x46;
    public static final  byte cmd_fingerTemp_push_ram=0x60;
    public static final  byte cmd_fingerTemp_merge=0x61;
}
