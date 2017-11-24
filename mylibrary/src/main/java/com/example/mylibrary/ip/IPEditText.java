package com.example.mylibrary.ip;

import android.content.Context;
import android.util.AttributeSet;

import com.example.mylibrary.AbsEditText;


/**
 * Created by idea on 2016/7/15.
 */
public class IPEditText extends AbsEditText {

    public IPEditText(Context context) {
        this(context, null, 0);
    }

    public IPEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IPEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getMaxLength() {
        return 3;
    }

    @Override
    public char[] getInputFilterAcceptedChars() {
        return new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    }

    @Override
    public boolean checkInputValue() {
        CharSequence ch=getText();
         if (ch.length()==3){
            int a=100*(ch.charAt(0)-48)+10*(ch.charAt(1)-48)+(ch.charAt(2)-48);
            if (a>255){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkValue(int value) {
        return value<=255;
    }

    @Override
    public int getPreValue(CharSequence s) {
        int a=(s.charAt(0)-48)*10+(s.charAt(1)-48);
        return a;
    }
}