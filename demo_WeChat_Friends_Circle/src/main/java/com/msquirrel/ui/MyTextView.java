package com.msquirrel.ui;

/**
 * Created by taixiang on 2015/12/14.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/4/15 0015.
 */
public class MyTextView extends TextView {
    String tag = "MyTextView";
    TextView contentTextView;
    int initLines = 6;
    boolean PICK_UP = true;
    boolean SEE_ALL = false;
    TextView tipTextView;
    String contentText;
    Context context;
    int lineNum;
    OnClickListener contentTextViewOnClickListener;
    OnClickListener tipTextViewOnClickListener;
    boolean flag = SEE_ALL;

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.my_text_view, null);
        contentTextView = (TextView) view.findViewById(R.id.contentTextView);
        tipTextView = (TextView) view.findViewById(R.id.tipTextView);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.my_text_view);
        contentText = typedArray.getString(R.styleable.my_text_view_contentText);
        tipTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                tipTextViewOnClick();
            }
        });
        initFlag();
        getLineNum();
    }

    /**
     * �ڴ�ʵ�ֶ�contentTextView����ʾ����
     */
    private void tipTextViewOnClick() {
        if (lineNum <= initLines) {
            return;
        }
        if (SEE_ALL == flag) {
            tipTextView.setText("����");
            flag = PICK_UP;
            // ���ȫ��
            contentTextView.setMaxLines(lineNum);
        } else if (PICK_UP == flag) {
            tipTextView.setText("ȫ��");
            flag = SEE_ALL;
            // �������
            contentTextView.setMaxLines(initLines);
        }
    }

    /**
     * ��ʼ��MyTextView
     */
    private void initMyTextView() {
        if (lineNum > initLines) {
            if (tipTextView.getVisibility() != VISIBLE) {
                tipTextView.setVisibility(VISIBLE);
            }
            contentTextView.setMaxLines(initLines);
        } else if (lineNum <= initLines) {
            if (tipTextView.getVisibility() != GONE) {
                tipTextView.setVisibility(GONE);
            }

        }
    }

    /**
     * ��ʼ��flag
     */
    private void initFlag() {
        String content = tipTextView.getText().toString();
        if (!("ȫ��".equals(content))) {
            tipTextView.setText("ȫ��");
            flag = SEE_ALL;
        }
    }

    /**
     * ��ȡcontentTextView���������
     */
    private void getLineNum() {
        /** �·��� */
//        lineNum=contentTextView.getHeight()/contentTextView.getLineHeight();
//        Log.d(tag,"lineNum = "+lineNum);
//        initMyTextView();
        /** �·������� */
        contentTextView.post(new Runnable() {
            @Override
            public void run() {
                lineNum = contentTextView.getLineCount();
                Log.d(tag, "lineNum=" + lineNum);
//                lineNum=11;
                initMyTextView();
            }
        });
    }

    public TextView getContentTextView() {
        return contentTextView;
    }

    public TextView getTipTextView() {
        return tipTextView;
    }

    public void setContentTextViewOnClickListener(OnClickListener contentTextViewOnClickListener) {
        contentTextView.setOnClickListener(contentTextViewOnClickListener);
    }

}
