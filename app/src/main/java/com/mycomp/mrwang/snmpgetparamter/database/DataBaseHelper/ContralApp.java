package com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mycomp.mrwang.snmpgetparamter.utils.CompatUtils;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.DaoMaster;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.DaoSession;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder1Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder2Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ds3e3Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbcPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbsPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.inputExistPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ipreceivePara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.netPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.other;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.trapPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.versionPara;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;


import static com.ryg.dynamicload.DLBasePluginService.TAG;


/**
 * 主Application, app加载时第一个执行的地方
 * 主要完成数据库的初始化，mib文件的解析，手机ip的获取等基础工作
 * Created by Mr Wang on 2016/6/2.
 */
public class ContralApp extends Application {
    long trapParaid=0,netParaid=0,versionParaid=0,inputExistParaid=0,decoder1Paraid=0,decoder2Paraid=0,ipreceiveParaid=0,dvbcParaid=0,dvbsParaid=0,ds3e3Paraid=0,oterid=0;

    final int levvel=12;
    final int beforelevel=11;
    private DBhelper dbManager;
    private static DaoSession daoSession;
    private static DaoMaster daoMaster;
    public static SQLiteDatabase db;
    private static DaoMaster.DevOpenHelper helper;
    public static final String DB_NAME = "myequipment.db";
    private CompatUtils Fhelper;


    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null)
        {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static void CloseHelper() {
        if (helper != null) {
            helper.close();
            helper = null;
        }
    }

    public static void CloasDaosession() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
    }

    public static void closeConnection() {
        CloseHelper();
        CloasDaosession();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fhelper = CompatUtils.getInstance(this);
        // 获得本机IP
        WifiManager manager = (WifiManager)this.getSystemService(WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        Fhelper.intToIp(info.getIpAddress());
        try {
            initialzieDatabase();//初始化数据库
        } catch (IOException | MibLoaderException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTerminate() {
        Log.e(TAG, "onTerminate: " );
        super.onTerminate();
    }

    private void initialzieDatabase() throws IOException, MibLoaderException {
        dbManager = DBhelper.getInstance(this);
        InputStream stream=getResources().getAssets().open("DE200-MIB.MIB");
        InputStreamReader reader=new InputStreamReader(stream);
        MibLoader mibLoader=new MibLoader();
        Mib mib=mibLoader.load(reader);
        Collection c=mib.getAllSymbols();
        for (Object obj : c) {
            if (obj instanceof MibValueSymbol) {
                MibValueSymbol mvs = (MibValueSymbol) obj;
                if (mvs.getValue().toString().split("\\.").length == levvel) {
                    if (!mvs.isTableColumn()) {//不是表格数据
                        String parent = mvs.getParent().getName();
                        String parameter = mvs.getName();
                        String oid = mvs.getValue().toString() + ".0";
                        String type = null;
                        if (mvs.getType() instanceof SnmpObjectType) {
                            SnmpObjectType shot = (SnmpObjectType) mvs.getType();
                            type = shot.getSyntax().getName();
                        }
                        initialsession(parent, parameter, oid, type);
                    }


                }
                /*存入其他参数*/
                if (mvs.getValue().toString().split("\\.").length == beforelevel && mvs.getType() instanceof SnmpObjectType) {
                    String parameter = mvs.getName();
                    String oid = mvs.getValue().toString() + ".0";
                    other session = new other(++oterid, parameter, oid, ((SnmpObjectType) mvs.getType()).getSyntax().getName());
                    Fhelper.restoreOid_Name(oid, parameter);
                    dbManager.saveother(session);
                }

                if (mvs.isTableColumn()) {
                    String parameter = mvs.getName();
                    String oid = mvs.getValue().toString() + ".";
                    initileTable(parameter, oid);
                }
            }
        }

    }

    private void initileTable(String parameter, String oid) {
        switch (parameter){
            case "D1programName":

                decoder1Para session=new decoder1Para(++decoder1Paraid);
                session.setOid(oid);
                session.setParameter(parameter);
                session.setType("STRING");
                dbManager.savedecoder1Para(session);
                break;
            case "D2programName":
                decoder2Para session2=new decoder2Para(++decoder2Paraid);
                session2.setOid(oid);
                session2.setParameter(parameter);
                session2.setType("STRING");
                dbManager.savedecoder2Para(session2);
                break;
        }
    }


    /*初始化数据库列表*/

    private void initialsession(String parent, String parameter, String oid, String type) {
        Fhelper.restoreOid_Name(oid, parameter);
        switch (parent){
            case "trapPara":
                trapPara session=new trapPara(++trapParaid,parameter,oid,type);
                dbManager.saveTrapPara(session);
                break;
            case "netPara":
                netPara session1=new netPara(++netParaid,parameter,oid,type);
                dbManager.saveNetPara(session1);
                break;
            case "versionPara":
                versionPara session2=new versionPara(++versionParaid,parameter,oid,type);
                dbManager.saveversionPara(session2);
                break;
            case "inputExistPara":
                parameter=parameter.replaceAll("Exist","");
                inputExistPara session3=new inputExistPara(++inputExistParaid,parameter,oid,type);
                dbManager.saveinputExistPara(session3);
                break;
            case "decoder1Para":
                decoder1Para session4=new decoder1Para(++decoder1Paraid,parameter,oid,type);
                dbManager.savedecoder1Para(session4);
                break;
            case "decoder2Para":
                decoder2Para session5=new decoder2Para(++decoder2Paraid,parameter,oid,type);
                dbManager.savedecoder2Para(session5);
                break;
            case "ipreceivePara":
                ipreceivePara session6=new ipreceivePara(++ipreceiveParaid,parameter,oid,type);
                dbManager.saveipreceivePara(session6);
                break;
            case "dvbcPara":
                dvbcPara session7=new dvbcPara(++dvbcParaid,parameter,oid,type);
                dbManager.savedvbcPara(session7);
                break;
            case "dvbsPara":
                dvbsPara session8=new dvbsPara(++dvbsParaid,parameter,oid,type);
                dbManager.savedvbsPara(session8);
                break;
            case "ds3e3Para":
                ds3e3Para session9=new ds3e3Para(++ds3e3Paraid,parameter,oid,type);
                dbManager.saveds3e3Para(session9);
                break;
        }
    }

}


