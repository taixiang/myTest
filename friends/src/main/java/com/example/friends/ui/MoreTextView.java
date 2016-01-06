package com.example.friends.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.friends.R;


/**
 * Created by taixiang on 2015/12/16.
 */
public class MoreTextView extends LinearLayout {
    TextView contentTextView;
    int initLines = 6;
    int maxLines = 15;
    boolean PICK_UP = true;
    boolean SEE_ALL = false;
    TextView tipTextView;
    String contentText;
    Context context;
    int lineNum;
    boolean flag = SEE_ALL;
    OnClickListener contentTextViewOnClickListener;
    OnClickListener tipTextViewOnClickListener;

    public MoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.my_text_view,this);
        contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        tipTextView = (TextView) view.findViewById(R.id.tipTextView);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.my_text_view);
//        contentText = typedArray.getString(R.styleable.my_text_view_contentText);
        tipTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tipTextViewOnClick();
            }
        });
        initFlag();
        getLineNum();
    }

    private void tipTextViewOnClick(){
        if(lineNum <= initLines){
            return;
        }
        if(SEE_ALL == flag){
            tipTextView.setText("收起");
            flag = PICK_UP;
            contentTextView.setMaxLines(lineNum);
        }else if(PICK_UP = flag){
            tipTextView.setText("全文");
            flag = SEE_ALL;
            contentTextView.setMaxLines(initLines);
        }
    }

    private void initMyTextView(){

        if(lineNum > maxLines){
            tipTextView.setVisibility(GONE);
            contentTextView.setSingleLine();
            contentTextView.setEllipsize(TextUtils.TruncateAt.END);
        }else if(lineNum > initLines){
            if(tipTextView.getVisibility() != VISIBLE){
                tipTextView.setVisibility(VISIBLE);
            }
            contentTextView.setMaxLines(initLines);
        }else if(lineNum <= initLines){
            if(tipTextView.getVisibility() != GONE){
                tipTextView.setVisibility(GONE);
            }
        }
    }

    private void initFlag(){
        String content = tipTextView.getText().toString();
        if(!("全文".equals(content))){
            tipTextView.setText("全文");
            flag = SEE_ALL;
        }
    }

    private void getLineNum(){
        contentTextView.post(new Runnable() {
            @Override
            public void run() {
                lineNum = contentTextView.getLineCount();
                initMyTextView();
            }
        });
    }

    public void setContentText(String content){
        contentTextView.setText(content);
    }

    public TextView getContentTextView() {
        return contentTextView;
    }

    public TextView getTipTextView() {
        return tipTextView;
    }

    public void setContentTextViewOnClickListener(OnClickListener contentTextViewOnClickListener) {
        this.contentTextViewOnClickListener = contentTextViewOnClickListener;
    }
}
