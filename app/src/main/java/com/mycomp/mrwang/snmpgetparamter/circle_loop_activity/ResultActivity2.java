package com.mycomp.mrwang.snmpgetparamter.circle_loop_activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.ryg.dynamicload.DLBasePluginActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * 修改decoder2 pcr_pid，video pid , 和 audio_pid界面
 * Created by Mr Wang on 2016/6/8.
 */
public class ResultActivity2 extends DLBasePluginActivity implements View.OnClickListener {
    Button bt1;
    Button bt2;

    private SnmpHelper manager;
    private CompatUtils helper;
    private DBhelper dbhelper;
    EditText text1;
    EditText text2;
    EditText text3;
    private Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.noresponse) {
                Toast.makeText(that, "failed to setPara", Toast.LENGTH_SHORT).show();
            }else if (msg.what ==  R.id.builderbt1) {
                helper.changeData(msg.getData());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.builder_view);
        helper = CompatUtils.getInstance(that);
        dbhelper = DBhelper.getInstance(that);
        manager = helper.getHelper(that);
        initialView();
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
    }

    void initialView() {
        text1= (EditText) findViewById(R.id.PCR_PID1);
        text2= (EditText) findViewById(R.id.Video_PID1);
        text3= (EditText) findViewById(R.id.Audio_PID1);
        bt1= (Button) findViewById(R.id.builderbt1);
        bt2= (Button) findViewById(R.id.builderbt2);
        text1.setText(String.valueOf( helper.getData().get("D2pcrpid")));
        text2.setText(String.valueOf( helper.getData().get("D2videopid")));
        text3.setText(String.valueOf( helper.getData().get("D2audiopid")));
        setRegion(text1);
        setRegion(text2);
        setRegion(text3);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.builderbt1:
                if (helper.getParaStatus(R.id.builderbt1)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> map = new HashMap<>();
                        map.put(dbhelper.getdecoder2Oid("D2pcrpid"), text1.getText().toString());
                        map.put(dbhelper.getdecoder2Oid("D2videopid"), text2.getText().toString());
                        map.put(dbhelper.getdecoder2Oid("D2audiopid"), text3.getText().toString());
                        helper.setParaStatus(R.id.builderbt1, true);
                        manager.snmpAsynSetList(map, myhandler, R.id.builderbt1);
                    }
                }).start();
                finish();
                break;
            case R.id.builderbt2:
                finish();
                break;
        }
    }
    /**
     * 设置EditText的输入范围
     * */
    public void setRegion(final EditText text) {



        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start>1){

                    int num=Integer.parseInt(s.toString());
                    if (num>8191){
                        s=String.valueOf(8191);
                        text.setText(s);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s!=null&&!"".equals(s.toString())){
                    int markVal;
                    try {
                        markVal=Integer.parseInt(s.toString());
                    }catch (NumberFormatException e){
                        markVal=0;
                    }
                    if (markVal>8191){
                        Toast.makeText(that, "输入的数值不能超过8191", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
