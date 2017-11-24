package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder1Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder2Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.inputExistPara;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 该线程用于获取解码器参数
 */
public class SnmpGetTask extends AsyncTask<Object, Void, Map<String, Object>> {
    private final String TAG = "snmpGet";
    private DBhelper dBhelper;
    private SnmpHelper manager;
    private List<Map<String, Object>> data = new ArrayList<>();

    private MyProgressDialog dialog;

    private Map<String, String> DE200InputStreamStatus; //存储输入流的状态

    private Adapter InputStreamAapter;//建立inputstream适配器

    private Context context;//上下文对象
    private Handler myhandler;

    private Spinner input_Stream;
    private SeekBar Volume;
    private TextView textView;
    private CompatUtils helper;

    public SnmpGetTask(Context context, Spinner input_Stream, SeekBar volume, TextView textView, Handler myhandler) {
        dialog = MyProgressDialog.createDialog(context, R.drawable.my_anim);
        this.context = context;
        this.input_Stream = input_Stream;
        this.Volume = volume;
        this.textView = textView;
        this.myhandler = myhandler;
        helper = CompatUtils.getInstance(context);
        manager = helper.getHelper(context);
        helper.clear();
        dBhelper = DBhelper.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("正在努力加载....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Map<String, Object> doInBackground(Object... para) {
        DE200InputStreamStatus = new HashMap<>();
        Set<String> inputLock = new HashSet<>();

        createtheStatuMap(manager);

        switch ((String) para[0]) {
            case "Decoder1":
                List<decoder1Para> list = dBhelper.loadAlldecoder1Para();
                for (decoder1Para session : list) {
                    if ("D1programName".equals(session.getParameter()) || "D1currentprogram".equals(session.getParameter())) {
                        continue;
                    }
                    manager.addOID(session.getOid());
                }
                Map<String, String> tmp1 = manager.getData(myhandler);
                for (String name : tmp1.keySet()) {
                    String val = tmp1.get(name);
                    helper.getData().put(name, val);
                }
                List<String> tmplist = new ArrayList<>();
                for (String type : DE200InputStreamStatus.keySet()) {
                    String tmpResult = DE200InputStreamStatus.get(type);
                    switch (tmpResult) {
                        case "0":
                            Log.e(TAG, "doInBackground: case 0" + tmpResult);
                            tmplist.add(type);
                            break;
                        case "1":
                            Log.e(TAG, "doInBackground: case 1" + tmpResult);
                            tmplist.add(type);
                            inputLock.add(type);
                            break;
                    }

                }
                for (String i : tmplist) {
                    Map<String, Object> map = new HashMap<>();
                    if (inputLock.contains(i)) {
                        map.put("pic", R.drawable.back1);
                    } else {
                        map.put("pic", R.drawable.back2);
                    }
                    String name = i.substring(0, i.indexOf("Exist"));
                    map.put("text", name);
                    helper.setInputStream_index(name);
                    data.add(map);
                }
                break;
            case "Decoder2":
                List<decoder2Para> list2 = dBhelper.loadAlldecoder2Para();
                for (decoder2Para session : list2) {
                    if ("D2programName".equals(session.getParameter()) || "D2currentprogram".equals(session.getParameter())) {
                        continue;
                    }
                    manager.addOID(session.getOid());

                }
                Map<String, String> tmp2 = manager.getData(myhandler);
                for (String name : tmp2.keySet()) {
                    String val = tmp2.get(name);
                    helper.getData().put(name, val);
                }
                List<String> tmplist2 = new ArrayList<>();
                for (String type : DE200InputStreamStatus.keySet()) {
                    String tmpResult = DE200InputStreamStatus.get(type);
                    switch (tmpResult) {
                        case "0":
                            tmplist2.add(type);
                            break;
                        case "1":
                            tmplist2.add(type);
                            inputLock.add(type);
                            break;
                    }

                }

                for (String i : tmplist2) {
                    Map<String, Object> map = new HashMap<>();
                    if (inputLock.contains(i)) {
                        map.put("pic", R.drawable.back1);
                    } else {
                        map.put("pic", R.drawable.back2);
                    }
                    String name = i.substring(0, i.indexOf("Exist"));
                    map.put("text", name);
                    helper.setInputStream_index(name);
                    data.add(map);
                }
        }
        helper.getData().put("TAG", para[0]);
        InputStreamAapter = new SimpleAdapter(context, data, R.layout.spinner_item, new String[]{"pic", "text"}, new int[]{R.id.image, R.id.check1});
        return helper.getData();
    }


    /**
     * 检测输入流的锁定情况；建立map
     * 参数1：输入流名称
     * 参数2：输入流状态：不存在（-1），锁定（1），未锁定（0）
     * *
     *
     * @param manager: Snmp helper
     */
    private void createtheStatuMap(SnmpHelper manager) {
        List<inputExistPara> list = dBhelper.loadAllinputExistParaDao();
        for (inputExistPara session : list) {
            manager.addOID(session.getOid());
        }

        Map<String, String> res = manager.getData(myhandler);
        for (String key : res.keySet()) {
            String val = res.get(key);
            if (!"-1".equals(val)) {
                DE200InputStreamStatus.put(key, val);
            }
        }
    }


    protected void onPostExecute(Map<String, Object> displayString) {
        dialog.dismiss();
        displayResult(displayString);

    }

    private void displayResult(Map<String, Object> result) {
        String current_inputstream;
        if ("Decoder1".equals(result.get("TAG"))) {
            current_inputstream = (String) result.get("D1inputchoose");
        } else {
            current_inputstream = (String) result.get("D2inputchoose");
        }
        input_Stream.setAdapter((SpinnerAdapter) InputStreamAapter);
        for (int k = 0; k < InputStreamAapter.getCount(); k++) {
            View view = InputStreamAapter.getView(k, null, null);
            TextView textView = (TextView) view.findViewById(R.id.check1);
            if (textView.getText().toString().equals(dBhelper.getInpustreamType(Long.parseLong(current_inputstream)))) {
                input_Stream.setSelection(k);
            }
        }
        int volume;
        if ("Decoder1".equals(result.get("TAG"))) {
            volume = Integer.parseInt((String) result.get("D1volume"));
        } else {
            volume = Integer.parseInt((String) result.get("D2volume"));
        }

        textView.setText(String.valueOf(volume));
        Volume.setProgress(volume);

    }

}