package com.msquirrel.adapter;

import java.util.List;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.msquirrel.bean.UserInfo;
import com.msquirrel.ui.MoreTextView;
import com.msquirrel.ui.R;
import com.msquirrel.widget.NoScrollGridView;

public class WeChatAdapter extends BaseAdapter implements View.OnClickListener {

	private List<UserInfo> mList;
	private Context mContext;
	private PopupWindow mPopupWindow;
	private int width ;
	private int height ;
	private ICommentListener iCommentListener;


	public interface ICommentListener{
		public void comment();
	}

	public void setiCommentListener(ICommentListener iCommentListener) {
		this.iCommentListener = iCommentListener;
	}

	public WeChatAdapter(Context _context) {
		this.mContext = _context;
	}

	public void setData(List<UserInfo> _list) {
		this.mList = _list;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public UserInfo getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.friends_circle_item, parent, false);
			holder.gridView = (NoScrollGridView) convertView
					.findViewById(R.id.gridView);
			holder.feed_more = (ImageView) convertView.findViewById(R.id.feed_more);
			holder.moreTextView = (MoreTextView) convertView.findViewById(R.id.content);
			holder.comment_container = (LinearLayout) convertView.findViewById(R.id.comment_container);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		UserInfo mUserInfo = getItem(position);
		if (mList != null && mList.size() > 0) {
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new MyGridAdapter(mUserInfo.getUi(),
					mContext));
			holder.gridView
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
												View view, int position, long id) {
							// imageBrower(position,bean.urls);
						}
					});
			holder.moreTextView.setContentText("23333");
			holder.feed_more.setOnClickListener(this);
			TextView textView = new TextView(mContext);
			textView.setText("2222");
			holder.comment_container.addView(textView);
		}
		return convertView;
	}

	public class ViewHolder {
		LinearLayout comment_container;
		NoScrollGridView gridView;
		ImageView feed_more;
		MoreTextView moreTextView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.feed_more:
				showMore(v);
				break;
			case R.id.comment:
				if(iCommentListener != null){
					iCommentListener.comment();
				}
				if(mPopupWindow != null && mPopupWindow.isShowing()){
					mPopupWindow.dismiss();
				}
				break;
		}
	}

	private void showMore(View moreBtnView){
		if(mPopupWindow == null){
			View view =LayoutInflater.from(mContext).inflate(R.layout.show_more,null);
			mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopupWindow.setAnimationStyle(R.style.popShowAnim);
			mPopupWindow.setTouchable(true);
			mPopupWindow.setOutsideTouchable(false);
			mPopupWindow.setFocusable(true);

			view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			width = view.getMeasuredWidth();
			height = view.getMeasuredHeight();
			View parent = mPopupWindow.getContentView();
			TextView like = (TextView) parent.findViewById(R.id.like);
			TextView comment = (TextView) parent.findViewById(R.id.comment);
			comment.setOnClickListener(this);
		}

		if(mPopupWindow.isShowing()){
			mPopupWindow.dismiss();
		}else {
			int heightMore = moreBtnView.getHeight();
			mPopupWindow.showAsDropDown(moreBtnView,-width,-(height+heightMore)/2);
		}
	}


}
