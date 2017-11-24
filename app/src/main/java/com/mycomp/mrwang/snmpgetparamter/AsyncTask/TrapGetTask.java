package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.mycomp.mrwang.snmpgetparamter.ui.MyProgressDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;

import java.util.Map;

import static android.os.Message.obtain;

/**
 * Created by wzq on 2017/6/26.
 */

public class TrapGetTask extends AsyncTask<Void,Void,Void> {
    private MyProgressDialog pd;
    private FragmentActivity activity;
    private Handler myhandler;
    private CompatUtils helper;
    private DBhelper dBhelper;
    private SnmpHelper manager;

    public TrapGetTask(FragmentActivity activity, Handler myhandler) {
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
        pd.setMessage("玩命加载中...");
        pd.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String oid = dBhelper.gettrapParaOid("trapInterval");

        while ("noSuchObject".equals(manager.getsigleData(null, oid))) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        manager.addOIDs(dBhelper.gettrapParas().values());
        Map<String, String> tmp = manager.getData(myhandler);
        Bundle bundle = new Bundle();


        for (Map.Entry<String,String> entry : tmp.entrySet()) {
            String key = entry.getKey();
            String val = entry.getValue();
            String pre = (String) helper.getData().get(entry.getKey());
            if (!val.equals(pre)) {
                bundle.putString(key, val);
            }
        }
        Message msg = obtain(myhandler, R.id.trapbt1);
        msg.setData(bundle);
        msg.sendToTarget();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
    }
}
