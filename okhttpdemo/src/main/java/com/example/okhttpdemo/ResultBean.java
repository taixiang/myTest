package com.example.okhttpdemo;

/**
 * Created by taixiang on 2015/12/8.
 */
public class ResultBean {
    DataBean result;

    public DataBean getResult() {
        return result;
    }

    public void setResult(DataBean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "result=" + result +
                '}';
    }
}
