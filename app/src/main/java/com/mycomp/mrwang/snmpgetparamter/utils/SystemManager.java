package com.mycomp.mrwang.snmpgetparamter.utils;

import android.util.Log;

import java.io.DataOutputStream;

/**
 * Created by wzq on 2017/6/18.
 */

public class SystemManager {
    private static final String TAG = "SystemManager";
    public static boolean RootCommand(String command)
    {
        Process process = null;
        DataOutputStream os = null;
        try
        {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e)
        {
            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
            return false;
        } finally
        {
            try
            {
                if (os != null)
                {
                    os.close();
                }
                assert process != null;
                process.destroy();
            } catch (Exception e)
            {
                Log.e(TAG,"RootCommand: "+ e.getMessage());
            }
        }
        Log.d("*** DEBUG ***", "Root SUC ");
        return true;
    }
}
