package com.example.okhttpdemo;

/**
 * Created by taixiang on 2015/12/8.
 */
public class DataBean {
    DataListBean data;

    public DataListBean getData() {
        return data;
    }

    public void setData(DataListBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "data=" + data +
                '}';
    }
}
