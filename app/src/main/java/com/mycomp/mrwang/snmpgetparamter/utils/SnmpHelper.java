package com.mycomp.mrwang.snmpgetparamter.utils;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.mycomp.mrwang.snmpgetparamter.R;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;


import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.snmp4j.smi.GenericAddress.parse;

/**
 * 该类为snmp 协议通信主要的类，完成所有协议传输的工作
 * Created by wzq on 2017/5/22.
 */


public class SnmpHelper {
    private CompatUtils helper;
    private final String TAG = "snmpHelper";
    private List<VariableBinding> VariableBindings;
    private String IP;
    private final int DEFAULT_VERSION;
    private final String DEFAULT_PROTOCOL;
    private final int DEFAULT_PORT;
    private final long DEFAULT_TIMEOUT;
    private final int DEFAULT_RETRY;
    private final String DEFAULT_COMMUNITY;
    private final String NUM_REGEX;//
    private Map<String, String> oidKey;
    private Map<String, Object> data;

    public SnmpHelper(Context context) {
        VariableBindings = new ArrayList<>();
        helper = CompatUtils.getInstance(context);
        data = helper.getData();
        this.oidKey = helper.getOidkey();
        /*-----------默认snmp协议基本参数设置-------*/
        DEFAULT_VERSION = SnmpConstants.version2c;
        DEFAULT_PROTOCOL = "udp";
        DEFAULT_PORT = 161;
        DEFAULT_TIMEOUT = 3 * 1000L;
        DEFAULT_RETRY = 5;
        DEFAULT_COMMUNITY = "public";
         /*-----------默认snmp协议基本参数设置-------*/

        NUM_REGEX = "[0-9]+";
    }

    /**
     * 创建对象communityTarget
     *
     * @return CommunityTarget
     */
    private CommunityTarget createDefault() {
        Address address = parse(DEFAULT_PROTOCOL + ":" + IP
                + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(DEFAULT_COMMUNITY));
        target.setAddress(address);
        target.setVersion(DEFAULT_VERSION);
        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
        target.setRetries(DEFAULT_RETRY);
        return target;
    }
    /*负ip*/
    void setIP(String IP) {
        this.IP = IP;
    }
    /*添加需要查找的oid*/
    public void addOID(String oid){
        VariableBindings.add(new VariableBinding(new OID(oid)));
    }
    public void addOIDs(Collection<String> oids) {
        for (String oid : oids) {
            VariableBindings.add(new VariableBinding(new OID(oid)));
        }
    }
    /**
     * get single one data
     * */
    public String getsigleData(final Handler handler, String oid){
        if (oid == null) {
            return null;
        }
        final String[] res = {null};
        CommunityTarget target = createDefault();
        Snmp snmp = createSnmp();
        try {
            snmp.listen();
            PDU pdu = new PDU();
            pdu.setType(PDU.GET);
            pdu.addOID(new VariableBinding(new OID(oid)));
            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    if (response == null) {
                        Log.e(TAG, "onResponse: getsingleData noresponse::::::::::::::::"  );

                        Message msg = Message.obtain(handler,R.id.noresponse);
                        msg.sendToTarget();
                        /*---------------------check if ip is changed---------------*/
                    } else {
                        if(response.getErrorIndex()== PDU.noError &&response.getErrorStatus()== PDU.noError) {
                            res[0] = response.get(0).getVariable().toString();
                            Log.e(TAG, "onResponse: getsigledata success::::::::::::::::" + res[0] );
                        }else {
                            Log.e(TAG, "onResponse: getsigledata error::::::::::::::::" + response.getErrorStatusText()  );
                        }

                        latch.countDown();
                    }
                }
            };
            pdu.setType(PDU.GET);
            snmp.send(pdu, target, null, listener);
            latch.await(20, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                snmp.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res[0];
    }


    /**
     * get the data via given oid
     * */
    public Map<String, String> getData(final Handler handler){
        final Map<String, String> res = new HashMap<>();
        CommunityTarget target = createDefault();
        Snmp snmp = createSnmp();
        try {
            snmp.listen();
            PDU pdu = new PDU();
            for (VariableBinding binding : VariableBindings) {
                pdu.add(binding);
            }

            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    if (response == null) {
                        Log.e(TAG, "onResponse: getData noresponse::::::::::::::::"  );

                        if (handler == null) {
                            return;
                        }
                        Message msg = Message.obtain(handler,R.id.noresponse);
                        msg.sendToTarget();
                    } else {
                        if(response.getErrorIndex()== PDU.noError &&response.getErrorStatus()== PDU.noError){
                            for (int i = 0; i < response.size(); i++) {
                                VariableBinding vb = response.get(i);
                                res.put(oidKey.get(vb.getOid().toString()), vb.getVariable().toString());
                            }
                            Log.e(TAG, "onResponse: getData " + res );
                        }else{
                            Log.e(TAG, "onResponse: getData error::::::::::::::::" + response.getErrorStatusText()  );
                        }

                        latch.countDown();
                    }
                }
            };
            pdu.setType(PDU.GET);
            snmp.send(pdu, target, null, listener);
            latch.await(20, TimeUnit.SECONDS);
            snmp.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "getData: " + e.getMessage() );
        }finally {
            VariableBindings.clear();
            try {
                snmp.close();
            } catch (IOException e) {
                Log.e(TAG, "getData: " + e.getMessage() );
            }
        }
        return res;
    }

/**
 * AsynSet the  para
 * @param id :the id of segment which is being setted
 * @param map :the key-value key:oid,
 * @param myhandler :the handler which msg send to notify the layout
 * */
    public void snmpAsynSetList(final Map<String, String> map, final Handler myhandler, final int id){
        CommunityTarget target = createDefault();
        Snmp snmp = createSnmp();
        try {
            snmp.listen();
            PDU pdu = new PDU();
            pdu.setType(PDU.SET);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String val = entry.getValue();
                String oid = entry.getKey();
                if (val.matches(NUM_REGEX)) {
                    pdu.add(new VariableBinding(new OID(oid), new Integer32(Integer.parseInt(val))));
                }else {
                    pdu.add(new VariableBinding(new OID(oid), new OctetString(val)));
                }
            }
            /*异步监听设置*/
            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {

                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                    PDU response = event.getResponse();
                    helper.setParaStatus(id, false);
                    if (response == null) {
                        Message msg = Message.obtain(myhandler,R.id.noresponse,id);
                        Bundle bd = new Bundle();
                        for (String key : map.keySet()) {
                            String para = oidKey.get(key);
                            if (para.equals("ipAddr")) {
                                bd = new Bundle();
                                for (String key1 : map.keySet()) {
                                    bd.putString(oidKey.get(key1), map.get(key1));
                                }
                                msg = Message.obtain(myhandler, R.id.ipchanged);
                                msg.sendToTarget();
                                return;
                            }
                            bd.putString(para, String.valueOf(data.get(para)));
                        }
                        msg.setData(bd);
                        msg.sendToTarget();
                    }else {
                        Message msg = Message.obtain(myhandler, id);
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < response.size(); i++) {
                            VariableBinding vb = response.get(i);
                            bundle.putString(oidKey.get(vb.getOid().toString()), vb.getVariable().toString());
                        }
                        msg.setData(bundle);
                        msg.sendToTarget();
                        latch.countDown();
                    }
                }
            };
            snmp.set(pdu, target, null, listener);
            latch.await(20, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "snmpAsynSetList: " + e.getMessage() );
        } finally {
            VariableBindings.clear();
            try {
                snmp.close();
            } catch (IOException e) {
                Log.e(TAG, "snmpAsynSetList: " + e.getMessage() );
            }
        }
    }

    /**
     * 创建snmp
     * 当有错误时重新创建
     * */
    private Snmp createSnmp() {
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            return new Snmp(transport);
        } catch (IOException e) {
            Log.e(TAG, "createSnmp: " + e.getMessage() );
            return createSnmp();
        }
    }

}
