package com.mycomp.mrwang.snmpgetparamter.utils;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

/**
 * 系统级的关闭activity操作类
 * Created by wzq on 2017/5/4.
 */
public class SystemApplication extends Application {
    private LinkedList<Activity> actList = new LinkedList<>();
    private static SystemApplication instance;
    private SystemApplication(){

    }

    public static SystemApplication getInstance() {
        if (null == instance) {
            instance = new SystemApplication();
        }
        return instance;
    }

    //add Activity
    public void addActivity(Activity activity){
        actList.add(activity);
    }

    public void exit(){
        try {
            for (Activity activity : actList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
