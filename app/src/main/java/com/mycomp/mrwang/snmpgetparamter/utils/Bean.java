package com.mycomp.mrwang.snmpgetparamter.utils;

import java.io.Serializable;

/**
 * check box tuple
 * Created by Mr Wang on 2016/6/21.
 */
public class Bean implements Serializable {
    public boolean isChecked;
    public Bean(){}
    public Bean(boolean isChecked){
        this.isChecked=isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}
