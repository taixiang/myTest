package com.example.okhttpdemo;

/**
 * Created by taixiang on 2015/12/8.
 */
public class ClassBean {

    private String name;
    private String id;
    private String school_id;
    private String org_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getOrg_id() {
        return org_id;
    }

    public void setOrg_id(String org_id) {
        this.org_id = org_id;
    }

    @Override
    public String toString() {
        return "ClassBean{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", school_id='" + school_id + '\'' +
                ", org_id='" + org_id + '\'' +
                '}';
    }
}
