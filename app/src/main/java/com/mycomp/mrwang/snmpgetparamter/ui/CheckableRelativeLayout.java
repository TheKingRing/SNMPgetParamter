package com.mycomp.mrwang.snmpgetparamter.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by Mr Wang on 2016/5/12.
 */
public class CheckableRelativeLayout extends RelativeLayout implements Checkable{
    private boolean mChecked=false;
    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked!=checked){
            mChecked=checked;
            refreshDrawableState();
            for (int i=0,len=getChildCount();i<len;i++){
                View child=getChildAt(i);
                if (child instanceof Checkable){
                    ((Checkable) child).setChecked(checked);
                }

            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
    setChecked(!mChecked);
    }
}
