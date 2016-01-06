package com.example.mylinechart;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {

    BarChart barChart;
    ArrayList<Float> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        barChart = (BarChart) findViewById(R.id.barChart);
        list.add((float) 20);
        list.add((float) 25);
        list.add((float) 39);
        list.add((float) 12);
        list.add((float) 34);
        list.add((float) 30);
        barChart.setBarsData(list,"2001","2002","2003","2004","2005");
    }

}
