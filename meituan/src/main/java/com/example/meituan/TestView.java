package com.example.meituan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.zip.Inflater;

/**
 * Created by taixiang on 2015/11/23.
 */
public class TestView extends RelativeLayout {
    private TextView textView;
    public TestView(Context context) {
        super(context);
        init(context);
    }

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    init(context);
    }

    private void init(Context context){
       View.inflate(context,R.layout.activity_search,this);
        textView = (TextView) findViewById(R.id.text);
    }



}
