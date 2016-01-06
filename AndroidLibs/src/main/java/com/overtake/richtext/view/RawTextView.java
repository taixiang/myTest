package com.overtake.richtext.view;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class RawTextView extends BaseRichTextView {

	@Override
	public int getLength() {
		return mContent.length();
	}

	@Override
	protected void assignType() {
		mType = TYPE_TEXT;
	}

	@Override
	public void initFromJSONObject(JSONObject linkObj) {
		setContent(linkObj.optString("c"));
	}

	@Override
	public String getPlainText() {
		return mContent;
	}

	@Override
	public Drawable getDrawalbe(Context context) {
		return null;
	}
}
