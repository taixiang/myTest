package com.overtake.emotion.view;

import cn.ikinder.androidlibs.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

public class DotView extends LinearLayout implements OnClickListener {

	public interface OnDotClickHandler {
		public void onDotClick(int index);
	}

	private float mLittleDotSize = 0f;
	private float mDotSpan = 10f;
	private float mDotRadius = 3.25f;

	private int mSelectedColor = 0xFF69BE37;
	private int mUnSelectedColor = 0xFFB3B3B3;
	private OnDotClickHandler mOnDotClickHandler;

	public DotView(Context context) {
		super(context);
	}

	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setGravity(Gravity.CENTER_HORIZONTAL);

		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.DotView, 0, 0);
		if (arr != null) {
			if (arr.hasValue(R.styleable.DotView_dot_radius)) {
				mDotRadius = arr.getDimension(R.styleable.DotView_dot_radius, mDotRadius);
			}

			if (arr.hasValue(R.styleable.DotView_dot_span)) {
				mDotSpan = arr.getDimension(R.styleable.DotView_dot_span, mDotSpan);
			}

			mSelectedColor = arr.getColor(R.styleable.DotView_dot_selected_color, mSelectedColor);
			mUnSelectedColor = arr.getColor(R.styleable.DotView_dot_unselected_color, mUnSelectedColor);
			arr.recycle();
		}

		mLittleDotSize = (mDotSpan / 2 + mDotRadius * 2);
	}

	public final void init(int num) {
		if (num < 0)
			return;

		removeAllViews();
		setOrientation(HORIZONTAL);
		for (int i = 0; i < num; i++) {
			LittleDot dot = new LittleDot(getContext(), i);
			if (i == 0) {
				dot.setColor(mSelectedColor);
			} else {
				dot.setColor(mUnSelectedColor);
			}
			dot.setLayoutParams(new LayoutParams((int) mLittleDotSize, (int) mLittleDotSize));
			dot.setClickable(true);
			dot.setOnClickListener(this);
			addView(dot);
		}
	}

	public void setOnDotClickHandler(OnDotClickHandler handler) {
		mOnDotClickHandler = handler;
	}

	@Override
	public void onClick(View v) {

		if (v instanceof LittleDot && null != mOnDotClickHandler) {
			mOnDotClickHandler.onDotClick(((LittleDot) v).getIndex());
		}
	}

	public final void setSelected(int index) {
		if (index >= getChildCount() || index < 0)
			return;
		for (int i = 0; i < getChildCount(); i++) {
			int color = 0;
			if (i == index) {
				color = mSelectedColor;
			} else {
				color = mUnSelectedColor;
			}
			((LittleDot) getChildAt(i)).setColor(color);
		}
	}

	private class LittleDot extends View {

		private int mColor;
		private Paint mPaint;
		private int mIndex;

		public LittleDot(Context context, int index) {
			super(context);
			mPaint = new Paint();
			mIndex = index;
		}

		public int getIndex() {
			return mIndex;
		}

		public void setColor(int color) {
			mColor = color;
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			mPaint.setColor(mColor);
			canvas.drawCircle(mLittleDotSize / 2, mLittleDotSize / 2, mDotRadius, mPaint);
		}
	}
}
