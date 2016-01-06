package com.example.listviewsliding;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mListView = (ListView) findViewById(R.id.mListView);

        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.add("选项" + i);
        }
        //实例化自定义内容适配类
        MyAdapter adapter = new MyAdapter(this, this, list);
        //为listView设置适配
        mListView.setAdapter(adapter);

    }


    public void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
