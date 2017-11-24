package com.mycomp.mrwang.snmpgetparamter.circle_loop_activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.mycomp.mrwang.snmpgetparamter.AsyncTask.Decoder_Inputstream_Set;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.SearchTask;
import com.mycomp.mrwang.snmpgetparamter.AsyncTask.SnmpGetTask;
import com.mycomp.mrwang.snmpgetparamter.ui.TimerDialog;
import com.mycomp.mrwang.snmpgetparamter.utils.Bean;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;

import com.mycomp.mrwang.snmpgetparamter.utils.SnmpHelper;
import com.mycomp.mrwang.snmpgetparamter.utils.SystemApplication;
import com.mycomp.mrwang.snmpgetparamter.ui.SmoothCheck;
import com.mycomp.mrwang.snmpgetparamter.R;
import com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper.DBhelper;
import com.ryg.dynamicload.DLBasePluginActivity;
import com.ryg.dynamicload.internal.DLIntent;

import java.util.HashMap;
import java.util.Map;

/**
 * decoder2
 * Created by Mr Wang on 2016/6/7.
 */
public class Decoder2Activity extends DLBasePluginActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener
        , Spinner.OnItemSelectedListener, GestureDetector.OnGestureListener {
    private CompatUtils helper;
    private SnmpHelper manager;
    private DBhelper dBhelper;
    GestureDetector gestureDetector;
    String current_inputstream;//当前的输入流


    SnmpGetTask snmpGetTask; //snmpGet
    SearchTask searchTask;//firstSearch;
    Decoder_Inputstream_Set decoderInputstreamSet;

    Spinner input_Stream;
    SeekBar Volume;//音量拖动条
    ListView progranmList;
    TextView textView;

    Button search;

    Button clear;

    /*所有线程*/
    Thread Volumeseletct;
    Thread ItemSelect;
    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.id.stop:
                    finish();
                    break;
                case R.id.input_Stream:
                    helper.changeData(msg.getData());
                    break;
                case R.id.Volume1:
                    helper.changeData(msg.getData());
                    break;
                case R.id.noresponse:
                    switch (msg.arg1) {
                        case R.id.input_Stream:
                            String input = dBhelper.getInpustreamType(Long.parseLong(msg.getData().getString("D2inputchoose")));
                            input_Stream.setSelection(helper.getInputStream_index(input));
                            Toast.makeText(that, "输入输出流设置失败", Toast.LENGTH_SHORT).show();                            break;
                        case R.id.Volume:
                            Toast.makeText(that, "failed to change Volume", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case R.id.serachchaged:
                    // 搜索节目
                    searchTask = new SearchTask(that, progranmList, myhandler);
                    searchTask.execute("Decoder2");
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decoder2_main);
        helper = CompatUtils.getInstance(that);
        manager = helper.getHelper(that);
        SystemApplication.getInstance().addActivity(that); // restore the activity that should be exit
        initView();
        snmpGetTask = new SnmpGetTask(that, input_Stream, Volume, textView, myhandler);
        snmpGetTask.execute("Decoder2");

        searchTask = new SearchTask(that, progranmList, myhandler);
        searchTask.execute("Decoder2");


        /*调节音量*/
        Volume.setOnSeekBarChangeListener(this);

          /*给节目列表设置点击事件*/
        progranmList.setOnItemClickListener(this);
        progranmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    DLIntent intent = new DLIntent(getPackageName(), ResultActivity2.class);
                    startPluginActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });
        /*刷新*/
        clear.setOnClickListener(this);

       /*搜索*/
        search.setOnClickListener(this);
        /*snmpSet*/
        	/*设置spinner点击事件，点击内容不变则跳过*/
        input_Stream.setOnItemSelectedListener(this);
        gestureDetector = new GestureDetector(that, this);
    }


    private void initView() {

        input_Stream = (Spinner) findViewById(R.id.input_Stream1);
        Volume = (SeekBar) findViewById(R.id.Volume1);
        search = (Button) findViewById(R.id.search1);
        clear = (Button) findViewById(R.id.refresh);
        progranmList = (ListView) findViewById(R.id.program1);
        textView = (TextView) findViewById(R.id.valueofvolume1);
        dBhelper = DBhelper.getInstance(that);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        if (helper.getParaStatus(R.id.Volume1)) {
            return;
        }
        textView.setText(String.valueOf(progress));
        Volumeseletct = new Thread() {
            @Override
            public void run() {
                String oid = dBhelper.getdecoder2Oid("D2volume");
                Map<String, String> map = new HashMap<>();
                map.put(oid, String.valueOf(progress));

                helper.setParaStatus(R.id.Volume1, true);

                manager.snmpAsynSetList(map, myhandler, R.id.Volume1);

            }
        };
        Volumeseletct.start();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        final int progress = seekBar.getProgress();
        textView.setText(String.valueOf(progress));
        Volumeseletct = new Thread() {
            @Override
            public void run() {
                String oid = dBhelper.getdecoder1Oid("D2volume");
                Map<String, String> map = new HashMap<>();
                map.put(oid, String.valueOf(progress));
                helper.setParaStatus(R.id.Volume1, true);
                manager.snmpAsynSetList(map, myhandler, R.id.Volume1);

            }
        };
        Volumeseletct.start();
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
        Bean bean = (Bean) parent.getItemAtPosition(position);
        if (position == 0) {
            TimerDialog dialog = new TimerDialog(that);
            dialog.setTitle("长按该选项出现PID参数设置");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }, 10);
            dialog.show();
            dialog.setButtonType(Dialog.BUTTON_POSITIVE, 4, true);
        }
        if (bean.isChecked || helper.getParaStatus(R.id.current_programme)) {
            return;
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case R.id.current_programme:
                        int count = parent.getCount();
                        int capacity = parent.getChildCount();
                        int tag = 0;
                        //对适配器里的数据进行操作
                        for (int i = 0; i < count; i++) {
                            Bean pos = (Bean) parent.getItemAtPosition(i);
                            if (i == position) {
                                pos.setIsChecked(true);
                            } else if (pos.isChecked) {
                                tag = i;
                                pos.setIsChecked(false);
                            }
                        }
                        //对checkedbox动画进行操作
                        if (position - tag <= capacity) {
                            for (int i = 0; i <= capacity; i++) {
                                View v = parent.getChildAt(i);
                                if (v == null) continue;
                                SmoothCheck checked = (SmoothCheck) v.findViewById(R.id.scb);
                                if (checked.isChecked()) {
                                    checked.setChecked(false, true);
                                    break;
                                }
                            }
                        }
                        SmoothCheck check = (SmoothCheck) view.findViewById(R.id.scb);
                        check.setChecked(true, true);
                        helper.setCurrent_program(position);
                        helper.changeData("current_program", position);
                }
            }
        };
        ItemSelect = new Thread() {
            @Override
            public void run() {
                String oid = dBhelper.getdecoder1Oid("D2currentprogram");
                Map<String, String> map = new HashMap<>();
                map.put(oid, String.valueOf(position));
                manager.snmpAsynSetList(map, handler, R.id.current_programme);
            }
        };
        ItemSelect.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search1:
                searchTask = new SearchTask(that, progranmList, myhandler);
                searchTask.execute("Decoder2");
                break;
            case R.id.refresh:
                snmpGetTask = new SnmpGetTask(that, input_Stream, Volume, textView, myhandler);
                snmpGetTask.execute("Decoder2");
                searchTask = new SearchTask(that, progranmList, myhandler);
                searchTask.execute("Decoder2");
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        TextView text = (TextView) view.findViewById(R.id.check1);
        String cur = text.getText().toString();
        String pre = dBhelper.getInpustreamType(Long.parseLong((String) helper.getData().get("D2inputchoose")));
        if (!cur.equals(pre)) {
            current_inputstream = dBhelper.getInputStreamID(cur);
            helper.getData().put("D2inputchoose", current_inputstream);

            Decoder_Inputstream_Set task = new Decoder_Inputstream_Set(that, myhandler);
            task.execute("Decoder2", current_inputstream);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onDown(MotionEvent e) {
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
