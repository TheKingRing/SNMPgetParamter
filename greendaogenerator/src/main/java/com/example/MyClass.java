package com.example;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyClass {
    static final  int level1=11;

    public static void main(String[] args) throws Exception {
        Schema schema=new Schema(1,"com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator");
        String filepath=System.getProperty("user.dir")+"\\DE200-MIB.MIB";
        File file=new File(filepath);
        MibLoader loader=new MibLoader();
        Mib mib=loader.load(file);
        Collection c=mib.getAllSymbols();
        Iterator it=c.iterator();
        boolean tag=true;
        while (it.hasNext()){
            Object obj=it.next();
            if (obj instanceof MibValueSymbol){
                MibValueSymbol mvs= (MibValueSymbol) obj;

                int length=mvs.getValue().toString().split("\\.").length;
                if (length==level1){
                    if (mvs.getType()instanceof SnmpObjectType&&tag){
                        Entity note=schema.addEntity("other");
                        note.addIdProperty().autoincrement();
                        note.addStringProperty("parameter").notNull();
                        note.addStringProperty("oid").notNull();
                        note.addStringProperty("type").notNull();
                        tag=false;

                    }else {
                        Entity note=schema.addEntity(mvs.getName());
                        note.addIdProperty().autoincrement();
                        note.addStringProperty("parameter").notNull();
                        note.addStringProperty("oid").notNull();
                        note.addStringProperty("type").notNull();
                    }

                }
            }
        }

        new DaoGenerator().generateAll(schema,"./app/src/main/java");
    }



}
