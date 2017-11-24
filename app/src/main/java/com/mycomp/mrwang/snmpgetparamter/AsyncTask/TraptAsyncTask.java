package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;

import java.util.Map;

/**
 * 异步线程获取告警信息
 * Created by wzq on 2017/6/15.
 */

public class TraptAsyncTask extends AsyncTask<String,Integer, Void> {
  //  private final String TAG = "TraptAsyncTask";
    private Context context;
    private SnmpHelper manager;
    private DBhelper dbhelper;
    private CompatUtils helper;
    private ProgressDialog pd;
    private int pdStatus = 0;
    public TraptAsyncTask(Context context) {
        this.context = context;
        dbhelper = DBhelper.getInstance(context);
        helper = CompatUtils.getInstance(context);
        manager = helper.getHelper(context);
    }

    @Override
    protected Void doInBackground(String... para) {
        String oid = para[0];
        while ("noSuchObject".equals(manager.getsigleData(null, oid))) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pdStatus += 5;
            publishProgress(pdStatus);
        }
        manager.addOIDs(dbhelper.gettrapParas().values());
        Map<String, String> cur = manager.getData(null);
        if (cur != null) {
            helper.setData(cur);
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setMax(100);
        pd.setTitle("正在配置,请等待...");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(false);
        pd.incrementProgressBy(-pd.getProgress());
        pd.show();
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress=values[0];
        pd.setProgress(progress);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        pd.dismiss();
    }
}
