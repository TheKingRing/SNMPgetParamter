package com.mycomp.mrwang.snmpgetparamter.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.mycomp.mrwang.snmpgetparamter.R;

import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * 完成trap实时监听工作
 * 由于Android linux系统级端口使用限制问题，我们一致将trap接收的端口默认为1555
 * Created by wzq on 2017/5/15.
 */
public class TrapReciever implements CommandResponder, Runnable {
  //  private final String TAG = "TrapReceiver";
    private Snmp snmp = null;

    private List<Map<String, String>> data;
    private LinkedList<Map<String, String>> que; //限制只存储

    private boolean initFlag = false;
    private final String DEFAULT_PROTOCOL;
    private final int DEFAULT_PORT;
    private CompatUtils helper;

    private Handler handler;
    private boolean tag = true;
    private String ip;

    public TrapReciever(Context context, List<Map<String, String>> data, LinkedList<Map<String, String>> que, Handler handler) {
        this.data = data;
        this.que = que;
        this.handler = handler;
        helper = CompatUtils.getInstance(context);
        ip = helper.getMobileIP();
        DEFAULT_PROTOCOL = "udp";
        DEFAULT_PORT = 1555;
    }
    private void init(){
        ThreadPool threadPool = ThreadPool.create("Trap", 2);
        MultiThreadedMessageDispatcher dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());

        Address listenAddress = GenericAddress.parse(System.getProperty(
                "snmp4j.listenAddress", DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT));
        TransportMapping transport;
        // 对TCP与UDP协议进行处理
        try {
            if (listenAddress instanceof UdpAddress) {
                transport = new DefaultUdpTransportMapping((UdpAddress) listenAddress);
            } else {
                transport = new DefaultTcpTransportMapping((TcpAddress) listenAddress);
            }
            snmp = new Snmp(dispatcher, transport);
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3());
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3
                    .createLocalEngineID()), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp.listen();
            initFlag = true;
        }catch (IOException e) {
            e.printStackTrace();
        }



    }
    @Override
    public void run() {
        if (!initFlag) {
            init();
        }
        snmp.addCommandResponder(this);
    }

    @SuppressWarnings("unchecked")
    public synchronized void processPdu(CommandResponderEvent respEvnt) {
// 解析Response
        if (respEvnt != null && respEvnt.getPDU() != null)
        {
            /*过滤所有与该设备无关的ip*/
            String ip = respEvnt.getPeerAddress().toString().split("/")[0];
            if (ip.equals(helper.getEquipmentIP())) {
                SimpleDateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);//设置日期格式
                Date date = new Date();
                String time = df.format(date); // new Date()为获取当前系统时间

                Map<String, String> tmp = new HashMap<>();
                tmp.put("time", time.split(" ")[1]);
                tmp.put("ip", ip);
                data.add(tmp);
                if (data.size() > 20) {
                    data.remove(0);
                    que.removeFirst();
                }

                Vector<VariableBinding> recVBs = respEvnt.getPDU().getVariableBindings();
                Map<String, String> tmp1 = new HashMap<>();
                Map<String, String> oid_key = helper.getOidkey();
                for (int i = 0; i < recVBs.size(); i++)
                { // 解析
                    VariableBinding recVB = recVBs.elementAt(i);
                    String oid = recVB.getOid().toString();
                    String value = recVB.getVariable().toString();
                    if (oid_key.containsKey(oid)) {
                        tmp1.put(oid_key.get(oid), value);
                    }
                }

                que.addLast(tmp1);
                if (tag) {
                    tag = false;
                    Message msg = Message.obtain(handler, R.id.trapMessageInit);
                    msg.sendToTarget();
                }else {
                    Message msg = Message.obtain(handler, R.id.trapMessageGet);
                    msg.sendToTarget();
                }
            }


        }
    }
    public void stop(){
        try {
            snmp.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
