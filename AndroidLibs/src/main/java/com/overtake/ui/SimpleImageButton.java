package com.overtake.ui;

import com.overtake.ui.SimpleButton.SimpleButtonBackgroundDrawable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class SimpleImageButton extends ImageButton {

	public SimpleImageButton(Context context) {
		super(context);
	}

	public SimpleImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setImageDrawable(Drawable d) {

		SimpleButtonBackgroundDrawable drawable = new SimpleButtonBackgroundDrawable(d);

		super.setImageDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId) {

		Drawable drawable = getResources().getDrawable(resId);

		setImageDrawable(drawable);
	}

}
