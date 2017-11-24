package com.mycomp.mrwang.snmpgetparamter.AsyncTask;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 该线程用于判断更改完节目输出流后，是否已经配置完毕
 * Created by wzq on 2017/6/15.
 */

public class Decoder_Inputstream_Set extends AsyncTask<String,Void,Void> {
    private final String TAG = "Decoder_Inputstream_Set";
    private MyProgressDialog pd;
    private Context context;
    private DBhelper dBhelper;
    private SnmpHelper manager;
    private Handler handler;
    public Decoder_Inputstream_Set(Context context, Handler searchHanler){
        this.handler = searchHanler;
        this.context = context;
        dBhelper = DBhelper.getInstance(context);
        CompatUtils helper = CompatUtils.getInstance(context);
        manager = helper.getHelper(context);
    }

    @Override
    protected void onPreExecute() {
        pd = MyProgressDialog.createDialog(context ,R.drawable.my_anim);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("请耐心等待...");
        pd.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String tag = params[0];
        Map<String, String> map = new HashMap<>();
        switch (tag) {
            case "Decoder1":
                Log.e(TAG, "doInBackground: " + params[1] );
                map.put(dBhelper.getdecoder1Oid("D1inputchoose"), params[1]);
                manager.snmpAsynSetList(map, handler, R.id.input_Stream);
                String oid = dBhelper.getdecoder1Oid("D1searchEnable");
                while ("1".equals(manager.getsigleData(null, oid))) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = Message.obtain(handler, R.id.serachchaged);
                msg.sendToTarget();
                break;
            case "Decoder2":
                map.put(dBhelper.getdecoder1Oid("D2inputchoose"), params[1]);
                manager.snmpAsynSetList(map, handler, R.id.input_Stream);
                String oid2 = dBhelper.getdecoder2Oid("D2searchEnable");
                while ("1".equals(manager.getsigleData(null, oid2))) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg2 = Message.obtain(handler, R.id.serachchaged);
                msg2.sendToTarget();
                break;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        pd.dismiss();
    }
}
