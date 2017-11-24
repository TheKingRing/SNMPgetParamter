package com.mycomp.mrwang.snmpgetparamter.database.DataBaseHelper;

import android.content.Context;
import android.util.Log;

import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.DaoSession;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder1Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder1ParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder2Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.decoder2ParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ds3e3Para;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ds3e3ParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbcPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbcParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbsPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.dvbsParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.inputExistPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.inputExistParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ipreceivePara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ipreceiveParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.netPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.netParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.other;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.otherDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.trapPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.trapParaDao;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.versionPara;
import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.versionParaDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 数据库辅助函数
 * Created by Mr Wang on 2016/6/2.
 */
public class DBhelper {
    private static Context Appcontext;
    private static DBhelper instance;
    private DaoSession myDaosession;
    private trapParaDao mtrapParaDao;
    private netParaDao mnetParaDao;
    private versionParaDao mversionParaDao;
    private inputExistParaDao minputExistParaDao;
    private decoder1ParaDao mdecoder1ParaDao;
    private decoder2ParaDao mdecoder2ParaDao;
    private ipreceiveParaDao mipreceiveParaDao;
    private dvbcParaDao mdvbcParaDao;
    private dvbsParaDao mdvbsParaDao;
    private ds3e3ParaDao mds3e3ParaDao;
    private otherDao motherDao;


    private List<String> constellation;//星座图qam参数
    private List<String> inversion;//dvb-c inversion
    private List<String> j83Annex;//DVB-C J.83 Annex
    private List<String> polarization;//DVB-S polarizaion
    private List<String> dvbs22k;//DVB-S 22k


    private DBhelper() {
        constellation = new ArrayList<>(6);
        inversion = new ArrayList<>();
        j83Annex = new ArrayList<>();
        polarization = new ArrayList<>();
        dvbs22k = new ArrayList<>();
        constellation.add(0, null);

        for (int i = 0; i < 5; i++) {
            constellation.add(i + 1, (int) Math.pow(2, i) * 16 + "QAM");
        }

        inversion.add(0, "off");
        inversion.add(1, "on");

        j83Annex.add(0, "Annex_A");
        j83Annex.add(1, "Annex_C");

        polarization.add("垂直");
        polarization.add("水平");

        dvbs22k.add("关闭");
        dvbs22k.add("打开");

    }

    /*使用私有构造函数强化singleton属性*/
    public static DBhelper getInstance(Context context) {
        if (Appcontext == null) {
            Appcontext = context.getApplicationContext();
        }
        if (instance == null) {
            instance = new DBhelper();
        }
        instance.myDaosession = ContralApp.getDaoSession(context);

        instance.mtrapParaDao = instance.myDaosession.getTrapParaDao();
        instance.mnetParaDao = instance.myDaosession.getNetParaDao();
        instance.mversionParaDao = instance.myDaosession.getVersionParaDao();
        instance.minputExistParaDao = instance.myDaosession.getInputExistParaDao();
        instance.mdecoder1ParaDao = instance.myDaosession.getDecoder1ParaDao();
        instance.mdecoder2ParaDao = instance.myDaosession.getDecoder2ParaDao();
        instance.mipreceiveParaDao = instance.myDaosession.getIpreceiveParaDao();
        instance.mdvbcParaDao = instance.myDaosession.getDvbcParaDao();
        instance.mdvbsParaDao = instance.myDaosession.getDvbsParaDao();
        instance.mds3e3ParaDao = instance.myDaosession.getDs3e3ParaDao();
        instance.motherDao = instance.myDaosession.getOtherDao();
        return instance;
    }

    public List<String> getPolarization() {
        return polarization;
    }

    public List<String> getDvbs22k() {
        return dvbs22k;
    }

    public List<String> getConstellation() {
        return constellation;
    }

    public List<String> getInversion() {
        return inversion;
    }

    public List<String> getJ83Annex() {
        return j83Annex;
    }
    /*加载所有数据加载项*/


    public List<inputExistPara> loadAllinputExistParaDao() {
        return minputExistParaDao.loadAll();
    }


    public List<decoder1Para> loadAlldecoder1Para() {
        return mdecoder1ParaDao.loadAll();
    }

    public List<decoder2Para> loadAlldecoder2Para() {
        return mdecoder2ParaDao.loadAll();
    }


    /**
     * 插入或删除项
     */

    long saveTrapPara(trapPara session) {
        return mtrapParaDao.insertOrReplace(session);//返回的是插入行的id
    }

    long saveNetPara(netPara session) {
        return mnetParaDao.insertOrReplace(session);//返回的是插入行的id
    }

    long saveinputExistPara(inputExistPara session) {
        return minputExistParaDao.insertOrReplace(session);
    }

    long saveversionPara(versionPara session) {
        return mversionParaDao.insertOrReplace(session);
    }

    long savedecoder1Para(decoder1Para session) {
        return mdecoder1ParaDao.insertOrReplace(session);
    }

    long savedecoder2Para(decoder2Para session) {
        return mdecoder2ParaDao.insertOrReplace(session);
    }

    long saveipreceivePara(ipreceivePara session) {
        return mipreceiveParaDao.insertOrReplace(session);
    }

    long saveother(other session) {
        return motherDao.insertOrReplace(session);
    }

    long savedvbcPara(dvbcPara session) {
        return mdvbcParaDao.insertOrReplace(session);
    }

    long savedvbsPara(dvbsPara session) {
        return mdvbsParaDao.insertOrReplace(session);
    }

    long saveds3e3Para(ds3e3Para session) {
        return mds3e3ParaDao.insertOrReplace(session);
    }


    /*根据id找到Inpustream的某一项的type*/
    public String getInpustreamType(long id) {
        inputExistPara entity = minputExistParaDao.load(id);
        return entity.getParameter();
    }

    public String getInputStreamID(String type) {
        List<inputExistPara> list = minputExistParaDao.loadAll();
        for (inputExistPara session : list) {
            if (type.equals(session.getParameter())) {
                return String.valueOf(session.getId());
            }
        }
        return "0";
    }

    /*得到inputStream的oid与参数映射*/
    public Map<String, String> getInputstreamParas() {
        Map<String, String> map = new HashMap<>();
        List<inputExistPara> paras = minputExistParaDao.loadAll();
        for (inputExistPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*得到decoder1参数的oid*/
    public String getdecoder1Oid(String parameter) {
        List<decoder1Para> parameterdata = mdecoder1ParaDao.loadAll();
        Log.e("hello", "getdecoder1Oid: "+ parameterdata );
        for (decoder1Para entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }


    /*得到decoder2参数的oid*/
    public String getdecoder2Oid(String parameter) {
        List<decoder2Para> parameterdata = mdecoder2ParaDao.loadAll();

        for (decoder2Para entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }

    /*得到trapPara参数的oid*/
    public String gettrapParaOid(String parameter) {
        List<trapPara> trapParas = mtrapParaDao.loadAll();
        for (trapPara paras : trapParas) {
            if (parameter.equals(paras.getParameter())) {
                return paras.getOid();
            }
        }
        return null;
    }

    /*得到netPara参数的oid*/
    public String getnetParaOid(String parameter) {
        List<netPara> parameterdata = mnetParaDao.loadAll();
        for (netPara entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }


    /*得到组播设置的oid*/
    public String getbroadcastParaOid(String parameter) {
        List<ipreceivePara> parameterdata = mipreceiveParaDao.loadAll();
        for (ipreceivePara entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }

    /**
     * 得到的其余的参数oid(全部是只读的)
     * （1）assi环出的参数
     * （2）ip转asi输出码率
     * （3）是否允许restart设置。
     */
    public String getotherParaOid(String parameter) {
        List<other> parameterdata = motherDao.loadAll();
        for (other entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }
    /*获得dvbc的参数oid*/

    public String getdvbcParaOid(String parameter) {
        List<dvbcPara> parameterdata = mdvbcParaDao.loadAll();
        for (dvbcPara entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }

    /*获得dvbs的参数oid*/
    public String getdvbsParaOid(String parameter) {
        List<dvbsPara> parameterdata = mdvbsParaDao.loadAll();
        for (dvbsPara entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }

     /*获得dvbs的参数oid*/

    public String getds3e3ParaOid(String parameter) {
        List<ds3e3Para> parameterdata = mds3e3ParaDao.loadAll();
        for (ds3e3Para entity : parameterdata) {
            if (parameter.equals(entity.getParameter())) {
                return entity.getOid();
            }
        }
        return null;
    }

    /*获得netPara 参数与oid的映射map*/
    public Map<String, String> getnetParas() {
        Map<String, String> map = new HashMap<>();
        List<netPara> paras = mnetParaDao.loadAll();
        for (netPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得netPara 参数与oid的映射map*/
    public Map<String, String> gettrapParas() {
        Map<String, String> map = new HashMap<>();
        List<trapPara> paras = mtrapParaDao.loadAll();
        for (trapPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得versionPara 参数与oid的映射map*/
    public Map<String, String> getversionParas() {
        Map<String, String> map = new HashMap<>();
        List<versionPara> paras = mversionParaDao.loadAll();
        for (versionPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得组播设置 参数与oid的映射map*/
    public Map<String, String> getipreceiveParas() {
        Map<String, String> map = new HashMap<>();
        List<ipreceivePara> paras = mipreceiveParaDao.loadAll();
        for (ipreceivePara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得other 参数与oid的映射map*/
    public Map<String, String> getotherParas() {
        Map<String, String> map = new HashMap<>();
        List<other> paras = motherDao.loadAll();
        for (other para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得dvbcPara 参数与oid的映射map*/
    public Map<String, String> getdvbcParas() {
        Map<String, String> map = new HashMap<>();
        List<dvbcPara> paras = mdvbcParaDao.loadAll();
        for (dvbcPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得dvbsPara 参数与oid的映射map*/
    public Map<String, String> getdvbsParas() {
        Map<String, String> map = new HashMap<>();
        List<dvbsPara> paras = mdvbsParaDao.loadAll();
        for (dvbsPara para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }

    /*获得dvbsPara 参数与oid的映射map*/
    public Map<String, String> getds3e3Paras() {
        Map<String, String> map = new HashMap<>();
        List<ds3e3Para> paras = mds3e3ParaDao.loadAll();
        for (ds3e3Para para : paras) {
            map.put(para.getParameter(), para.getOid());
        }
        return map;
    }


    public void close() {
        myDaosession.getDatabase().close();
    }


}
