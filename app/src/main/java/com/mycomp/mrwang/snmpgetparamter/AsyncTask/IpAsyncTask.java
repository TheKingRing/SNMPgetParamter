package com.mycomp.mrwang.snmpgetparamter.AsyncTask;

import android.app.ProgressDialog;
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

import java.io.IOException;
import java.net.InetAddress;

import java.util.HashMap;
import java.util.Map;

import static android.os.Message.obtain;

/**
 * 该线程为IP设置时的线程
 * 作用：判断IP是否已经被设置，用isreach（）函数
 * 如果ip两次都不能ping通，则该IP没有设备，可以使用
 * Created by wzq on 2017/6/22.
 */

public class IpAsyncTask extends AsyncTask<String, Void,Void> {
    private final String TAG = "IpAsyncTask";
    private Context context;
    private MyProgressDialog pd;
    private SnmpHelper manager;
    private CompatUtils helper;
    private DBhelper dBhelper;
    private Handler handler;
    public IpAsyncTask(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        helper = CompatUtils.getInstance(context);
        dBhelper = DBhelper.getInstance(context);
        manager = helper.getHelper(context);
    }
    @Override
    protected void onPreExecute() {
        pd = MyProgressDialog.createDialog(context, R.drawable.my_anim);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("检测IP...");
        pd.show();
    }
    /**
     * @param params : par
     * */
    @Override
    protected Void doInBackground(String... params) {
        String ip = params[0];
        String mask = params[1];
        String gate = params[2];
        Map<String, String> map = new HashMap<>();
        if ("".equals(ip) || "".equals(mask) || "".equals(gate)) {
            Message message = obtain(handler, R.id.Nullerror);
            message.sendToTarget();
        }else if (!helper.checkMask(mask)) {
            Message message = obtain(handler, R.id.maskerror);
            message.sendToTarget();
        } else if (!helper.checkIP(ip, mask, gate)){
            Message message = obtain(handler, R.id.disMatch);
            message.sendToTarget();
        }else {
            boolean tag = false;
            if (!ip.equals(helper.getData().get("ipAddr"))){
                try {
                    Log.e(TAG, "doInBackground: " + ip);
                    InetAddress address = InetAddress.getByName(ip);
                    if (!address.isReachable(3000) && !address.isReachable(3000)) {
                        tag = true;
                    }
                    if (tag) {
                        Message msg = obtain(handler,R.id.ip_check_finished,pd);
                        msg.sendToTarget();
                        map.put(dBhelper.getnetParaOid("ipAddr"), ip);
                    }else {
                        Message msg = obtain(handler, R.id.ip_being_used,pd);
                        msg.sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!mask.equals(helper.getData().get("maskAddr"))) {
                map.put(dBhelper.getnetParaOid("maskAddr"), mask);
            }
            if (!gate.equals(helper.getData().get("gateAddr"))) {
                map.put(dBhelper.getnetParaOid("gateAddr"), gate);
            }
            helper.setParaStatus(R.id.internetbt, true);
            manager.snmpAsynSetList(map,handler,R.id.internetbt);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (pd.isShowing()) {
            pd.dismiss();
        }
    }
}
