package com.mycomp.mrwang.snmpgetparamter.circle_loop_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.mycomp.mrwang.snmpgetparamter.Adapter.trap_list_adapter;
import com.mycomp.mrwang.snmpgetparamter.utils.SystemManager;
import com.mycomp.mrwang.snmpgetparamter.utils.TrapReciever;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.ryg.dynamicload.DLBasePluginActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 告警信息界面
 * Created by wzq on 2017/6/16.
 */

public class TrapMessageActivity extends DLBasePluginActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {
    private ListView messages;
    private boolean tag1 = true;//监视是否已经开始监听
    private boolean tag2 = true;//监视是否长按
   // private final String TAG = "TrapMessageActivity";
    private trap_list_adapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.trapMessageInit:
                    adapter = new trap_list_adapter(that, data);
                    messages.setAdapter(adapter);
                    break;
                case R.id.trapMessageGet:
                    if (tag2) {
                        adapter.notifyDataSetInvalidated();
                    }
                    tag2 = true;
                    break;
                case R.id.clear_stop:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private TrapReciever reciever;

    private List<Map<String, String>> data;
    private LinkedList<Map<String, String>> queue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trap_activity);

        String apkRoot="chmod 777 "+getPackageCodePath();

        SystemManager.RootCommand(apkRoot);

        messages = (ListView) findViewById(R.id.trapMessage);
        messages.setOnItemLongClickListener(this);
        (findViewById(R.id.trap_begin)).setOnClickListener(this);
        (findViewById(R.id.trap_end)).setOnClickListener(this);
        (findViewById(R.id.clear)).setOnClickListener(this);
        data = new ArrayList<>();
        queue = new LinkedList<>();
        reciever = new TrapReciever(that, data,queue,handler);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trap_begin:
                if (tag1) {
                    Thread listen = new Thread(reciever);
                    listen.start();
                    tag1 = false;
                }else {
                    Toast.makeText(that, "主人，我已经开始监听啦~", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.trap_end:
                if (!tag1) {
                    reciever.stop();
                    tag1 = true;
                }

                break;
            case R.id.clear:
                if (!data.isEmpty()) {
                    reciever.stop();
                    tag1 = true;
                    data.clear();
                    Message msg = Message.obtain(handler, R.id.clear_stop);
                    msg.sendToTarget();
                }
                break;

        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        tag2 = false;
        Map<String, String> map = queue.get(position);
        Intent intent = new Intent(TrapMessageActivity.this, DetialsActivity.class);
        for (String key : map.keySet()) {
            intent.putExtra(key,map.get(key));
        }
        startActivity(intent);
        return true;
    }

}
