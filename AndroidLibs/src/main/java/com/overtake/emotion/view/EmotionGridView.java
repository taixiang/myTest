package com.overtake.emotion.view;

import com.overtake.emotion.EmojiDrawableUtil;
import com.overtake.emotion.SmileArray;
import com.overtake.emotion.view.EmotionPanelView.OnEmotionClickHandler;
import com.overtake.utils.LocalDisplay;

import cn.ikinder.androidlibs.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class EmotionGridView extends GridView{
	public enum EmotionType{
		system, emoji, delete
	}
	/*--- the fields about page ---*/
	private int mItemsPerPage;
	private int mTotalNum;
	private int mCurrentPage;
	
	
	private int mColumnWidth;
	/*---  the type ---*/
	private EmotionType mType;
	
	/*--- callback ---*/
	private OnItemClickListener onItemClickListener;
	private OnEmotionClickHandler onClickSmileL;
	
	/*--- adapter ---*/
	private SmileyBaseAdapter mAdapter;
	
	public EmotionGridView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		mItemsPerPage = 0;
		onItemClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (id < 0)
					return;
				int totalSmiles = mTotalNum;

				if (position != -1 + mAdapter.getCount()) {
					int itemsPerPage = mItemsPerPage - 1;
					if (position + mCurrentPage * itemsPerPage < totalSmiles) {
						onClickSmileL.onClickSmile(mType, position + mCurrentPage * itemsPerPage, "");
					}
				} else {
					onClickSmileL.onClickSmile(EmotionType.delete, -1, "");
				}
			}
		};
		mAdapter = new SmileyBaseAdapter();
		setBackgroundResource(0);
		setStretchMode(2);
        setPadding(10,0,10,0);
        setHorizontalSpacing(10);
		computeColumnWidth();
		setAdapter(mAdapter);
		setOnItemClickListener(onItemClickListener);
	}
	
	public void reSetOnItemClickListener(){
		setOnItemClickListener(onItemClickListener);
	}
	public SmileyBaseAdapter getSmileyAdapter(){
		return mAdapter;
	}
	private void computeColumnWidth() {
		mColumnWidth = LocalDisplay.getScaledWidthPixelsByDesignDP(38) * 3;
		setColumnWidth(mColumnWidth);
	}
	
	public final void setParameters(EmotionType type, int pageIndex, int total, int itemsPerPage, int totalPage, int numColumn) {
		mType = type;
		mCurrentPage = pageIndex;
		mTotalNum = total;
		mItemsPerPage = itemsPerPage;
		computeColumnWidth();
		setNumColumns(numColumn);
	}
	public class SmileyBaseAdapter extends BaseAdapter{
		ViewHolder holder;
		@Override
		public int getCount() {
			return mItemsPerPage;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}
		
		private boolean isTheLastItemInThisPage(int position) {
			return position == getCount() - 1;
		}

		private int getRealIndex(int position) {
			return position + (mItemsPerPage - 1) * mCurrentPage;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parentView) {
			int realIndex = getRealIndex(position);
			
			//get ViewHolder
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(getContext(), R.layout.emotion_smiley_grid_item_s, null);
				ImageView ivSmiley = (ImageView) convertView.findViewById(R.id.art_emoji_icon_iv);
				holder.text = ivSmiley;
				convertView.setTag(holder);
			}else{
				 holder = (ViewHolder) convertView.getTag();
			}
			
			//set the emotion
			if (isTheLastItemInThisPage(position)) {
				holder.text.setImageResource(R.drawable.global_input_delete);
			} else if (realIndex >= mTotalNum) {
				holder.text.setImageDrawable(null);
			} else if (mType == EmotionType.system) {
				Drawable smileDrawable = SmileArray.getSmileDrawable(getContext(), realIndex);
				holder.text.setImageDrawable(smileDrawable);
			} else {
				int pos = EmojiDrawableUtil.getPosition(getContext())[realIndex];
				holder.text.setImageDrawable(EmojiDrawableUtil.getEmojiDrawable(getContext(), pos));
			}
			return convertView;
		}
		
	}
	
	class ViewHolder {
		ImageView text;
    }

	public void setOnClickSmile(OnEmotionClickHandler mOnEmotionClickHandler) {
		this.onClickSmileL= mOnEmotionClickHandler;
	}
}
