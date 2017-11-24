package com.mycomp.mrwang.snmpgetparamter.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


import com.example.mylibrary.AbsEditText;
import com.example.mylibrary.ip.IPView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * 作者 : Mr wang
 * 日期 : 16/1/21 12:07
 * 邮箱 : aristocracyWZQ@gmail.com
 * 描述 : 工具类
 */
public class CompatUtils {
    private final HashMap<Integer, Boolean> paraStatusMap = new HashMap<>(); // 设置状态位 false: 可以发送消息设置 true: 表示那边正在设置没有收到回传消息，所以不能设置
    private Set<Integer> segMask; // 存储每个段的mask合法值
    private final String TAG = "CompatUtils";
    @SuppressLint("StaticFieldLeak")
    private static CompatUtils instance;

    private int current_program = -1;
    private Context context;
    private String EquipmentIP;
    private String MobileIP;
    private Map<String, Integer> index_asi;

    private final String[] tag = {"check0", "check1", "text1", "text2", "text3"};
    private final String[] tag1 = {"check0", "check1", "text1", "text2"};

    private Map<String, Object> data;//参数设置
    private Map<String, String> oidkey;
    private Map<String, Integer> inputStream_index;


    private CompatUtils(Context context) {
        this.context = context;
        initialData();
    }


    public static CompatUtils getInstance(Context context) {
        if (instance == null) {
            instance = new CompatUtils(context);
        }
        return instance;
    }


    //Suppress default constructor for noninstantiability
    private void initialData() {
        EquipmentIP = "192.168.9.146";
        data = new HashMap<>();
        oidkey = new HashMap<>();
        index_asi = new HashMap<>();
        inputStream_index = new HashMap<>();
        segMask = new HashSet<>();
        segMask.add(0);

        int tmp = 0;
        for (int i = 0; i < 8; i++) {
            tmp += Math.pow(2, i);
            segMask.add(tmp);
        }

    }

    public void setParaStatus(int id ,boolean status) {
        synchronized (paraStatusMap) {
            paraStatusMap.put(id, status);
        }
    }

    public boolean getParaStatus(int id) {
        synchronized (paraStatusMap) {
            if (paraStatusMap.containsKey(id)) {
                return paraStatusMap.get(id);
            }
            paraStatusMap.put(id, false);
            return false;
        }
    }

    /*获得snmp工具类*/
    public SnmpHelper getHelper(Context context) {
        SnmpHelper helper = new SnmpHelper(context);
        helper.setIP(EquipmentIP);
        return helper;
    }
    // 设置设备ip
    public void setEquipmentIP(String equipmentIP) {
        this.EquipmentIP = equipmentIP;
    }

    public int getNUM_ITEMS() {
        return 3;
    }

    public String[] getTag1() {
        return tag1;
    }

    public String[] getTag() {
        return tag;
    }

    public Map<String, String> getOidkey() {
        return oidkey;
    }

    public int getCurrent_program() {
        return current_program;
    }

    public void setCurrent_program(int current_program) {
        this.current_program = current_program;
    }

    public Map<String, Object> getData() {
        return data;
    }


    public void setData(Map<String, String> inform) {
        for (String st : inform.keySet()) {
            data.put(st, inform.get(st));
        }
    }

    public void changeData(String str, Object s) {
        data.put(str, s);
    }

    public int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void initialAddressView(IPView ipAddr, Object address) {
        ArrayList<AbsEditText> editTexts = ipAddr.getEditTexts();
        String[] strs = String.valueOf(address).split("\\.");
        editTexts.get(0).requestFocus();
        for (int i = 0; i < editTexts.size(); i++) {
            editTexts.get(i).setText(strs[i]);
        }
    }


    /*初始化viewadapter_main的数据*/
    public List<Map<String, Object>> initialdata(List<String[]> name) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String[] strings : name) {
            String choice = pickchoiseItem(strings[0]);
            int count = 0;
            Map<String, Object> map = new HashMap<>();
            for (String string : tag) {
                if (string.contains("check")) {
                    if (string.equals(choice)) {
                        map.put(string, new Bean(true));
                    } else {
                        map.put(string, new Bean());
                    }

                } else {
                    map.put(string, strings[count++]);
                }
            }
            list.add(map);
        }
        return list;
    }

    /*初始化viewadapter_main2的数据*/
    private List<Map<String, Object>> initialdata1(List<String[]> name) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String[] strings : name) {
            String choice = pickchoiseItem(strings[0]);
            int count = 0;
            Map<String, Object> map = new HashMap<>();
            for (String string : tag1) {
                if (string.contains("check")) {
                    if (string.equals(choice)) {
                        map.put(string, new Bean(true));
                    } else {
                        map.put(string, new Bean());
                    }
                } else {
                    map.put(string, strings[count++]);
                }
            }
            list.add(map);
        }
        return list;
    }

    /*根据获得的数据判断初始的选项*/
    private String pickchoiseItem(String TAG) {
        String tmpStr = "check";
        int type;
        switch (TAG) {
            case "Trap1版本":
                tmpStr += data.get("trapAddr1Version");
                break;
            case "Trap1使能":
                tmpStr += data.get("trapEnable1").equals("0") ? "1" : "0";
                break;
            case "Trap2版本":
                tmpStr += data.get("trapAddr2Version");
                break;
            case "Trap2使能":
                tmpStr += data.get("trapEnable2").equals("0") ? "1" : "0";
                break;
            case "FRAME":
                type = Integer.parseInt(String.valueOf(data.get("frameType")));
                if (type == 0) tmpStr += 1;
                else tmpStr += 0;
                break;
            case "RS":
                type = Integer.parseInt(String.valueOf(data.get("rsType")));
                if (type == 0) tmpStr += 1;
                else tmpStr += 0;
                break;
            case "MSBF":
                type = Integer.parseInt(String.valueOf(data.get("msbfType")));
                if (type == 0) tmpStr += 1;
                else tmpStr += 0;
                break;
            case "E3":
                tmpStr += data.get("inputSource");
                break;
            case "手动":
                tmpStr += data.get("autoDitect");
                break;
        }
        return tmpStr;
    }

    //生成fragment1 的数据
    public List<Map<String, Object>> getList1() {
        /*初始化view1*/
        List<String[]> name = new ArrayList<>();
        name.add(new String[]{"E3", "DS3 "});
        return initialdata1(name);
    }

    //生成fragment2 的数据

    public List<Map<String, Object>> getList2() {
        List<String[]> name = new ArrayList<>();
        name.add(new String[]{"手动", "自动"});
        return initialdata1(name);
    }

    //生成fragment3 的数据

    public List<Map<String, Object>> getList3() {
        List<String[]> name = new ArrayList<>();
        name.add(new String[]{"FRAME", "ON", "OFF"});
        name.add(new String[]{"RS", "ON", "OFF"});
        name.add(new String[]{"MSBF", "ON", "OFF"});
        return initialdata(name);
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public void restoreOid_Name(String oid, String parameter) {
        oidkey.put(oid, parameter);
    }

    /**
     * @param i : the int value of IP
     *          this func is to make i -> IP
     */
    public void intToIp(int i) {
        this.MobileIP = (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }


    /**
     * ip -> long
     * // left shifting 24,16,8,0 and bitwise OR
     * <p>
     * // 1. 192 << 24
     * // 1. 168 << 16
     * // 1. 1 << 8
     * // 1. 2 << 0
     */
    private long ipToLong(String ipAddress) {
        long result = 0;

        String[] ipAddressInArray = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return result;
    }


    /**
     * 判断子网掩码是否合法
     *
     * @param mask:子网掩码字符串
     */
    public boolean checkMask(String mask) {
        String[] ipIndex = mask.split("\\.");
        Log.e(TAG, "checkMask: " + mask);
        boolean tag = true;
        for (String seg : ipIndex) {
            int segma = Integer.parseInt(seg);
            if (tag) {
                if (segma != 255) {
                    if (!segMask.contains(segma)) {
                        Log.e(TAG, "checkMask: end");
                        return false;
                    }
                    tag = false;
                }
            } else {
                if (segma != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 一个设备的ip必须与网关在同一个网段，所以用 ip & mask 和 gate & mask想比较判断
     *
     * @param gate : 网关
     * @param ip   : ip 地址
     * @param mask : 子网掩码
     */
    public boolean checkIP(String ip, String mask, String gate) {
        long ipVal = ipToLong(ip);
        long gateVal = ipToLong(gate);
        long maskTag = ipToLong(mask);
        return (ipVal & maskTag) == (gateVal & maskTag);
    }

    /*获得设备ip*/
    String getEquipmentIP() {
        return EquipmentIP;
    }

    /*获得手机ip*/
    String getMobileIP() {
        return MobileIP;
    }

    public void clear() {
        inputStream_index.clear();
        index_asi.clear();
    }

    /*存储inputStream spinner 的index*/
    public void setInputStream_index(String index) {
        int size = inputStream_index.size();
        inputStream_index.put(index, size);
    }

    public int getInputStream_index(String name) {
        return inputStream_index.get(name);
    }

    /*存储asi spinner 的index*/
    public void setAsi_index(String asi_index) {
        int size = index_asi.size();
        index_asi.put(asi_index, size);
    }

    /*蝴蝶asi spinner index的项*/
    public int getAsi_Index(String name) {
        return index_asi.get(name);
    }

    /*修改数据*/
    public void changeData(Bundle bd) {
        for (String key : bd.keySet()) {
            data.put(key, bd.get(key));
        }
    }

    /**
     * 动态测量listview-Item的高度
     *
     * @param listView:需要测量的listview
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
