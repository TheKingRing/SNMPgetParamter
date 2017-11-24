package com.mycomp.mrwang.snmpgetparamter.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mycomp.mrwang.snmpgetparamter.utils.Bean;
import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.ui.SmoothcheckBoxforAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于适配参数设置中trap选择和DS3的适配器；
 * 效果：多个选择只能选择一个，horizon布局
 * Created by Mr Wang on 2016/6/17.
 */
public class myViewAdapter extends BaseAdapter {
    private Context context;
    private  List<Map<String,Object>> list;
    private int LayoutID;
    private String[] str;
    private int[] id;
    private boolean clickable;
    private CompatUtils helper;
    public myViewAdapter(Context context,List<Map<String,Object>> list,int layoutID,String[] str,int[] id,boolean clickable){
        super();
        helper = CompatUtils.getInstance(context);
        this.list=list;
        this.context=context;
        this.LayoutID=layoutID;
        this.str=str;
        this.id=id;
        this.clickable=clickable;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null){
            holder=new ViewHolder(str,id);
            convertView=View.inflate(context,LayoutID,null);
            for (String string:holder.viewData.keySet()){
                View view=holder.viewData.get(string);
                if (view instanceof TextView){
                    holder.viewData.put(string,convertView.findViewById(holder.viewid.get(string)));
                }else {
                    holder.viewData.put(string,convertView.findViewById(holder.viewid.get(string)));
                }
            }
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        for (final String string:holder.viewData.keySet()){
            View view=holder.viewData.get(string);
            if (view instanceof TextView){
                ((TextView)holder.viewData.get(string)).setText((String) list.get(position).get(string));
            }else {
                final Bean bean= (Bean) list.get(position).get(string);
                holder.viewData.get(string).setClickable(clickable);
                ((SmoothcheckBoxforAdapter)holder.viewData.get(string)).setChecked(bean.isChecked,true);
                ((SmoothcheckBoxforAdapter)holder.viewData.get(string)).setOnCheckedChangeListener(new SmoothcheckBoxforAdapter.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothcheckBoxforAdapter checkBox, boolean isChecked) {
                        bean.isChecked=isChecked;
                        ChangeData(((TextView)holder.viewData.get("text1")).getText().toString());
                        for (String string1:holder.viewData.keySet()){
                            if (holder.viewData.get(string1) instanceof TextView||string1.equals(string)){
                                continue;
                            }
                            Bean tmpbean= (Bean) list.get(position).get(string1);
                            if (tmpbean.isChecked){
                                    ((SmoothcheckBoxforAdapter)holder.viewData.get(string1)).setChecked(false,true);
                                    ((Bean) list.get(position).get(string1)).isChecked=false;
                                }

                            }

                    }

                });
            }

        }

        return convertView;
    }

    /**
     * 更具标题内容对缓存中的数据进行更改
     * @param text1 ：标题内容
     * */

    private void ChangeData(String text1) {
        Object type;

        switch (text1){
            case "Trap1版本":
                type = helper.getData().get("trapAddr1Version");
                if ("0".equals(type)) helper.changeData("trapAddr1Version","1");
                else helper.changeData("trapAddr1Version","0");
                break;
            case "Trap1使能":
                type= helper.getData().get("trapEnable1");
                if ("0".equals(type)) helper.changeData("trapEnable1","1");
                else helper.changeData("trapEnable1","0");
                break;
            case "Trap2版本":
                type=  helper.getData().get("trapAddr2Version");
                if ("0".equals(type)) helper.changeData("trapAddr2Version","1");
                else helper.changeData("trapAddr2Version","0");
                break;
            case "Trap2使能":
                type= helper.getData().get("trapEnable2");
                if ("0".equals(type)) helper.changeData("trapEnable2","1");
                else helper.changeData("trapEnable2","0");
                break;
            case "FRAME":
                type=  helper.getData().get("frameType");
                if ("0".equals(type)) helper.changeData("frameType","1");
                else helper.changeData("frameType","0");
                break;
            case "RS" :
                type=  helper.getData().get("rsType");
                if ("0".equals(type)) helper.changeData("rsType","1");
                else helper.changeData("rsType","0");
                break;
            case "MSBF":
                type=  helper.getData().get("msbfType");
                if ("0".equals(type))  helper.changeData("msbfType","1");
                else helper.changeData("msbfType","0");
                break;
            case "E3":
                type= helper.getData().get("inputSource");
                if ("0".equals(type))helper.changeData("inputSource","1");
                else helper.changeData("inputSource","0");
                break;
            case "手动":
                type= helper.getData().get("autoDitect");
                if ("0".equals(type)) helper.changeData("autoDitect","1");
                else helper.changeData("autoDitect","0");
                break;
        }
    }
    /**
     * 适配器的自定义View holder
     * */
    private class ViewHolder{

        int[] id;
        String[] str;
        Map<String,View> viewData;
        Map<String,Integer> viewid;
        ViewHolder(String[] str,int[] id){
            this.id=id;
            this.str=str;
            viewData=new HashMap<>(5);
            viewid=new HashMap<>(5);
            intitData(str);

        }

        private void intitData(String[] str) {
            int count=0;
            for (String string:str){

                if (string.contains("check")){
                    viewData.put(string,new SmoothcheckBoxforAdapter(context));
                    viewid.put(string,id[count]);
                }else {
                    viewData.put(string,new TextView(context));
                    viewid.put(string,id[count]);
                }
                count++;
            }
        }
    }

}

