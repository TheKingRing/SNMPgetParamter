package com.mycomp.mrwang.snmpgetparamter.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.mycomp.mrwang.snmpgetparamter.R;

/**
 * 自定义dialog
 * Created by wzq on 2017/6/25.
 */

public class MyProgressDialog extends Dialog {
    private int anim=0;
    private static MyProgressDialog MyProgressDialog = null;

    public MyProgressDialog(Context context){
        super(context);
    }

    public MyProgressDialog(Context context, int theme, int anim) {
        super(context, theme);
        this.anim=anim;
    }

    public static MyProgressDialog createDialog(Context context,int anim){
        MyProgressDialog = new MyProgressDialog(context, R.style.MyProgressDialog ,anim);
        MyProgressDialog.setContentView(R.layout.comm_progress_dialog);
        MyProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;

        return MyProgressDialog;
    }



    public void onWindowFocusChanged(boolean hasFocus){

        if (MyProgressDialog == null){
            return;
        }

        ImageView imageView = (ImageView) MyProgressDialog.findViewById(R.id.iv_loading);
        if(anim!=0) {
            imageView.setBackgroundResource(anim);
        }
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    /**
     * 设置标题
     * @param strTitle: 需要设置的标题内容
     * @return dialog
     */
    public MyProgressDialog setTitile(String strTitle){
        return MyProgressDialog;
    }

    /**
     * 设置提示内容
     * @param strMessage
     * @return
     */
    public MyProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)MyProgressDialog.findViewById(R.id.tv_loading_msg);

        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }

        return MyProgressDialog;
    }
}
