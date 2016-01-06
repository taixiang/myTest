package com.example.okhttpdemo;

import java.util.List;

/**
 * Created by taixiang on 2015/12/8.
 */
public class DataListBean {
    List<ClassBean> data;

    public List<ClassBean> getData() {
        return data;
    }

    public void setData(List<ClassBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataListBean{" +
                "data=" + data +
                '}';
    }
}
