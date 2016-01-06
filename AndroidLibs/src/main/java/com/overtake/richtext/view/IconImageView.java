package com.overtake.richtext.view;

import org.json.JSONObject;

import com.overtake.richtext.RichTextViewUtil;


import android.content.Context;
import android.graphics.drawable.Drawable;

public class IconImageView extends BaseRichTextView {

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	protected void assignType() {
		mType = BaseRichTextView.TYPE_IMAGE;
	}

	@Override
	public void initFromJSONObject(JSONObject linkObj) {
		setContent(linkObj.optString("c"));
	}

	@Override
	public String getPlainText() {
		return " ";
	}

	@Override
	public Drawable getDrawalbe(Context context) {
		Drawable drawable = RichTextViewUtil.getImgageDrawable(context, mContent);
		return drawable;
	}
}
