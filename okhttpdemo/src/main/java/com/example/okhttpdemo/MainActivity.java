package com.example.okhttpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    private String url = "http://api.dean.appisbest.com:18080/api/user/classes/?identify=EFEC654CBE699F31B06B0B5ABC7BC8AA&token=3c4f15118142820146a490efbd9ab75941305433&plat=android&org_id=&school_id=&start=0&access_token=NDAwMDAwMjA3XzFfMTQ0OTQ1MTM1NV81ODUwMjE3NjA4YzE4NTkwN2Q0NmZmNGRiNWRiMjU2ZA==&num=20&model=ME173X&testing=K32L49U5N&deviceid=2448a01d69548db6&channel=official&student_id=400000205&ver=2.1.2";

    ListView listView;
    List<ClassBean> listTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<ResultBean>() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(ResultBean response) {
                listTest = response.getResult().getData().getData();
                Log.i("》》》", listTest.toString());
                listView.setAdapter(new MyAdapter(listTest));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getBaseContext(),Main2Activity.class);
                intent.putExtra("class_id", listTest.get(position).getId());
                intent.putExtra("school_id",listTest.get(position).getSchool_id());
                intent.putExtra("org_id",listTest.get(position).getOrg_id());
                startActivity(intent);
            }
        });


    }

    class MyAdapter extends BaseAdapter{
        List<ClassBean> list;
        public MyAdapter( List<ClassBean> list) {
        this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getBaseContext());
            textView.setText(list.get(position).getName());
            return textView;
        }
    }


}
