package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import android.view.View;
import android.widget.Button;


import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;

import java.util.Map;



/**
 * 异步线程获取decoder1 与 decoder2是否存在
 * Created by wzq on 2017/6/15.
 */

public class Decoder_Exists_Analyse extends AsyncTask<Void, Void,Map<String, String>> {
    // private final String TAG = "Decoder_Exists_Analyse";
    private Context context;
    private MyProgressDialog pd;
    private SnmpHelper manager;
    private DBhelper dBhelper;
    private Button bt1;
    private Button bt2;
    private Handler handler;
    public Decoder_Exists_Analyse(Context context, Button bt1, Button bt2, Handler handler) {
        this.context = context;
        this.bt1 = bt1;
        this.bt2 = bt2;
        this.handler = handler;
        CompatUtils helper = CompatUtils.getInstance(context);
        manager = helper.getHelper(context);
        dBhelper= DBhelper.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        pd = MyProgressDialog.createDialog(context, R.drawable.my_anim);
        pd.setTitle("loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    @Override
    protected Map<String, String> doInBackground(Void... params) {
        String Exist1 = dBhelper.getdecoder1Oid("D1existflag");
        String Exist2 = dBhelper.getdecoder2Oid("D2existflag");
        manager.addOID(Exist1);
        manager.addOID(Exist2);
        return manager.getData(handler);
    }

    @Override
    protected void onPostExecute(Map<String, String> res) {
        pd.dismiss();
        String num;
        int arg1 =  (num = res.get("D1existflag")) == null ? 0:Integer.parseInt(num);
        int arg2 =  (num = res.get("D2existflag")) == null ? 0:Integer.parseInt(num);
        if (arg1 == 1 && arg2 == 1) {
            bt1.setVisibility(View.VISIBLE);
            bt2.setVisibility(View.VISIBLE);
        }else if (arg1 == 1) {
            bt1.setVisibility(View.VISIBLE);
        }else if (arg2 == 1) {
            bt2.setVisibility(View.VISIBLE);
        }
    }
}
