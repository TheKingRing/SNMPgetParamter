package com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator.ds3e3Para;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DS3E3_PARA".
*/
public class ds3e3ParaDao extends AbstractDao<ds3e3Para, Long> {

    public static final String TABLENAME = "DS3E3_PARA";

    /**
     * Properties of entity ds3e3Para.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Parameter = new Property(1, String.class, "parameter", false, "PARAMETER");
        public final static Property Oid = new Property(2, String.class, "oid", false, "OID");
        public final static Property Type = new Property(3, String.class, "type", false, "TYPE");
    };


    public ds3e3ParaDao(DaoConfig config) {
        super(config);
    }
    
    public ds3e3ParaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DS3E3_PARA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"PARAMETER\" TEXT NOT NULL ," + // 1: parameter
                "\"OID\" TEXT NOT NULL ," + // 2: oid
                "\"TYPE\" TEXT NOT NULL );"); // 3: type
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DS3E3_PARA\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ds3e3Para entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getParameter());
        stmt.bindString(3, entity.getOid());
        stmt.bindString(4, entity.getType());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ds3e3Para readEntity(Cursor cursor, int offset) {
        ds3e3Para entity = new ds3e3Para( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // parameter
            cursor.getString(offset + 2), // oid
            cursor.getString(offset + 3) // type
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ds3e3Para entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setParameter(cursor.getString(offset + 1));
        entity.setOid(cursor.getString(offset + 2));
        entity.setType(cursor.getString(offset + 3));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ds3e3Para entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ds3e3Para entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
