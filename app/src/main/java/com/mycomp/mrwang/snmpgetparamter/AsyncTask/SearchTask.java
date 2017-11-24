package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;


import com.mycomp.mrwang.snmpgetparamter.Adapter.listviewAdapter;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 该线程用于搜索电视节目
 * Created by Mr Wang on 2016/6/18.
 */
public class SearchTask extends AsyncTask<Object,Integer,Adapter> {
    private final String TAG = "SearchTask";
    private Context context;
    private ListView progranmList;
    private ProgressDialog progressDialog;
    private CompatUtils helper;
    private SnmpHelper manager;
    private DBhelper dBhelper;
    private Handler myhandler;

    public SearchTask(Context context, ListView progranmList, Handler myhandler) {
        this.myhandler = myhandler;
        this.context=context;
        this.progranmList=progranmList;
        helper = CompatUtils.getInstance(context);
        manager = helper.getHelper(context);
        dBhelper = DBhelper.getInstance(context);
    }

    @Override
    protected void onPreExecute() {
        try {
            progressDialog=new ProgressDialog(context);
            progressDialog.setTitle("DownLoading....");
            progressDialog.setMax(253);  //设置进度条的最大值
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }catch (Exception e) {
            Log.e(TAG, "onPreExecute: " + e.getMessage() );
        }



    }

    @Override
    protected Adapter doInBackground(Object... params) {
        Map<Integer, String> map = new HashMap<>();
        switch ((String)params[0]){
            case "Decoder1":
                String oid1= dBhelper.getdecoder1Oid("D1currentprogram");
                String res1 = manager.getsigleData(myhandler, oid1);
                if (res1 == null) {
                    Message msg = Message.obtain(myhandler, R.id.stop);
                    msg.sendToTarget();
                    return null;
                }
                int current = Integer.parseInt(res1);
                if (current ==254){
                    helper.setCurrent_program(0);
                }else {
                    helper.setCurrent_program(current);
                }
                String oid= dBhelper.getdecoder1Oid("D1programName");
                int Number=0;
                String program = manager.getsigleData(myhandler, oid + Number);
                if (program == null) {
                    Message msg = Message.obtain(myhandler, R.id.stop);
                    msg.sendToTarget();
                    return null;
                }
                if (program.contains(":")) {
                    program = new String(helper.hexStringToBytes(program.replaceAll(":", ""))).trim();
                }

                synchronized (this){
                    while (!("noSuchInstance").equals(program)){
                        map.put(Number,program);
                        try {
                            wait(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(Number);
                        Number++;
                        program = manager.getsigleData(myhandler, oid + Number);
                        if (program == null) {
                            Message msg = Message.obtain(myhandler, R.id.stop);
                            msg.sendToTarget();
                            return null;
                        }
                    }
                }
                break;
            case "Decoder2":
                String oid2 = dBhelper.getdecoder2Oid("D2currentprogram");
                String res2 = manager.getsigleData(myhandler, oid2);
                if (res2 == null) {
                    Message msg = Message.obtain(myhandler, R.id.stop);
                    msg.sendToTarget();
                    return null;
                }
                int current2 = Integer.parseInt(res2);
                if (current2 ==254){
                    helper.setCurrent_program(0);
                }else {
                    helper.setCurrent_program(current2);
                }

                oid2= dBhelper.getdecoder2Oid("D2programName");
                int Number2=0;

                String program2 = manager.getsigleData(myhandler, oid2 + Number2);
                if (program2 == null) {
                    Message msg = Message.obtain(myhandler, R.id.stop);
                    msg.sendToTarget();
                    return null;
                }
                if (program2.contains(":")) {
                    program2 = new String(helper.hexStringToBytes(program2.replaceAll(":", ""))).trim();
                }

                synchronized (this){
                    while (!"noSuchInstance".equals(program2)){
                        map.put(Number2,program2);
                        try {
                            wait(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        publishProgress(Number2);
                        Number2++;
                        program2 = manager.getsigleData(myhandler, oid2 + Number2);
                        if (program2 == null) {
                            Message msg = Message.obtain(myhandler, R.id.stop);
                            msg.sendToTarget();
                            return null;
                        }
                    }
                }
                break;
        }
        helper.getData().put("current_number", helper.getCurrent_program());

        return new listviewAdapter(context, map);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress=values[0];
        progressDialog.setProgress(progress);
    }

    @Override
    protected void onPostExecute(Adapter adapter) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progranmList.setAdapter((ListAdapter) adapter);
    }

}
