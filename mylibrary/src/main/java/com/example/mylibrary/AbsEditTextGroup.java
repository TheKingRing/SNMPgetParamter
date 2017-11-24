package com.example.mylibrary;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by idea on 2016/7/18.
 */
public abstract class AbsEditTextGroup extends LinearLayout implements TextWatcher,View.OnKeyListener {
    private boolean tag=false;
    protected float sp20 = 20.0f;
    protected int dp4 = 4;
    private ArrayList<AbsEditText> editTexts = new ArrayList<AbsEditText>();

    public AbsEditTextGroup(Context context) {
        this(context, null, 0);
    }

    public AbsEditTextGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsEditTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addViews();
        buildListener();

    }

    protected void addViews() {
        for (int i = 0; i < getChildCount(); i++) {
            if (i%2==0) {
                AbsEditText absEditText= createAbsEditText();
                switch (i){
                    case 0:
                        absEditText.setId(R.id.iptext0);
                        break;
                    case 2:
                        absEditText.setId(R.id.iptext1);
                        break;
                    case 4:
                        absEditText.setId(R.id.iptext2);
                        break;
                    case 6:
                        absEditText.setId(R.id.iptext3);
                        break;
                }
                editTexts.add(absEditText);
                addView(absEditText);
            } else {
                addView(createSemicolonTextView());
            }
        }
    }

    protected AbsEditText createAbsEditText() {

        AbsEditText absEditText = getAbsEditText();
        LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        absEditText.setLayoutParams(params);
        absEditText.setTextSize(sp20);//sp
        absEditText.setTypeface(Typeface.DEFAULT,1);
        absEditText.setTextColor(getResources().getColor(R.color.win8_blue));
        absEditText.setGravity(Gravity.CENTER);
        absEditText.setPadding(dp4, dp4, dp4, dp4);
        absEditText.setSingleLine();
        absEditText.setFocusableInTouchMode(true);
        absEditText.setBackgroundColor(getResources().getColor(R.color.white));
        applyEditTextTheme(absEditText);
        return absEditText;
    }

    protected TextView createSemicolonTextView() {
        TextView textView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(params);
        textView.setTextSize(sp20);//sp
        textView.setTypeface(Typeface.DEFAULT, 1);
        textView.setTextColor(getResources().getColor(R.color.win8_blue));
        textView.setText(getSemicolomText());
        applySemicolonTextViewTheme(textView);
        return textView;
    }

    protected void buildListener() {

        ((AbsEditText)findViewById(R.id.iptext0)).addTextChangedListener(this);
        ((AbsEditText)findViewById(R.id.iptext1)).addTextChangedListener(this);
        ((AbsEditText)findViewById(R.id.iptext2)).addTextChangedListener(this);
        ((AbsEditText)findViewById(R.id.iptext3)).addTextChangedListener(this);

        findViewById(R.id.iptext0).setOnKeyListener(this);
        findViewById(R.id.iptext1).setOnKeyListener(this);
        findViewById(R.id.iptext2).setOnKeyListener(this);
        findViewById(R.id.iptext3).setOnKeyListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        AbsEditText editText = null;
        for (int i=0;i<editTexts.size();i++){
            editText=editTexts.get(i);
            if (editText.hasFocus()) break;
        }
        if (!checkInputValue(editText)){
            editText.setTextColor(getResources().getColor(R.color.red));
            tag=true;
            editText.setText(String.valueOf(editText.getPreValue(s)));
        }
        editText.setSelection(editText.getText().length());


    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() == getDelMaxLength()) {
            int a=100*(s.charAt(0)-48)+10*(s.charAt(1)-48)+(s.charAt(2)-48);
            if (tag){
                tag=false;
                for (int i=0; i< editTexts.size()-1; i++){
                    if(editTexts.get(i).hasFocus()){
                        editTexts.get(i).clearFocus();
                        editTexts.get(i+1).requestFocus();
                        break;
                    }
                }
            }else if (getAbsEditText().checkValue(a)){
                for (int i=0; i< editTexts.size()-1; i++){
                    if(editTexts.get(i).hasFocus()){
                        editTexts.get(i).clearFocus();
                        editTexts.get(i+1).requestFocus();
                        break;
                    }
                }
            }
        }
    }

    public boolean checkInputValue(AbsEditText... params) {
        boolean result = true;
        if (!params[0].checkInputValue()) {
            result = false;
        }
        return result;
    }

    public String getValues() {
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < editTexts.size(); i++) {
            builder.append(editTexts.get(i).getText().toString());
            builder.append(".");
        }
        builder.deleteCharAt(builder.lastIndexOf("."));
        return builder.toString();
    }

    public ArrayList<AbsEditText> getEditTexts() {
        return editTexts;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        AbsEditText clearEditText=(AbsEditText) v;
        AbsEditText requestEditText;
        int i = v.getId();

        if (i == R.id.iptext0&&keyCode==KeyEvent.KEYCODE_DEL&&event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (clearEditText.getSelectionStart() == 0) return true;
                clearEditText.setTextColor(getResources().getColor(R.color.win8_blue));
            }

        } else if (i == R.id.iptext1&&keyCode==KeyEvent.KEYCODE_DEL&&event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                requestEditText=(AbsEditText) findViewById(R.id.iptext0);
                if (clearEditText.getSelectionStart() == 0) {
                    clearEditText.clearFocus();
                    requestEditText.requestFocus();
                    return true;
                }
                clearEditText.setTextColor(getResources().getColor(R.color.win8_blue));
            }
        } else if (i == R.id.iptext2&&keyCode==KeyEvent.KEYCODE_DEL&&event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                requestEditText=(AbsEditText) findViewById(R.id.iptext1);
                if (clearEditText.getSelectionStart() == 0) {
                    clearEditText.clearFocus();
                    requestEditText.requestFocus();
                    return true;
                }
                clearEditText.setTextColor(getResources().getColor(R.color.win8_blue));
            }
        } else if (i == R.id.iptext3&&keyCode==KeyEvent.KEYCODE_DEL&&event.getAction() == KeyEvent.ACTION_DOWN) {
            requestEditText=(AbsEditText) findViewById(R.id.iptext2);
            if (clearEditText.getSelectionStart() == 0) {
                clearEditText.clearFocus();
                requestEditText.requestFocus();
                return true;
            }
            clearEditText.setTextColor(getResources().getColor(R.color.win8_blue));

        }
        return false;

    }

    public abstract int getChildCount();

    public abstract AbsEditText getAbsEditText();

    public abstract String getSemicolomText();

    public abstract int getDelMaxLength();

    public abstract void applySemicolonTextViewTheme(TextView semicolonTextView);

    public abstract void applyEditTextTheme(AbsEditText absEditText);

}