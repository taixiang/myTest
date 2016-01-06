package com.example.friends.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.friends.Item;
import com.example.friends.R;
import com.example.friends.UserImage;

import java.util.List;

/**
 * Created by taixiang on 2015/12/16.
 */
public class ItemView extends LinearLayout implements View.OnClickListener{
    public interface ICommentListener{
        void comment(int position);
    }

    public interface IItemContentListener{
        void addContent(int position,int num);
    }

    private LinearLayout comment_container;
    private NoScrollGridView gridView;
    private ImageView feed_more,logoImage;
    private MoreTextView moreTextView;
    private TextView name,time;
    private PopupWindow mPopupWindow;
    private int width;
    private int height;
    private int position;
    private Item mData;
    private ICommentListener commentListener;
    private IItemContentListener itemContentListener;

    public ItemView(Context context) {
        super(context);
        initView(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        View.inflate(context,R.layout.friend_circle_item,this);
        comment_container = (LinearLayout) findViewById(R.id.comment_container);
        gridView = (NoScrollGridView) findViewById(R.id.gridView);
        moreTextView = (MoreTextView) findViewById(R.id.moreTextView);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        feed_more = (ImageView) findViewById(R.id.feed_more);
        logoImage = (ImageView) findViewById(R.id.logoImage);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setmData(Item mData) {
        this.mData = mData;
        logoImage.setImageResource(mData.getPortraitId());
        name.setText(mData.getNickName());
        time.setText(mData.getCreatedAt());
        moreTextView.setContentText(mData.getContent());
        if(mData.getImages()!= null){
            gridView.setVisibility(VISIBLE);
            gridView.setAdapter(new GridAdapter(mData.getImages()));
        }
        feed_more.setOnClickListener(this);

        updateComment();

    }

    public void setCommentListener(ICommentListener commentListener) {
        this.commentListener = commentListener;
    }

    public void setItemContentListener(IItemContentListener itemContentListener) {
        this.itemContentListener = itemContentListener;
    }

    private void show_more(View moreBtnView){
        if(mPopupWindow == null){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.show_more,null);
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
   //       mPopupWindow.setAnimationStyle(R.style.popShowAnim);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(false);
            mPopupWindow.setFocusable(true);

            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            width = view.getMeasuredWidth();
            height = view.getMeasuredHeight();
            View parent = mPopupWindow.getContentView();
            ImageView like = (ImageView) parent.findViewById(R.id.like);
            ImageView comment = (ImageView) parent.findViewById(R.id.comment);
            comment.setOnClickListener(this);
        }

        if(mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }else {
            int heightMore = moreBtnView.getHeight();
            mPopupWindow.showAsDropDown(moreBtnView,-width,-(height+heightMore)/2);
        }
    }

    private class GridAdapter extends BaseAdapter{
        List<UserImage> images;
        public GridAdapter(List<UserImage> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object getItem(int position) {
            return images.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.image_item,null);
                holder.imageView = (ImageView) convertView.findViewById(R.id.user_img);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageResource(images.get(position).getImage());
            return convertView;
        }
    }

    private class ViewHolder{
        ImageView imageView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feed_more:
                show_more(v);
                break;
            case R.id.comment:
                if(commentListener != null){
                    commentListener.comment(position);
                }
                if(mPopupWindow != null && mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
                }
                break;
        }
    }

    public void updateComment(){
        if(mData.getComments().size() > 0){
            comment_container.removeAllViews();
            for(int i=0;i<mData.getComments().size();i++){
                CommentItemView commentItemView = new CommentItemView(getContext());
                commentItemView.setData(mData.getComments().get(i));
                final int finalI = i;
                commentItemView.setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(itemContentListener != null){
                            itemContentListener.addContent(position,finalI);
                        }
                    }
                });
                comment_container.addView(commentItemView);
            }
        }else {
            comment_container.setVisibility(GONE);
        }
    }

    public ImageView getFeed_more() {
        return feed_more;
    }
}
