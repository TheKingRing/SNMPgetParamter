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
 * 修改decoder1 pcr_pid，video pid , 和 audio_pid界面
 * Created by Mr Wang on 2016/5/27.
 */
public class ResultActivity1 extends DLBasePluginActivity implements View.OnClickListener{

   // private static final String TAG = "ResultActivity1";
    private DBhelper dBhelper;
	private CompatUtils helper;
    private SnmpHelper manager;
    EditText text1;
    EditText text2;
    EditText text3;

    Button bt1;
    Button bt2;
    Handler myhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.noresponse) {
                Toast.makeText(that, "failed to setPara", Toast.LENGTH_SHORT).show();
            }else if (msg.what ==  R.id.builderbt1) {
				helper.changeData(msg.getData());
			}else if (msg.what == R.id.changed) {
				text1.setText(String.valueOf(helper.getData().get("D1pcrpid")));
				text2.setText(String.valueOf(helper.getData().get("D1videopid")));
				text3.setText(String.valueOf(helper.getData().get("D1audiopid")));
			}
        }
    };

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.builder_view);
		helper = CompatUtils.getInstance(that);
        dBhelper = DBhelper.getInstance(that);
		manager = helper.getHelper(that);
        initialView();
		bt1.setOnClickListener(this);
		bt2.setOnClickListener(this);

    }

    void initialView() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				manager.addOID(dBhelper.getdecoder1Oid("D1pcrpid"));
				manager.addOID(dBhelper.getdecoder1Oid("D1videopid"));
				manager.addOID(dBhelper.getdecoder1Oid("D1audiopid"));
				Map<String, String> map = manager.getData(myhandler);
				for (String key : map.keySet()) {
					if (!helper.getData().get(key).equals(map.get(key))) {
						helper.changeData(key, map.get(key));
					}
				}
				Message msg = Message.obtain(myhandler, R.id.changed);
				msg.sendToTarget();
			}
		}).start();
        text1= (EditText) findViewById(R.id.PCR_PID1);
        text2= (EditText) findViewById(R.id.Video_PID1);
        text3= (EditText) findViewById(R.id.Audio_PID1);
        bt1= (Button) findViewById(R.id.builderbt1);
        bt2= (Button) findViewById(R.id.builderbt2);

        /*设置edittext的输入范围*/
        setRegion(text1);
        setRegion(text2);
        setRegion(text3);
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


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.builderbt1:
				if (helper.getParaStatus(R.id.builderbt1)) return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> map = new HashMap<>();
                        map.put(dBhelper.getdecoder1Oid("D1pcrpid"), text1.getText().toString());
                        map.put(dBhelper.getdecoder1Oid("D1videopid"), text2.getText().toString());
                        map.put(dBhelper.getdecoder1Oid("D1audiopid"), text3.getText().toString());
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

}
