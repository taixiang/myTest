package com.example.okhttpdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Iterator;

public class Main2Activity extends Activity {

    String url = "http://api.dean.appisbest.com:18080/api/class/process/?identify=EFEC654CBE699F31B06B0B5ABC7BC8AA&token=3c4f15118142820146a490efbd9ab75941305433&plat=android&access_token=NDAwMDAwMjA3XzFfMTQ0OTQ1ODkzMl83NzhjMDk4MzEwNzQwOTBkZGNhZTdjMzRkYTdiMGM3OQ==&num=2147483647&model=ME173X&testing=K32L49U5N&deviceid=2448a01d69548db6&channel=official&student_id=400000205&ver=2.1.2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        String org_id = intent.getStringExtra("org_id");
        String school_id = intent.getStringExtra("school_id");
        String class_id = intent.getStringExtra("class_id");
        HashMap<String,String> map = new HashMap();
        map.put("org_id",org_id);
        map.put("school_id",school_id);
        map.put("class_id",class_id);
        OkHttpClientManager.getAsyn(getUrl(url, map), new OkHttpClientManager.ResultCallback<ResultBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(ResultBean response) {
                Log.i("》》》》",response.toString());
            }
        });

    }

    private String getUrl(String url,HashMap<String, String> params) {
        // 添加url参数
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = new StringBuffer();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                    sb.append("&");
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        Log.i("》》》",url);
        return url;
    }
}
