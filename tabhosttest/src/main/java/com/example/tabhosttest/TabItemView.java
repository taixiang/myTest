package com.example.tabhosttest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by taixiang on 2015/11/2.
 */
public class TabItemView extends RelativeLayout {

    ImageView imageView;

    TextView textView, flagText;

    public TabItemView(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_iten_view,null);
         imageView = (ImageView) view.findViewById(R.id.image);

         textView = (TextView) view.findViewById(R.id.text);
    }




    public void setUp(int iconResId, String titleResId) {
        imageView.setImageResource(iconResId);
        textView.setText(titleResId);
    }

    public void setNum(int num) {
        if (num > 0) {
            if (num > 99) {
                flagText.setText("99+");
            } else {
                flagText.setText(String.valueOf(num));
            }

            flagText.setVisibility(VISIBLE);
        } else {
            flagText.setVisibility(GONE);
        }
    }
}