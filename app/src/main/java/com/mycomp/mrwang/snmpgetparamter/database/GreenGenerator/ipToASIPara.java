package com.mycomp.mrwang.snmpgetparamter.database.GreenGenerator;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "IP_TO_ASIPARA".
 */
public class ipToASIPara {

    private Long id;
    /** Not-null value. */
    private String parameter;
    /** Not-null value. */
    private String oid;
    /** Not-null value. */
    private String type;

    public ipToASIPara() {
    }

    public ipToASIPara(Long id) {
        this.id = id;
    }

    public ipToASIPara(Long id, String parameter, String oid, String type) {
        this.id = id;
        this.parameter = parameter;
        this.oid = oid;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getParameter() {
        return parameter;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /** Not-null value. */
    public String getOid() {
        return oid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOid(String oid) {
        this.oid = oid;
    }

    /** Not-null value. */
    public String getType() {
        return type;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setType(String type) {
        this.type = type;
    }

}
