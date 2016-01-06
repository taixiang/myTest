package com.example.listviewsliding;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by taixiang on 2015/11/9.
 */
public class MyAdapter extends BaseAdapter {

    private List<String> arrays = null;
    private Context mContext;
    //   private Button curDel_btn;
    private float x, ux;
    private Main2Activity mActivity;

    public MyAdapter(Main2Activity activity, Context mContext, List<String> arrays) {
        this.mContext = mContext;
        this.arrays = arrays;
        mActivity = activity;
    }

    @Override
    public int getCount() {
        return arrays.size();
    }

    @Override
    public Object getItem(int position) {
        return arrays.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.textView1);
            viewHolder.btnDel = (Button) convertView.findViewById(R.id.btnDel);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ViewHolder finalViewHolder = viewHolder;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalViewHolder.btnDel.getVisibility() == View.INVISIBLE){
                    mActivity.openActivity();
                }
            }
        });
        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewHolder holder = (ViewHolder) v.getTag();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    x = event.getX();
                    Log.i("》》》x===", " " + x);
                    if (holder.btnDel.getVisibility() == View.VISIBLE) {
                        holder.btnDel.setVisibility(View.INVISIBLE);
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ux = event.getX();
                    if (x - ux > 20) {
                        Log.i("》》》ux===", " " + ux);
                        holder.btnDel.setVisibility(View.VISIBLE);
                        return true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });
        viewHolder.tvTitle.setText(arrays.get(position));
        viewHolder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrays.remove(position);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }

    class ViewHolder {
        TextView tvTitle;
        Button btnDel;
    }


}
