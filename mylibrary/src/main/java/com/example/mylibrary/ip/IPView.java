package com.example.mylibrary.ip;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.example.mylibrary.AbsEditText;
import com.example.mylibrary.AbsEditTextGroup;


/**
 * Created by idea on 2016/7/15.
 */
public class IPView extends AbsEditTextGroup {

    public IPView(Context context) {
        super(context);
    }

    public IPView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IPView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getChildCount() {
        return 7;
    }


    @Override
    public AbsEditText getAbsEditText() {
        return new IPEditText(getContext());
    }

    @Override
    public String getSemicolomText() {
        return ".";

    }

    @Override
    public int getDelMaxLength() {
        return 3;
    }

    @Override
    public void applySemicolonTextViewTheme(TextView semicolonTextView) {
        semicolonTextView.setPadding(0,0,0,5);
        semicolonTextView.getPaint().setFakeBoldText(true);
        semicolonTextView.setBackgroundColor(0xFFFFFFFF);
        semicolonTextView.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void applyEditTextTheme(AbsEditText absEditText) {

    }
}
