package com.overtake.richtext.view;

import org.json.JSONObject;

import com.overtake.emotion.EmojiUtil;
import com.overtake.emotion.SmileArray;
import com.overtake.emotion.SmileyDrawableUtil;
import com.overtake.emotion.SmileArray.SmileValue;
import com.overtake.richtext.RichTextViewUtil;


import android.content.Context;
import android.graphics.drawable.Drawable;

public class EmotionView extends BaseRichTextView {

	@Override
	public int getLength() {
		return mContent.length();
	}

	@Override
	protected void assignType() {
		mType = TYPE_EMOTION;
	}

	@Override
	public void initFromJSONObject(JSONObject linkObj) {
		JSONObject contentObj = linkObj.optJSONObject("c");
		mContent = contentObj.optString("c");
		mSubType = contentObj.optInt("t");
	}

	@Override
	public String getPlainText() {
		return mContent;
	}

	@Override
	public Drawable getDrawalbe(Context context) {

		int textSize = RichTextViewUtil.getInstance(context).textSize;
		int imageHeight = RichTextViewUtil.getInstance(context).imageHeight;

		String face = mContent;
		Drawable drawable = null;

		if (mSubType == EMOTION_SMILE) {
			SmileValue sv = SmileArray.getSmileByValue(context, face);
			if (sv != null) {
				drawable = SmileyDrawableUtil.getSmileDrawable(context, 0, (int) textSize, sv.pos);
				if (drawable != null) {
					drawable.setBounds(0, 0, imageHeight, imageHeight);
				}
			}
		} else {
			char charAt = face.charAt(0);
			int emojiPos = EmojiUtil.getEmojiPos(charAt);

			drawable = SmileyDrawableUtil.getSmileDrawable(context, 1, (int) textSize, emojiPos);
			if (drawable != null) {
				drawable.setBounds(0, 0, imageHeight, imageHeight);
			}
		}
		return drawable;
	}
}
