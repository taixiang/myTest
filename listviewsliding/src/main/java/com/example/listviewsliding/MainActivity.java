package com.example.listviewsliding;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 自定义listview对象
     */
    private MyListView myListview;
    /**
     * listView的数据集合
     */
    private List<String> contentList = new LinkedList<String>();
    /**
     * 自定义数据适配器
     */
    private MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化数据
        setData();
        myListview = (MyListView)findViewById(R.id.my_listview);
        adapter = new MyAdapter(this);
        myListview.setAdapter(adapter);
        //添加自定义listview的按钮单击事件，处理删除结果，和普通listview使用的唯一不同之处，
        myListview.setOnItemDeleteListener(new MyListView.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int index) {
                contentList.remove(index);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * @类名称: MyAdapter
     * @描述: 自定义数据适配器
     * @throws
     * @author 徐纪伟
     * 2014年11月9日下午12:20:19
     */
    class MyAdapter extends BaseAdapter {

        private Context context;
        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return contentList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return contentList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            }
            TextView textView = (TextView)convertView.findViewById(R.id.textView1);
            textView.setText(contentList.get(position));
            return convertView;
        }
    }

    /**
     * @方法名称: setData
     * @描述: 初始化数据
     * @param
     * @return void
     * @throws
     * @author 徐纪伟
     * 2014年11月9日 下午12:20:32
     */
    private void setData() {

        for (int i = 0; i < 30; i++) {
            contentList.add("Item "+i);
        }
    }

}
