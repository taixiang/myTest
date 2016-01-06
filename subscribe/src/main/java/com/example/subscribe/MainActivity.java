package com.example.subscribe;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.overtake.base.OTJson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    GridView grid1,grid2;
    Button editor;
    List<String> list1 = new ArrayList<>() ;
    List<String> list2 = new ArrayList<>();
    Adapter1 adapter1 = new Adapter1(list1);
    Adapter2 adapter2 ;
    boolean isShow;
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        grid1 = (GridView) findViewById(R.id.grid1);
        grid2 = (GridView) findViewById(R.id.grid2);
        editor = (Button) findViewById(R.id.editor);
        initData();

        sp = getSharedPreferences("MainActivity", MODE_PRIVATE);
        spEditor = sp.edit();
        int size = sp.getInt("list1",0);
        if(size!=0){
            for(int i=0;i<size;i++){
                list1.clear();
                list1.add(sp.getString("item"+i,null));
            }
        }

        adapter2 = new Adapter2();
        grid1.setAdapter(adapter1);
        grid2.setAdapter(adapter2);
        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShow){
                    isShow = true;
                    adapter1.setIsShow(isShow);
                }else {
                    isShow = false;
                    adapter1.setIsShow(isShow);
                }
            }
        });

        grid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String cur = list2.get(position);
                list2.remove(position);
                adapter2.notifyDataSetChanged();

                list1.add(cur);
                adapter1.notifyDataSetChanged();
                spEditor.putInt("list1",list1.size());
                for(int i=0;i<list1.size();i++){
                    spEditor.putString("item"+i,list1.get(i));
                }

                spEditor.apply();
            }
        });
    }



    class Adapter1 extends BaseAdapter{
        List<String> list;
        boolean isShow;
        public Adapter1(List<String> list){
            this.list = list;

        }

        public void setIsShow(boolean isShow){
            this.isShow = isShow;
            notifyDataSetChanged();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
           ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.view_item_grid1,null);
                holder.textView = (TextView) convertView.findViewById(R.id.item);
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(isShow){
                holder.imageView.setVisibility(View.VISIBLE);
            }else {
                holder.imageView.setVisibility(View.INVISIBLE);
            }
            holder.textView.setText(list.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onClick(View v) {
                    String cur = list1.get(position);
                    list1.remove(position);
                    adapter1.notifyDataSetChanged();
                    list2.add(cur);
                    adapter2.notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }


    class Adapter2 extends BaseAdapter{
        @Override
        public int getCount() {
            return list2.size();
        }

        @Override
        public Object getItem(int position) {
            return list2.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getBaseContext());
            textView.setText(list2.get(position));
            return textView;
        }
    }

    class ViewHolder{
        TextView textView;
        ImageView imageView;
    }

    private void initData(){
        for (int i =0;i<10;i++){
            list1.add("item"+i);
        }
        for(int i=0;i<10;i++){
            list2.add("test"+i);
        }
    }


}
