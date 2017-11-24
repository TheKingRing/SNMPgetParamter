package com.mycomp.mrwang.snmpgetparamter.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycomp.mrwang.snmpgetparamter.utils.Bean;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.ui.SmoothCheck;
import com.mycomp.mrwang.snmpgetparamter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于适配节目单的adapter
 * 效果：每次只能选择一个节目
 * Created by Mr Wang on 2016/5/26.
 */

public class listviewAdapter extends BaseAdapter {
    public static List<Bean> mList;
    private Context context;
    private Map<Integer,String> map;
    private CompatUtils helper;
    boolean tag;
    public listviewAdapter(Context context, Map<Integer, String> map){
        mList=new ArrayList<>();
        helper = CompatUtils.getInstance(context);
        this.context=context;
        this.map=map;
        for (int k=0;k < map.size();k++){
            if (k==helper.getCurrent_program()){
                mList.add(new Bean(true));
            }else {
                mList.add(new Bean(false));
            }
        }
        tag=true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView==null){
            //初始化layout
            holder = new ViewHolder();
            convertView=View.inflate(context,R.layout.list_view,null);
            holder.programnumber = (TextView) convertView.findViewById(R.id.tv1);
            holder. programecheck = (SmoothCheck) convertView.findViewById(R.id.scb);
            holder. programname= (TextView) convertView.findViewById(R.id.tv2);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        Bean bean = mList.get(position);
        holder.programnumber.setText(String.valueOf(position));
        holder.programname.setText(map.get(position));
        if (bean.isChecked && !holder.programecheck.isChecked()) {
            holder.programecheck.setChecked(true, true);
        }else if (!bean.isChecked && holder.programecheck.isChecked()) {
            holder.programecheck.setChecked(false, true);
        }
        return convertView;
    }


    private class ViewHolder {
        SmoothCheck programecheck;
        TextView programnumber;
        TextView programname;

    }

}
