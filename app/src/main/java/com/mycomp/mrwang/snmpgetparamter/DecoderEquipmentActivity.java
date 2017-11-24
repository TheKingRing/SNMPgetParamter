package com.mycomp.mrwang.snmpgetparamter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.mycomp.mrwang.snmpgetparamter.AsyncTask.Decoder_Exists_Analyse;
import com.mycomp.mrwang.snmpgetparamter.circle_loop_activity.Decoder1Activity;
import com.mycomp.mrwang.snmpgetparamter.circle_loop_activity.Decoder2Activity;
import com.mycomp.mrwang.snmpgetparamter.circle_loop_activity.EquipmentParameter;
import com.mycomp.mrwang.snmpgetparamter.circle_loop_activity.TrapMessageActivity;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.ContralApp;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SystemApplication;
import com.ryg.dynamicload.DLBasePluginActivity;
import com.ryg.dynamicload.internal.DLIntent;


/**
 * 入口界面
 * Created by Mr Wang on 2016/6/8.
 */
public class DecoderEquipmentActivity extends DLBasePluginActivity implements View.OnClickListener{
   // private static final String TAG = "Activity";
    Button bt1;
    Button bt2;
    Button bt3;
    Button bt4;
    private CompatUtils helper;
    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.noresponse) {
                Toast.makeText(that, "请退出重新加载", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decoder_main);
//        Intent dlIntent =  getIntent(); // 注意这里用的是Intent 不是DLIntent
//        helper = CompatUtils.getInstance(that);
//        String ip = dlIntent.getStringExtra("IP");
//        helper.setIP(ip);
        DBhelper dBhelper = DBhelper.getInstance(that);
        if (dBhelper.gettrapParas().containsKey("trapPort1") || dBhelper.gettrapParas().containsKey("trapPort2")) {
            bt4.setVisibility(View.VISIBLE);
        }
        bt1= (Button) findViewById(R.id.decoder1);
        bt2= (Button) findViewById(R.id.decoder2);
        bt3= (Button) findViewById(R.id.equipment);
        bt4 = (Button)findViewById(R.id.traprecive);
        bt3.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt4.setOnClickListener(this);
        SystemApplication.getInstance().addActivity(that);
        initialView();

    }

    private void initialView() {
        Decoder_Exists_Analyse task = new Decoder_Exists_Analyse(that, bt1, bt2, myhandler);
        task.execute();
    }

    @Override
    public void onRestart() {
        helper = CompatUtils.getInstance(that);
        super.onRestart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.decoder1:
                DLIntent intent=new DLIntent(getPackageName(), Decoder1Activity.class);
                startPluginActivity(intent);
                break;
            case R.id.decoder2:
                DLIntent intent1=new DLIntent(getPackageName(), Decoder2Activity.class);
                startPluginActivity(intent1);
                break;
            case R.id.equipment:
                DLIntent intent3=new DLIntent(getPackageName(), EquipmentParameter.class);
                startPluginActivity(intent3);
                break;
            case R.id.traprecive:
                DLIntent intent4=new DLIntent(getPackageName(), TrapMessageActivity.class);
                startPluginActivity(intent4);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
        ContralApp.closeConnection();

    }
}
