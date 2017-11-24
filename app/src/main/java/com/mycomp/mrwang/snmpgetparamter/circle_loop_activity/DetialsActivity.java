package com.mycomp.mrwang.snmpgetparamter.circle_loop_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mycomp.mrwang.snmpgetparamter.R;
import com.ryg.dynamicload.DLBasePluginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * trap告警信息细节
 * Created by wzq on 2017/6/21.
 */

public class DetialsActivity extends DLBasePluginActivity {
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        list = (ListView) findViewById(R.id.details_Message);
        Intent intent = getIntent();
        Bundle bd = intent.getExtras();
        List<Map<String, String>> data = new ArrayList<>();
        for (String key: bd.keySet()) {
            Map<String, String> map = new HashMap<>();
            map.put("text", key);
            map.put("val", bd.getString(key));
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(that,data,R.layout.details_listview,new String[]{"text", "val"}, new int[]{R.id.trap_details_parameter, R.id.trap_detials_value});
        list.setAdapter(adapter);
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
