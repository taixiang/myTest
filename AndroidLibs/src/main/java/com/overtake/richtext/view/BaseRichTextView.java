package com.overtake.richtext.view;

import java.util.HashMap;

import org.json.JSONObject;

import com.overtake.utils.HashMapWrapper;

import android.content.Context;
import android.graphics.drawable.Drawable;

public abstract class BaseRichTextView {

	public static final int TYPE_UNKNOWN = 0;

	public static final int TYPE_LOCATION = 2;

	public static final int TYPE_EMOTION = 6;

	public static final int TYPE_IMAGE = 7;

	public static final int TYPE_TEXT = 8;

	public static final int TYPE_UNREAD_COMMENT_NUM = 101;

	public static final int EMOTION_SMILE = 0;
	public static final int EMOTION_EMOJI = 1;

	protected int mType;
	protected int mSubType = -1;
	protected String mContent;

	public BaseRichTextView() {
		assignType();
	}

	public void setContent(String content) {
		mContent = content;
	}

	public String getContent() {
		return mContent;
	}

	public void setSubType(int subType) {
		mSubType = subType;
	}

	@SuppressWarnings("rawtypes")
	public HashMap toHashMap() {
		HashMapWrapper map = new HashMapWrapper();
		map.put("t", String.valueOf(mType));
		if (mSubType != -1) {
			HashMapWrapper child = map.getChild("c");
			child.put("c", mContent);
			child.put("t", String.valueOf(mSubType));

		} else {
			map.put("c", mContent);
		}
		return map.getData();
	}

	protected abstract void assignType();

	public abstract int getLength();

	public abstract void initFromJSONObject(JSONObject jsonObject);

	public abstract String getPlainText();

	public abstract Drawable getDrawalbe(Context context);
}
