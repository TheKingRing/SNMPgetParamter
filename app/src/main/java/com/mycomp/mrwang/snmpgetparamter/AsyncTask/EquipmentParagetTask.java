package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import com.example.mylibrary.ip.IPView;
import com.mycomp.mrwang.snmpgetparamter.Adapter.myFragmentAdapter;
import com.mycomp.mrwang.snmpgetparamter.Adapter.myViewAdapter;
import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.mycomp.mrwang.snmpgetparamter.utils.SystemApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import static com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils.getInstance;


/**
 * 该线程是设备参数设置界面初始化数据获得的主要线程。完成设备参数界面中所有数据的
 * 采集以及界面的初始化
 * Created by Mr Wang on 2016/8/29.
 */
public class EquipmentParagetTask extends AsyncTask<Void, Object, List<Map<String, Object>>> {
    private Adapter madapter;
    private Map<String, View> ViewData;
    private MyProgressDialog progressDialog;
    private FragmentActivity activity;
    private DBhelper dBhelper;
    private Map<String, Object> data;
    private CompatUtils helper;
    private SnmpHelper manager;
    private Set<String> exists;
    private Handler myhandler;
    /**
     * 构造器
     *
     * @param activity  :传入的上下文activity
     * @param myhandler : 传入的主线程与UI线程相互通信的handler
     */
    public EquipmentParagetTask(FragmentActivity activity, Handler myhandler, Map<String, View> ViewData) {
        this.activity = activity;
        this.myhandler = myhandler;
        this.ViewData = ViewData;
        helper = getInstance(activity);
        helper.clear();
        data = helper.getData();
        manager = helper.getHelper(activity.getApplicationContext());
        dBhelper = DBhelper.getInstance(activity.getApplicationContext());
        exists = new HashSet<>();
    }


    @Override
    protected List<Map<String, Object>> doInBackground(Void... params) {
        //网络参数
        Map<String, String> tmpmaps = dBhelper.getnetParas();
        manager.addOIDs(tmpmaps.values());
        //告警参数
        tmpmaps = dBhelper.gettrapParas();
        manager.addOIDs(tmpmaps.values());
        //版本参数
        tmpmaps = dBhelper.getversionParas();
        manager.addOIDs(tmpmaps.values());
        //ip receive 参数
        tmpmaps = dBhelper.getipreceiveParas();
        manager.addOIDs(tmpmaps.values());
        // asiOut, ipToAsi, restartEnable
        tmpmaps = dBhelper.getotherParas();
        manager.addOIDs(tmpmaps.values());

        Map<String, String> res = manager.getData(myhandler);
        for (String para : res.keySet()) {
            data.put(para, res.get(para));
        }

        /*得到inpustream存在的输入流*/
        tmpmaps = dBhelper.getInputstreamParas();
        List<Map<String, Object>> l = new ArrayList<>();
        for (String str : tmpmaps.keySet()) {
            String str1 = manager.getsigleData(myhandler,tmpmaps.get(str));
            if (!"-1".equals(str1)) {
                Map<String, Object> map = new HashMap<>();
                if ("0".equals(str1)) {
                    map.put("pic", R.drawable.back2);
                } else {
                    map.put("pic", R.drawable.back1);
                }
                map.put("text", str);
                helper.setAsi_index(str);
                exists.add(str);
                l.add(map);
            }
        }
        //通过输入输出流查看 ds3e3;dvbc;dvbs是否存在
        for (String str : exists) {
            switch (str) {
                case "ds3e3":
                    tmpmaps = dBhelper.getds3e3Paras();
                    manager.addOIDs(tmpmaps.values());
                    break;
                case "dvbc":
                    tmpmaps = dBhelper.getdvbcParas();
                    manager.addOIDs(tmpmaps.values());
                    break;
                case "dvbs":
                    tmpmaps = dBhelper.getdvbsParas();
                    manager.addOIDs(tmpmaps.values());

            }
        }
        res = manager.getData(myhandler);
        for (String key : res.keySet()) {
            String val = res.get(key);
            data.put(key, val);
        }
        return l;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = MyProgressDialog.createDialog(activity, R.drawable.my_anim);
        progressDialog.setTitle("加载中...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


    }
    @Override
    protected void onPostExecute(List<Map<String, Object>> mapList) {
        progressDialog.dismiss();
        try {
            long aisout = Long.parseLong(String.valueOf(data.get("asiOutputPara")));
            //初始化ASI环出
            Spinner ASI_output = (Spinner) ViewData.get("ASI_output");
            madapter = new SimpleAdapter(activity.getApplicationContext(), mapList, R.layout.spinner_item, new String[]{"pic", "text"}, new int[]{R.id.image, R.id.check1});
            ASI_output.setAdapter((SpinnerAdapter) madapter);

            TextView text = (TextView) ViewData.get("macAddr");
            text.setHint((String) data.get("macAddr"));
            text.setFocusable(false);
            text.setFocusableInTouchMode(false);

            // initial IP-View
            helper.initialAddressView((IPView) ViewData.get("ipAddr"), data.get("ipAddr"));
            helper.initialAddressView((IPView) ViewData.get("maskAddr"), data.get("maskAddr"));
            helper.initialAddressView((IPView) ViewData.get("gateAddr"), data.get("gateAddr"));
            helper.initialAddressView((IPView) ViewData.get("trapAddress1"), data.get("trapAddress1"));
            helper.initialAddressView((IPView) ViewData.get("trapAddress2"), data.get("trapAddress2"));
            helper.initialAddressView((IPView) ViewData.get("multicastAddr"), data.get("multicastAddr"));

            ((EditText) ViewData.get("port")).setText((String) data.get("port"));
            ((EditText) ViewData.get("ipToASIPara")).setText((String) data.get("ipToASIPara"));
            ((EditText) ViewData.get("trapInterval")).setText((String) data.get("trapInterval"));

            inittheChoice();

            analyseForPara(exists);
        }catch (Exception e) {
            Log.e("EquipmentParagetTask", "onPostExecute: " + e.getMessage() );
            Message msg = Message.obtain(myhandler, R.id.noresponse, 0);
            msg.sendToTarget();
            SystemApplication.getInstance().exit();
        }

    }

    /**
     * 初始化带有选项的参数
     * （1）trap1
     * (2) trap2
     */
    private void inittheChoice() {
      /*初始化第一个选项处*/
        List<String[]> name = new ArrayList<>();
        name.add(new String[]{"Trap1版本", "V1", "V2c"});
        name.add(new String[]{"Trap1使能", "ON", "OFF"});
        List<Map<String, Object>> list = helper.initialdata(name);
        myViewAdapter madapter1 = new myViewAdapter(activity.getApplicationContext(), list, R.layout.viewadapter_main, helper.getTag(), new int[]{R.id.checkboxfor1, R.id.checkboxfor2, R.id.viewadaptertext1, R.id.viewadaptertext2, R.id.viewadaptertext3}, true);
        ListView view1 = ((ListView) ViewData.get("trap1Enable"));
        view1.setAdapter(madapter1);
        helper.setListViewHeightBasedOnChildren(view1);

         /*初始化第二个选项处*/
        name = new ArrayList<>();
        name.add(new String[]{"Trap2版本", "V1", "V2c"});
        name.add(new String[]{"Trap2使能", "ON", "OFF"});
        list = helper.initialdata(name);
        myViewAdapter madapter2 = new myViewAdapter(activity.getApplicationContext(), list, R.layout.viewadapter_main, helper.getTag(), new int[]{R.id.checkboxfor1, R.id.checkboxfor2, R.id.viewadaptertext1, R.id.viewadaptertext2, R.id.viewadaptertext3}, true);
        ListView view2 = ((ListView) ViewData.get("trap2Enable"));
        view2.setAdapter(madapter2);
        helper.setListViewHeightBasedOnChildren(view2);

    }

    /**
     * 看这个设备是否有（1）ds3e3 (2) dvbc (3) dvbs 这三个参数，有则初始化显示，没有则不初始化且不显示
     */
    private void analyseForPara(Set<String> set) {
        if (set.size() == 0) {
            return;
        }
        for (String str : set) {
            switch (str) {
                case "ds3e3":
                    initViewPager();
                    ViewData.get("ds3e3Paratext").setVisibility(View.VISIBLE);
                    ViewData.get("ds3e3Para").setVisibility(View.VISIBLE);
                    continue;
                case "dvbc":
                    initdvbcLayout();
                    ViewData.get("dvbcParatext").setVisibility(View.VISIBLE);
                    ViewData.get("Frequencylayout").setVisibility(View.VISIBLE);
                    ViewData.get("DvbcSymbolRateLayout").setVisibility(View.VISIBLE);
                    ViewData.get("ConstellationLayout").setVisibility(View.VISIBLE);
                    ViewData.get("InversionLayout").setVisibility(View.VISIBLE);
                    ViewData.get("J83AnnexLayoutView").setVisibility(View.VISIBLE);
                    ViewData.get("dvbcButtonView").setVisibility(View.VISIBLE);
                    continue;
                case "dvbs":
                    initdvbsLayout();
                    ViewData.get("dvbs_textview").setVisibility(View.VISIBLE);
                    ViewData.get("Lfrequencyview").setVisibility(View.VISIBLE);
                    ViewData.get("dvbsSymbolRateview").setVisibility(View.VISIBLE);
                    ViewData.get("polarizationview").setVisibility(View.VISIBLE);
                    ViewData.get("dvbs22Kview").setVisibility(View.VISIBLE);
                    ViewData.get("Dfrequencyview").setVisibility(View.VISIBLE);
                    ViewData.get("dvbsButtonView").setVisibility(View.VISIBLE);
            }
        }
    }

    /* 初始化dvbs布局*/
    private void initdvbsLayout() {
        ((EditText) ViewData.get("Dfrequency")).setText((String) data.get("Dfrequency"));
        ((EditText) ViewData.get("Lfrequency")).setText((String) data.get("Lfrequency"));
        ((EditText) ViewData.get("dvbsSymbolRate")).setText((String) data.get("dvbsSymbolRate"));

        List<String> tmpList = dBhelper.getPolarization();
        String[] Polarizationdata = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) {
            Polarizationdata[i] = tmpList.get(i);
        }
        madapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item1, R.id.item, Polarizationdata);
        Spinner polarizationsp = (Spinner) ViewData.get("polarizationsp");
        polarizationsp.setAdapter((SpinnerAdapter) madapter);
        polarizationsp.setSelection(Integer.parseInt((String) data.get("polarization")));


        tmpList = dBhelper.getDvbs22k();
        Polarizationdata = new String[tmpList.size() - 1];
        for (int i = 0; i < tmpList.size(); i++) {
            Polarizationdata[i] = tmpList.get(i);
        }
        madapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item1, R.id.item, Polarizationdata);
        Spinner dvbs22Ksp = (Spinner) ViewData.get("dvbs22Ksp");
        dvbs22Ksp.setAdapter((SpinnerAdapter) madapter);
        dvbs22Ksp.setSelection(Integer.parseInt((String) data.get("dvbs22K")));


    }

    /*初始化dvbc布局*/
    private void initdvbcLayout() {
        ((EditText) ViewData.get("frequency")).setText((String) data.get("frequency"));
        ((EditText) ViewData.get("dvbcSymbolRate")).setText((String) data.get("dvbcSymbolRate"));

        List<String> tmpList = dBhelper.getConstellation();
        String[] arr = new String[tmpList.size() - 1];
        for (int i = 1; i < tmpList.size(); i++) {
            arr[i - 1] = tmpList.get(i);
        }
        madapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item1, R.id.item, arr);
        Spinner contellation_style = (Spinner) ViewData.get("contellation_style");
        contellation_style.setAdapter((SpinnerAdapter) madapter);
        int choice = Integer.parseInt(String.valueOf(data.get("constellation"))) - 1;
        contellation_style.setSelection(choice);

        tmpList = dBhelper.getInversion();
        arr = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) {
            arr[i] = tmpList.get(i);
        }
        madapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item1, R.id.item, arr);
        Spinner inversionNum = (Spinner) ViewData.get("inversionNum");
        inversionNum.setAdapter((SpinnerAdapter) madapter);
        inversionNum.setSelection(Integer.parseInt((String) data.get("inversion")));


        tmpList = dBhelper.getJ83Annex();
        arr = new String[tmpList.size()];
        for (int i = 0; i < tmpList.size(); i++) {
            arr[i] = tmpList.get(i);
        }
        madapter = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.spinner_item1, R.id.item, arr);
        Spinner J83_type = (Spinner) ViewData.get("J83_type");
        J83_type.setAdapter((SpinnerAdapter) madapter);
        J83_type.setSelection(Integer.parseInt((String) data.get("j83Annex")));


    }/*初始化viewpaer里的view1*/

    private void initViewPager() {
        List<String> title = new ArrayList<>();
        title.add("DS3/E3模式选择");
        title.add("DS3/E3选择");
        title.add("协议选择");
        ViewPager pager = (ViewPager) ViewData.get("pager");
        FragmentPagerAdapter adapter = new myFragmentAdapter(activity, title);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);

    }

}
