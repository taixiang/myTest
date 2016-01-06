package com.example.friends.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.friends.CommentItem;
import com.example.friends.R;

/**
 * Created by taixiang on 2015/12/30.
 */
public class CommentItemView extends LinearLayout {
    TextView nameText,replyText,contentText,toNameText;
    CommentItem commentItem;
    public CommentItemView(Context context) {
        super(context);
        initView(context);
    }

    public CommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View.inflate(context, R.layout.view_comment_item,this);
        nameText = (TextView) findViewById(R.id.nameText);
        replyText = (TextView) findViewById(R.id.replyText);
        contentText = (TextView) findViewById(R.id.contentText);
        toNameText = (TextView) findViewById(R.id.toNameText);
    }

    public void setData(CommentItem commentItem){
        this.commentItem = commentItem;
        nameText.setText(commentItem.getName());
        contentText.setText(commentItem.getComment());
        if(commentItem.getToName() != null && commentItem.getToName().length() > 0){
            replyText.setVisibility(VISIBLE);
            toNameText.setVisibility(VISIBLE);
            toNameText.setText(commentItem.getToName());
        }
    }


}
