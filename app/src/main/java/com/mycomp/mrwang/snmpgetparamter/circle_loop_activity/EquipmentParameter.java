package com.mycomp.mrwang.snmpgetparamter.circle_loop_activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import android.widget.Spinner;

import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mylibrary.ip.IPView;


import com.mycomp.mrwang.snmpgetparamter.Adapter.myFragmentAdapter;
import com.mycomp.mrwang.snmpgetparamter.Adapter.myViewAdapter;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.EquipmentParagetTask;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.IpAsyncTask;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.TrapGetTask;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.TrapSetAsyncTask;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.TraptAsyncTask;
import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.utils.SystemApplication;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.ryg.dynamicload.DLBasePluginFragmentActivity;


import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


import java.util.concurrent.ExecutionException;

import static android.os.Message.obtain;


import static com.mycomp.mrwang.snmpgetparamter.R.id.dvbs22K;
import static com.mycomp.mrwang.snmpgetparamter.R.id.polarization;


/**
 * 设备参数 activity
 * Created by Mr Wang on 2016/6/12.
 */
public class EquipmentParameter extends DLBasePluginFragmentActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private final String TAG = "Activity";
    private DBhelper dBhelper;
    private Message message;
    private  ListView trap1Enable;
    private  ListView trap2Enable;
    private ViewPager pager;
    private Spinner ASI_output;
    private Spinner contellation_style;
    private Spinner inversionNum;
    private Spinner J83_type;
    private Spinner polarizationsp;
    private Spinner dvbs22Ksp;
    private IPView maskAddr;
    private IPView trapAddress1;
    private IPView trapAddress2 ;
    private IPView multicastAddr;
    private IPView gateAddr;

    private Map<String, View> ViewData;

    private SnmpHelper manager;
    private CompatUtils helper;


    /*dvbc,dvbs,de3ePara的参数*/
    private Map<String, String> dvbsMap;
    private Map<String, String> dvbcMap;


    private Handler myhandler = new Handler() {

        /*handler 接收*/
        @Override
        public void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            switch (msg.what) {
                /*--------------ip ping finshed------------*/
                case R.id.ip_check_finished:
                    MyProgressDialog dialog = (MyProgressDialog) msg.obj;
                    dialog.dismiss();
                    break;
                case R.id.ip_being_used:
                    MyProgressDialog dio = (MyProgressDialog) msg.obj;
                    dio.dismiss();
                    Toast.makeText(mProxyActivity, "IP has been used", Toast.LENGTH_LONG).show();
                    break;
                    /*--------------ip ping finshed------------*/
                case R.id.ipchanged:
                    helper.changeData(bd);
                    /*--------------ip 改完 需要回传到宿主----------------*/
                    helper.setEquipmentIP(bd.getString("ipAddr"));
                    SystemApplication.getInstance().exit();
                    break;
                    /*--------------no response------------*/
                case R.id.noresponse:
                        switch (msg.arg1) {
                            case R.id.internetbt:
                                setIPValue(bd);
                                break;
                            case R.id.trapbt:
                                setTraptValue(bd);
                                break;
                            case R.id.broadcastbt:
                                setBroadcastValue(bd);
                                break;
                            case R.id.ipToAsi:
                                ((EditText) findViewById(R.id.ipToASIPara)).setText(bd.getString("ipToASIPara"));
                                break;
                            case R.id.ASI_out:
                                String name =dBhelper.getInpustreamType(Long.parseLong(bd.getString("asiOutputPara")));
                                ASI_output.setSelection(helper.getAsi_Index(name));
                                break;
                            case R.id.dvbc:
                                setDvbcValue(bd);
                                break;
                            case R.id.dvbs:
                                setDvbsValue(bd);
                                break;
                            case R.id.DS3_set:
                                setDS3Value(bd);
                                break;
                            default:
                                Toast.makeText(mProxyActivity, "[ERROR]: response is null", Toast.LENGTH_LONG).show();

                        }
                        break;
                /*------------text null--------------------*/
                case R.id.Nullerror:
                    Toast.makeText(mProxyActivity, "please fill your configuration", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.update:
                    pager.getAdapter().notifyDataSetChanged();
                    break;
                /*------------the ip mask and gate is match--------------*/
                case R.id.disMatch:
                    Toast.makeText(mProxyActivity, "the ip and gate are not in same sub internet", Toast.LENGTH_SHORT).show();
                    break;
                /*------------the mask is illegal-------------*/
                case R.id.maskerror:
                    Toast.makeText(mProxyActivity, "the mask address is illegal", Toast.LENGTH_SHORT).show();
                    break;

               /*--------------trap set --------------------*/
                case R.id.trapbt:
                    bd = msg.getData();
                    if (bd.isEmpty()) {
                        break;
                    }
                    Iterator<String> it = bd.keySet().iterator();
                    String oid = dBhelper.gettrapParaOid(it.next());

                    TraptAsyncTask traptAsyncTask = new TraptAsyncTask(that);
                    traptAsyncTask.execute(oid);
                    break;
                /*--------------trap set --------------------*/
                /*----------------refresh------------------*/
                case R.id.internetbt2:
                    if (!helper.getData().get("maskAddr").equals(bd.get("maskAddr"))) {
                        helper.initialAddressView(maskAddr, bd.get("maskAddr"));
                        helper.getData().put("maskAddr", bd.get("maskAddr"));

                    }
                    if (!helper.getData().get("gateAddr").equals(bd.get("gateAddr"))) {
                        helper.initialAddressView(gateAddr, bd.getString("gateAddr"));
                        helper.getData().put("gateAddr", bd.get("gateAddr"));
                    }
                    break;
                case R.id.trapbt1:
                    helper.changeData(bd);
                    setTraptValue(bd);
                    break;
                case R.id.broadcastbt1:
                    helper.changeData(bd);
                    setBroadcastValue(bd);
                    break;
                case R.id.ipToAsi1:
                    Log.e(TAG, "handleMessage: " + bd);
                    helper.changeData(bd);
                    setasiOut(bd);
                    break;
                case R.id.dvbc1:
                    helper.changeData(bd);
                    setDvbcValue(bd);
                    break;
                case  R.id.dvbs1:
                    helper.changeData(bd);
                    setDvbsValue(bd);
                    break;
                 /*----------------refresh------------------*/
                 /*----------------set except trap ------------------*/
                case R.id.internetbt:
                    helper.changeData(bd);
                    break;
                case R.id.broadcastbt:
                    helper.changeData(bd);
                    break;
                case R.id.ipToAsi:
                    helper.changeData(bd);
                    break;
                case R.id.dvbc:
                    helper.changeData(bd);
                    break;
                case R.id.dvbs:
                    helper.changeData(bd);
                    break;
                case R.id.DS3_set:
                    helper.changeData(bd);
                    break;
                case R.id.ASI_out:
                    Log.e(TAG, "handleMessage: " + bd );
                    helper.changeData(bd);
                    break;
                /*----------------set except trap ------------------*/


            }
        }

        private void setIPValue(Bundle bd) {
            for (String key : bd.keySet()) {
                switch (key) {
                    case "maskAddr":
                        helper.initialAddressView(maskAddr, bd.get("mask"));
                        break;
                    case "gateAddr":
                        helper.initialAddressView(gateAddr, bd.get("gateAddr"));
                        break;
                }
            }
        }

        private void setasiOut(Bundle bd) {
            for (String key : bd.keySet()) {
                switch (key) {
                    case "asiOutputPara":
                        String cur = dBhelper.getInpustreamType(Long.parseLong(bd.getString(key)));
                        ASI_output.setSelection(helper.getAsi_Index(cur));
                        break;
                    case "ipToASIPara":
                        ((EditText) findViewById(R.id.ipToASIPara)).setText(bd.getString(key));
                        break;
                }
            }
        }

        /*设置DS3Value*/
        private void setDS3Value(Bundle bd) {
            for (String key: bd.keySet()) {
                helper.changeData(key, bd.get(key));
            }
            initViewPager();
        }

        /*设置DVBs*/
        private void setDvbsValue(Bundle bd) {
            for (String key : bd.keySet()) {
                switch (key){
                    case "Dfrequency":
                        ((EditText) findViewById(R.id.Dfrequency)).setText(bd.getString("Dfrequency"));
                        break;
                    case "Lfrequency":
                        ((EditText) findViewById(R.id.Lfrequency)).setText(bd.getString("Lfrequency"));
                        break;
                    case "dvbsSymbolRate":
                        ((EditText) findViewById(R.id.dvbsSymbolRate)).setText(bd.getString("dvbsSymbolRate"));
                        break;
                    case "polarization":
                        int j = Integer.parseInt(bd.getString("polarization"));
                        polarizationsp.setSelection(j);
                        break;
                    case "dvbs22K":
                        int k =  Integer.parseInt(bd.getString("dvbs22K"));
                        dvbs22Ksp.setSelection(k);
                        break;
                }
            }
        }
        /*设置广播*/
        private void setBroadcastValue(Bundle bd) {
            for (String key : bd.keySet()) {
                switch (key) {
                    case "multicastAddr":
                        helper.initialAddressView(multicastAddr, bd.getString("multicastAddr"));
                        break;
                    case "port":
                        ((EditText) findViewById(R.id.port)).setText(bd.getString("port"));
                        break;
                }
            }
        }
        /*设置trap*/
        private void setTraptValue(Bundle bd) {
            boolean tag1 = false;
            boolean tag2 = false;
            for (String key : bd.keySet()) {
                switch (key) {
                    case "trapAddress1":
                        helper.initialAddressView(trapAddress1, bd.getString("trapAddress1"));
                        break;
                    case "trapAddress2":
                        helper.initialAddressView(trapAddress2, bd.getString("trapAddress2"));
                        break;
                    case "trapInterval":
                        ((EditText)findViewById(R.id.traptime)).setText(bd.getString("trapInterval"));
                        break;
                    case "trapEnable1":
                        tag1 = true;
                        break;
                    case "trapAddr1Version":
                        tag1 = true;
                        break;
                    case "trapEnable2":
                        tag2 = true;
                        break;
                    case "trapAddr2Version":
                        tag2 = true;
                        break;
                }
            }

            if (tag1) {
                initFirst();
            }
            if (tag2){
                intSecond();
            }
        }

        /*初始化第一个选项处*/
        //选项是根据data里的值确定的
        private void initFirst() {
            List<String[]> name=new ArrayList<>();
            name.add(new String[]{"Trap1版本","V1","V2c"});
            name.add(new String[]{"Trap1使能","OFF","ON"});
            List<Map<String, Object>> list = helper.initialdata(name);
            myViewAdapter madapter1=new myViewAdapter(that, list,R.layout.viewadapter_main,helper.getTag(),new int[]{R.id.checkboxfor1,R.id.checkboxfor2,R.id.viewadaptertext1,R.id.viewadaptertext2,R.id.viewadaptertext3},true);
            trap1Enable.setAdapter(madapter1);
        }
        /*初始化第二个选项处*/
        private void intSecond() {
            List<String[]> name=new ArrayList<>();
            name.add(new String[]{"Trap2版本", "V1", "V2c"});
            name.add(new String[]{"Trap2使能", "OFF", "ON"});
            List<Map<String, Object>> list =helper.initialdata(name);
            myViewAdapter madapter2=new myViewAdapter(that, list,R.layout.viewadapter_main,helper.getTag(),new int[]{R.id.checkboxfor1,R.id.checkboxfor2,R.id.viewadaptertext1,R.id.viewadaptertext2,R.id.viewadaptertext3},true);
            trap2Enable.setAdapter(madapter2);
        }
        /*设置DVBC*/
        private void setDvbcValue(Bundle bd) {
            for (String key : bd.keySet()) {
                switch (key){
                    case "frequency":
                        ((EditText) findViewById(R.id.frequency)).setText(bd.getString("frequency"));
                        break;
                    case "dvbcSymbolRate":
                        ((EditText) findViewById(R.id.dvbcSymbolRate)).setText(bd.getString("dvbcSymbolRate"));
                        break;
                    case "constellation":
                        int i = Integer.parseInt(bd.getString("constellation"));
                        contellation_style.setSelection(i - 1);
                        break;
                    case "inversion":
                        int j = Integer.parseInt(bd.getString("inversion"));
                        inversionNum.setSelection(j);
                        break;
                    case "j83Annex":
                        int k =  Integer.parseInt(bd.getString("j83Annex"));
                        J83_type.setSelection(k);
                        break;
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipement_activity);
        helper = CompatUtils.getInstance(that);
        manager = helper.getHelper(that);
        SystemApplication.getInstance().addActivity(that);
        inititView();
        ScrollView scollview = (ScrollView) findViewById(R.id.scrollView);
        setupUI(scollview);
        findViewById(R.id.internetbt).setOnClickListener(this);
        findViewById(R.id.internetbt2).setOnClickListener(this);
        findViewById(R.id.trapbt).setOnClickListener(this);
        findViewById(R.id.trapbt1).setOnClickListener(this);
        findViewById(R.id.broadcastbt).setOnClickListener(this);
        findViewById(R.id.broadcastbt1).setOnClickListener(this);
        findViewById(R.id.ipToAsi).setOnClickListener(this);
        findViewById(R.id.ipToAsi1).setOnClickListener(this);
        findViewById(R.id.dvbc).setOnClickListener(this);
        findViewById(R.id.dvbc1).setOnClickListener(this);
        findViewById(R.id.dvbs).setOnClickListener(this);
        findViewById(R.id.dvbs1).setOnClickListener(this);
        findViewById(R.id.DS3_set).setOnClickListener(this);

        ASI_output.setOnItemSelectedListener(this);
        contellation_style.setOnItemSelectedListener(this);
        inversionNum.setOnItemSelectedListener(this);
        J83_type.setOnItemSelectedListener(this);
        polarizationsp.setOnItemSelectedListener(this);
        dvbs22Ksp.setOnItemSelectedListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            creatThread();//获取信息
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void creatThread() throws ExecutionException, InterruptedException {
        EquipmentParagetTask task = new EquipmentParagetTask(that, myhandler, ViewData);
        task.execute();
    }


    private void inititView() {

        ViewData = new HashMap<>();

        dBhelper = DBhelper.getInstance(that);
        dvbcMap = new HashMap<>();
        dvbsMap = new HashMap<>();

        // ipView
        IPView ipAddr = (IPView) findViewById(R.id.IP);
        maskAddr = (IPView) findViewById(R.id.mask);

        gateAddr = (IPView) findViewById(R.id.Gate);
        trapAddress1 = (IPView) findViewById(R.id.trap1);
        trapAddress2 = (IPView) findViewById(R.id.trap2);
        multicastAddr = (IPView) findViewById(R.id.adressTrap);

        //dvbc layout view initial
        RelativeLayout dvbcParatext = (RelativeLayout) findViewById(R.id.dvbc_text);
        TableRow frequencylayout = (TableRow) findViewById(R.id.frequencylsyout);
        TableRow dvbcSymbolRateLayout = (TableRow) findViewById(R.id.dvbcSymbolRateLayout);
        TableRow J83AnnexLayoutView = (TableRow) findViewById(R.id.J83AnnexLayout);
        TableRow ConstellationLayout = (TableRow) findViewById(R.id.constellationLayout);
        TableRow InversionLayout = (TableRow) findViewById(R.id.inversionLayout);
        TableRow dvbcButtonView = (TableRow) findViewById(R.id.dvbcButton);

        //dvbs layout view initial
        RelativeLayout dvbs_textview = (RelativeLayout) findViewById(R.id.dvbs_text);
        TableRow Dfrequencyview = (TableRow) findViewById(R.id.Dfrequencylayout);
        TableRow Lfrequencyview = (TableRow) findViewById(R.id.Lfrequencylayout);
        TableRow polarizationview = (TableRow) findViewById(R.id.polarizationlayout);
        TableRow dvbs22Kview = (TableRow) findViewById(R.id.dvbs22Klayout);
        TableRow dvbsSymbolRateview = (TableRow) findViewById(R.id.dvbsSymbolRatelayout);
        TableRow dvbsButtonView = (TableRow) findViewById(R.id.dvbsbutton);

        RelativeLayout ds3e3Paratext = (RelativeLayout) findViewById(R.id.ds3e3_text);
        RelativeLayout ds3e3Para = (RelativeLayout) findViewById(R.id.ds3e3_para);


        trap1Enable = (ListView) findViewById(R.id.trap1Enable);
        trap2Enable = (ListView) findViewById(R.id.trap2Enable);

        contellation_style = (Spinner) findViewById(R.id.constellation);
        inversionNum = (Spinner) findViewById(R.id.inversion);
        J83_type = (Spinner) findViewById(R.id.J83Annex);
        ASI_output = (Spinner) findViewById(R.id.ASI_out);
        polarizationsp = (Spinner) findViewById(polarization);
        dvbs22Ksp = (Spinner) findViewById(dvbs22K);


        /*初始化viewpager*/
        pager = (ViewPager) findViewById(R.id.viewpaper);
        PagerTabStrip strip = (PagerTabStrip) findViewById(R.id.tabstrip);

        strip.setDrawFullUnderline(false);

        strip.setBackgroundColor(getResources().getColor(R.color.DimGray));
        strip.setTabIndicatorColor(that.getResources().getColor(R.color.win8_blue));
        strip.setTextSpacing(100);

        //init key - View
        ViewData.put("ipAddr", ipAddr);
        ViewData.put("maskAddr", maskAddr);
        ViewData.put("gateAddr", gateAddr);
        ViewData.put("trapAddress1", trapAddress1);
        ViewData.put("trapAddress2", trapAddress2);
        ViewData.put("multicastAddr", multicastAddr);
        ViewData.put("dvbs_textview", dvbs_textview);
        ViewData.put("Lfrequencyview", Lfrequencyview);
        ViewData.put("dvbsSymbolRateview", dvbsSymbolRateview);
        ViewData.put("polarizationview", polarizationview);
        ViewData.put("dvbs22Kview", dvbs22Kview);
        ViewData.put("Dfrequencyview", Dfrequencyview);
        ViewData.put("dvbsButtonView", dvbsButtonView);
        ViewData.put("dvbcParatext", dvbcParatext);
        ViewData.put("Frequencylayout", frequencylayout);
        ViewData.put("DvbcSymbolRateLayout", dvbcSymbolRateLayout);
        ViewData.put("ConstellationLayout", ConstellationLayout);
        ViewData.put("InversionLayout", InversionLayout);
        ViewData.put("J83AnnexLayoutView", J83AnnexLayoutView);
        ViewData.put("dvbcButtonView", dvbcButtonView);
        ViewData.put("pager", pager);
        ViewData.put("ASI_output", ASI_output);
        ViewData.put("contellation_style", contellation_style);
        ViewData.put("inversionNum", inversionNum);
        ViewData.put("J83_type", J83_type);
        ViewData.put("polarizationsp", polarizationsp);
        ViewData.put("dvbs22Ksp", dvbs22Ksp);
        ViewData.put("trap1Enable", trap1Enable);
        ViewData.put("trap2Enable", trap2Enable);
        ViewData.put("macAddr", findViewById(R.id.Mac));

        ViewData.put("ds3e3Paratext", ds3e3Paratext);
        ViewData.put("ds3e3Para", ds3e3Para);
        EditText text1 = (EditText) findViewById(R.id.Dfrequency);
        EditText text2 = (EditText) findViewById(R.id.Lfrequency);
        EditText text3 = (EditText) findViewById(R.id.dvbsSymbolRate);
        EditText text4 = (EditText) findViewById(R.id.frequency);
        EditText text5 = (EditText) findViewById(R.id.dvbcSymbolRate);
        EditText text6 = (EditText) findViewById(R.id.ipToASIPara);
        EditText text7 = (EditText) findViewById(R.id.port);
        EditText text8 = (EditText) findViewById(R.id.traptime);
        setRegion(text8);
        setRegion(text7);
        setRegion(text6);
        setRegion(text1);
        setRegion(text2);
        setRegion(text3);
        setRegion(text4);
        setRegion(text5);
        ViewData.put("Dfrequency", text1);
        ViewData.put("Lfrequency", text2);
        ViewData.put("dvbsSymbolRate", text3);
        ViewData.put("frequency", text4);
        ViewData.put("dvbcSymbolRate", text5);
        ViewData.put("ipToASIPara", text6);
        ViewData.put("port", text7);
        ViewData.put("trapInterval",text8);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*--------------internet 参数-----------------------*/
            case R.id.internetbt:
                if (helper.getParaStatus(R.id.internetbt)) return;
                String ip = ((IPView) findViewById(R.id.IP)).getValues();
                String mask = ((IPView) findViewById(R.id.mask)).getValues();
                String gate = ((IPView) findViewById(R.id.Gate)).getValues();
                IpAsyncTask task = new IpAsyncTask(that, myhandler);
                task.execute(ip,mask,gate);
                break;
            case R.id.internetbt2:
                manager.addOID(dBhelper.getnetParaOid("maskAddr"));
                manager.addOID(dBhelper.getnetParaOid("gateAddr"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, String> map = manager.getData(myhandler);
                        Bundle bundle = new Bundle();
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            bundle.putString(entry.getKey(), entry.getValue());
                        }
                        Message msg = obtain(myhandler, R.id.internetbt2);
                        msg.setData(bundle);
                        msg.sendToTarget();
                    }
                }).start();

                break;
             /*--------------internet 参数-----------------------*/
              /*--------------trap 参数-----------------------*/
            case R.id.trapbt:
                if (helper.getParaStatus(R.id.trapbt))return;

                String trapip1 = ((IPView) findViewById(R.id.trap1)).getValues();
                String trapip2 = ((IPView) findViewById(R.id.trap2)).getValues();
                String trapinterval = ((EditText) findViewById(R.id.traptime)).getText().toString();
                if ("".equals(trapip1) || "".equals(trapip1) || "".equals(trapinterval)) {
                    Message message = obtain(myhandler, R.id.Nullerror);
                    message.sendToTarget();
                }else {
                    TrapSetAsyncTask trapTask = new TrapSetAsyncTask(that, myhandler);
                    trapTask.execute(trapip1, trapip2, trapinterval);
                }
                break;
            case R.id.trapbt1:
                /*refresh the trap parameter*/
                TrapGetTask tapGet = new TrapGetTask(that, myhandler);
                tapGet.execute();
                break;
             /*--------------trap 参数-----------------------*/
              /*--------------广播 参数-----------------------*/
            case R.id.broadcastbt:
                if (helper.getParaStatus(R.id.broadcastbt)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String tmpstr1 = multicastAddr.getValues();
                        String tmpstr2 = ((EditText) findViewById(R.id.port)).getText().toString();
                        if ("".equals(tmpstr1) || "".equals(tmpstr2)) {
                            message = obtain(myhandler, R.id.Nullerror);
                            message.sendToTarget();
                        } else {
                            Map<String, String> map = new HashMap<>();
                            manager.addOID(dBhelper.getbroadcastParaOid("multicastAddr"));
                            manager.addOID(dBhelper.getbroadcastParaOid("port"));
                            Map<String, String> pre = manager.getData(myhandler);
                            for (String key : pre.keySet()){
                                helper.changeData(key, pre.get(key));
                            }
                            if (!tmpstr1.equals(pre.get("multicastAddr"))) {
                                map.put(dBhelper.getbroadcastParaOid("multicastAddr"),tmpstr1);
                            }
                            if (!tmpstr2.equals(pre.get("port"))) {
                                map.put(dBhelper.getbroadcastParaOid("port"),tmpstr2);
                            }
                            if (map.isEmpty()) return;
                            helper.setParaStatus(R.id.broadcastbt, true);
                            manager.snmpAsynSetList(map, myhandler, R.id.broadcastbt);
                        }
                    }
                }).start();
                break;
            case R.id.broadcastbt1:
                manager.addOID(dBhelper.getbroadcastParaOid("multicastAddr"));
                manager.addOID(dBhelper.getbroadcastParaOid("port"));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> map = manager.getData(myhandler);
                        Bundle broads = new Bundle();
                        for (Map.Entry<String,String> entry : map.entrySet()) {
                            if (!entry.getValue().equals(helper.getData().get(entry.getKey()))) {
                                broads.putString(entry.getKey(), entry.getValue());
                            }
                        }
                        Message msg = obtain(myhandler, R.id.broadcastbt1);
                        msg.setData(broads);
                        msg.sendToTarget();
                    }
                }).start();
                break;
            /*--------------广播 参数-----------------------*/
            /*--------------ip to asi 参数-----------------------*/
            case R.id.ipToAsi:
                if (helper.getParaStatus(R.id.ipToAsi)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = ((EditText) findViewById(R.id.ipToASIPara)).getText().toString();
                        if ("".equals(data)) {
                            message = obtain(myhandler, R.id.Nullerror);
                            message.sendToTarget();
                        } else {
                            String oid = dBhelper.getotherParaOid("ipToASIPara");
                            String res = manager.getsigleData(myhandler, oid);
                            Map<String, String> map = new HashMap<>();
                            map.put(oid, data);
                            if (!data.equals(res)) {
                                helper.setParaStatus(R.id.ipToAsi, true);
                                manager.snmpAsynSetList(map, myhandler, R.id.ipToAsi);
                            }

                        }
                    }
                }).start();
                break;
            case R.id.ipToAsi1:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        message = obtain(myhandler, R.id.ipToAsi1);
                        Bundle bd =new Bundle();
                        String oid = dBhelper.getotherParaOid("ipToASIPara");
                        String res = manager.getsigleData(myhandler, oid);
                        if (!res.equals(((EditText) findViewById(R.id.ipToASIPara)).getText().toString())) {
                            bd.putString("ipToASIPara", res);
                        }
                        oid = dBhelper.getotherParaOid("asiOutputPara");
                        res = manager.getsigleData(myhandler, oid);

                        if (!res.equals(helper.getData().get("asiOutputPara"))) {
                            bd.putString("asiOutputPara", res);
                        }
                        message.setData(bd);
                        message.sendToTarget();
                    }
                }).start();
                break;
            /*--------------asiOut 参数-----------------------*/
            /*--------------dvbc 参数-----------------------*/
            case R.id.dvbc:
                if (helper.getParaStatus(R.id.dvbc)) return;
                String tmpstr1 = ((EditText) findViewById(R.id.frequency)).getText().toString();
                String tmpstr2 = ((EditText) findViewById(R.id.dvbcSymbolRate)).getText().toString();
                if ("".equals(tmpstr1) || "".equals(tmpstr2)) {
                    message = Message.obtain(myhandler, R.id.Nullerror);
                    message.sendToTarget();
                } else {
                    if (!tmpstr1.equals(helper.getData().get("frequency"))) {
                        dvbcMap.put("frequency", tmpstr1);
                    }
                    if (!tmpstr2.equals(helper.getData().get("dvbcSymbolRate"))) {
                        dvbcMap.put("dvbcSymbolRate", tmpstr2);
                    }
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            manager.addOIDs(dBhelper.getdvbcParas().values());
                            Map<String, String> map = manager.getData(myhandler);
                            for (String key : map.keySet()) {
                                if (map.get(key).equals(dvbcMap.get(key))) {
                                    dvbcMap.remove(key);
                                }
                            }
                            if (dvbcMap.isEmpty()) return;
                            Map<String, String> target = new HashMap<>();
                            for (String key : dvbcMap.keySet()) {
                                target.put(dBhelper.getdvbcParaOid(key),dvbcMap.get(key));
                            }
                            helper.setParaStatus(R.id.dvbc, true);
                            manager.snmpAsynSetList(target,myhandler, R.id.dvbc);
                        }
                    };
                    thread.start();

                }

                break;
            case R.id.dvbc1:
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        manager.addOIDs(dBhelper.getdvbcParas().values());
                        Map<String, String> map = manager.getData(myhandler);
                        Bundle bd = new Bundle();
                        for (String key : map.keySet()) {
                            if (!map.get(key).equals(helper.getData().get(key))) {
                                bd.putString(key, map.get(key));
                            }
                        }
                        message = obtain(myhandler, R.id.dvbc1);
                        message.setData(bd);
                        message.sendToTarget();
                    }
                };
                thread.start();
                break;
             /*--------------dvbc 参数-----------------------*/
              /*--------------dvbs 参数-----------------------*/
            case R.id.dvbs:
                if (helper.getParaStatus(R.id.dvbs)) return;
                String data1 = ((EditText) findViewById(R.id.Dfrequency)).getText().toString();
                String data2 = ((EditText) findViewById(R.id.dvbsSymbolRate)).getText().toString();
                String data3 = ((EditText) findViewById(R.id.Lfrequency)).getText().toString();
                if ("".equals(data1) || "".equals(data2) || "".equals(data3)) {
                    message = obtain(myhandler, R.id.Nullerror);
                }
                if (!data1.equals(helper.getData().get("Dfrequency"))) {
                    dvbsMap.put("Dfrequency", data1);
                }
                if (!data2.equals(helper.getData().get("dvbsSymbolRate"))) {
                    dvbsMap.put("dvbsSymbolRate", data2);
                }
                if (!data3.equals(helper.getData().get("Lfrequency"))) {
                    dvbsMap.put("Lfrequency", data3);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        manager.addOIDs(dBhelper.getdvbsParas().values());
                        Map<String, String> map = manager.getData(myhandler);
                        for (String key : map.keySet()) {
                            if (map.get(key).equals(dvbsMap.get(key))) {
                                dvbsMap.remove(key);
                            }
                        }
                        if (dvbsMap.isEmpty()) return;
                        Map<String, String> target = new HashMap<>();
                        for (String key : dvbsMap.keySet()) {
                            target.put(dBhelper.getdvbsParaOid(key),dvbsMap.get(key));
                        }
                        helper.setParaStatus(R.id.dvbs, true);
                        manager.snmpAsynSetList(target,myhandler, R.id.dvbs);
                    }
                }).start();
                break;
            case R.id.dvbs1:
                Thread thread1 = new Thread() {
                    @Override
                    public void run() {
                        manager.addOIDs(dBhelper.getdvbsParas().values());
                        Map<String, String> map = manager.getData(myhandler);
                        Bundle bd = new Bundle();
                        for (String key : map.keySet()) {
                            if (!map.get(key).equals(helper.getData().get(key))) {
                                bd.putString(key, map.get(key));
                            }
                        }
                        message = obtain(myhandler, R.id.dvbs1);
                        message.setData(bd);
                        message.sendToTarget();
                    }
                };
                thread1.start();
                break;
            case R.id.DS3_set:
                if (helper.getParaStatus(R.id.DS3_set)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        manager.addOIDs(dBhelper.getds3e3Paras().values());
                        Map<String, String> pre = manager.getData(myhandler);
                        Map<String, String> target = new HashMap<>();
                        for (String key : pre.keySet()) {
                            if (!helper.getData().get(key).equals(pre.get(key))) {
                                target.put(dBhelper.getds3e3ParaOid(key), pre.get(key));
                            }
                        }
                        if (target.isEmpty()) return;
                        helper.setParaStatus(R.id.DS3_set, true);
                        manager.snmpAsynSetList(target, myhandler, R.id.DS3_set);
                    }
                }).start();
                break;


        }
    }


    public void hideSoftKeyboard() {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.e("sdsd", "hideSoftKeyboard: " + getCurrentFocus());
        try {
            if (getCurrentFocus() != null) {
                im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    /*解决焦点获取问题edittext*/
    public void setupUI(final View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    view.setFocusable(true);
                    view.setFocusableInTouchMode(true);
                    view.requestFocus();
                    return false;
                }
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, final View view, final int position, long id) {
        switch (parent.getId()) {
            case R.id.ASI_out:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String oid = dBhelper.getotherParaOid("asiOutputPara");
                        String value = manager.getsigleData(myhandler, oid);
                        helper.changeData("asiOutputPara", value);
                        String name = dBhelper.getInpustreamType(Long.parseLong(value));
                      //  Log.e(TAG, "run: " + name );
                        String cur = ((TextView)view.findViewById(R.id.check1)).getText().toString();
                       // Log.e(TAG, "run: " + cur );
                        if (!cur.equals(name)) {
                            Map<String, String> map = new HashMap<>();
                            String asiid = dBhelper.getInputStreamID(cur);
                            map.put(dBhelper.getotherParaOid("asiOutputPara"), asiid);
                        //    Log.e(TAG, "run: " + asiid );
                            manager.snmpAsynSetList(map, myhandler, R.id.ASI_out);
                        }

                    }
                }).start();
                break;
            case R.id.constellation:
                dvbcMap.put("constellation", String.valueOf(position + 1));
                break;
            case R.id.inversion:
                dvbcMap.put("inversion",String.valueOf(position));
                break;
            case R.id.J83Annex:
                dvbcMap.put("j83Annex", String.valueOf(position));
                break;
            case polarization:
                dvbsMap.put("polarization",String.valueOf(position));
                break;
            case dvbs22K:
                dvbsMap.put("dvbs22K", String.valueOf(position));
                break;

        }
    }
    /**
     * 设置EditText的输入范围
     * */
    public void setRegion(final EditText text) {



        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start>1){

                    long num=Long.parseLong(s.toString());
                    if (num>Integer.MAX_VALUE){
                        s=String.valueOf(Integer.MAX_VALUE);
                        text.setText(s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null&&!"".equals(s.toString())){
                    long markVal;
                    try {
                        markVal=Long.parseLong(s.toString());
                    }catch (NumberFormatException e){
                        markVal=0;
                    }
                    if (markVal>Integer.MAX_VALUE){
                        Toast.makeText(that, "输入的数值不能超过" + Integer.MAX_VALUE, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void initViewPager() {
        List<String> title = new ArrayList<>();
        title.add("DS3/E3模式选择");
        title.add("DS3/E3选择");
        title.add("协议选择");
        ViewPager pager = (ViewPager) ViewData.get("pager");
        FragmentPagerAdapter adapter = new myFragmentAdapter(that, title);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

    }
}
