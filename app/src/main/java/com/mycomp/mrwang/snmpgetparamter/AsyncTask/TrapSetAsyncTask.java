package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;

import java.util.HashMap;
import java.util.Map;

import static android.os.Message.obtain;

/**
 * Created by wzq on 2017/6/26.
 */

public class TrapSetAsyncTask extends AsyncTask<String, Void, Void> {
    private FragmentActivity activity;
    private MyProgressDialog pd;
    private Handler myhandler;
    private CompatUtils helper;
    private DBhelper dBhelper;
    private SnmpHelper manager;

    public TrapSetAsyncTask(FragmentActivity activity, Handler myhandler) {
        this.activity = activity;
        this.myhandler = myhandler;
        helper = CompatUtils.getInstance(activity);
        dBhelper = DBhelper.getInstance(activity);
        manager = helper.getHelper(activity);
    }

    @Override
    protected void onPreExecute() {
        pd = MyProgressDialog.createDialog(activity, R.drawable.my_anim);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("设置中...");
        pd.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        String trapip1 = params[0];
        String trapip2 = params[1];
        String trapinterval = params[2];
        Map<String, String> data = new HashMap<>();
        manager.addOIDs(dBhelper.gettrapParas().values());
        Map<String, String> pre = manager.getData(myhandler);
        for (String key : pre.keySet()) {
            if (key.equals("trapAddress1") && !trapip1.equals(pre.get("trapAddress1"))) {
                data.put(dBhelper.gettrapParaOid("trapAddress1"), trapip1);
            }else if (key.equals("trapAddress2") && !trapip2.equals(pre.get("trapAddress1"))) {
                data.put(dBhelper.gettrapParaOid("trapAddress2"), trapip2);
            }else if (key.equals("trapInterval") && !trapinterval.equals(pre.get("trapInterval"))) {
                data.put(dBhelper.gettrapParaOid("trapInterval"), trapinterval);
            }else if (!helper.getData().get(key).equals(pre.get(key))) {
                data.put(dBhelper.gettrapParaOid(key), (String) helper.getData().get(key));
            }
        }
        if (data.isEmpty()) return null;
        Log.e("hahaha", "doInBackground: " + data );
        helper.setParaStatus(R.id.trapbt, true);
        manager.snmpAsynSetList(data, myhandler, R.id.trapbt);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
    }
}
