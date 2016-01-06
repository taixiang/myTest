package com.overtake.richtext.view;

import org.json.JSONObject;

import cn.ikinder.androidlibs.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class LocationView extends BaseRichTextView {

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	protected void assignType() {
		mType = TYPE_LOCATION;
	}

	@Override
	public void initFromJSONObject(JSONObject linkObj) {
	}

	@Override
	public String getPlainText() {
		return " ";
	}

	@Override
	public Drawable getDrawalbe(Context context) {
		Drawable drawable = context.getResources().getDrawable(R.drawable.richtext_icon_location);
		if (drawable != null)
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		return drawable;
	}
}
