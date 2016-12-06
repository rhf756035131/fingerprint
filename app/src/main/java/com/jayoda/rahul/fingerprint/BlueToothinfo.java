package com.jayoda.rahul.fingerprint;

/**
 * Created by rahul on 16-12-5.
 */

public class BlueToothinfo {
    /**
     * 蓝牙连接类型
     * @author rahul
     *
     */
    public enum ServerOrCilent{
        NONE,
        SERVICE,
        CILENT
    };
    //蓝牙连接方式
    public static ServerOrCilent serviceOrCilent = ServerOrCilent.NONE;
    //连接蓝牙地址
    public static String BlueToothAddress = null,lastblueToothAddress=null;
    //通信线程是否开启
    public static boolean isOpen = false;
}
