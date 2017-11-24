package com.mycomp.mrwang.snmpgetparamter.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.mycomp.mrwang.snmpgetparamter.R;

import java.util.List;
import java.util.Map;

/**
 * 同于适配trap告警界面的adapter
 * Created by Mr Wang on 2016/5/26.
 */
public class trap_list_adapter extends BaseAdapter {
    private final String TAG = "trap_list_adapter";
    private Context context;
    private List<Map<String, String>> data;

    public trap_list_adapter(Context context, List<Map<String, String>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            //初始化layout
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.trap_listview, null);
            holder.time = (TextView) convertView.findViewById(R.id.time_value);
            holder.ip = (TextView) convertView.findViewById(R.id.host_id);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.time.setText(data.get(position).get("time"));
        holder.ip.setText(data.get(position).get("ip"));


        return convertView;
    }


    private class ViewHolder {
        TextView time;
        TextView ip;
    }

}
