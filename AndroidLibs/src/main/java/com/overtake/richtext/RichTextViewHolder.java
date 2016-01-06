package com.overtake.richtext;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.overtake.richtext.view.BaseRichTextView;
import com.overtake.richtext.view.EmotionView;
import com.overtake.richtext.view.RawTextView;
import com.overtake.utils.OTLog;
import com.overtake.utils.Utils;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

public class RichTextViewHolder {

	private ArrayList<BaseRichTextView> viewList = new ArrayList<BaseRichTextView>();
	private boolean mDrawed = false;

	private String mShowContentString;
	private SpannableString mSpannableString;
	private boolean mIsRawText = true;

	public void performDraw(Context context) {
		if (mDrawed)
			return;
		mDrawed = true;

		StringBuilder sb = new StringBuilder();

		for (BaseRichTextView richTextView : viewList) {
			if (!(richTextView instanceof RawTextView)) {
				mIsRawText = false;
			}
			sb.append(richTextView.getPlainText());
		}

		mShowContentString = sb.toString();
		mSpannableString = new SpannableString(mShowContentString);

		int start = 0;
		int end = 0;
		for (BaseRichTextView richTextView : viewList) {
			Drawable drawable = richTextView.getDrawalbe(context);
			end = start + richTextView.getLength();
			if (drawable != null) {
				if (richTextView instanceof EmotionView) {
					mSpannableString.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM), start, end, 33);
				} else {
					mSpannableString.setSpan(new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE), start, end, 33);
				}
			}
			start = end;
		}
	}

	public void renderTextView(TextView textView) {
		renderTextView(textView.getContext(), textView, true);
	}

	public void renderTextView(Context context, TextView textView, boolean setGoneIfEmpty) {
		performDraw(context);

		if (viewList.size() == 0 || Utils.isNullOrEmpty(mShowContentString)) {
			if (setGoneIfEmpty) {
				textView.setVisibility(View.GONE);
				return;
			}
		}
		textView.setVisibility(View.VISIBLE);
		if (mIsRawText) {
			textView.setText(mShowContentString);
		} else {
			textView.setText(mSpannableString);
		}
	}

	public ArrayList<BaseRichTextView> getViewList() {
		return viewList;
	}

	public static RichTextViewHolder createFromJSONString(String jsonString) {
		RichTextViewHolder wrapper = new RichTextViewHolder();
		try {
			JSONArray contentArray = new JSONArray(jsonString);
			int length = contentArray == null ? 0 : contentArray.length();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = contentArray.optJSONObject(i);
					BaseRichTextView view = RichTextViewFatory.crateFromJSONObject(jsonObject);
					if (view != null) {
						wrapper.viewList.add(view);
					}
				}
			}
		} catch (Exception e) {
            OTLog.e("kb_error", String.format("createFromJSONString %s", e));
		}
		return wrapper;
	}

	public static RichTextViewHolder createFromRawString(String srcText,Context context) {
		ArrayList<BaseRichTextView> list = RichTextViewFatory.createViewListFromRawString(srcText,context);
		RichTextViewHolder wrapper = new RichTextViewHolder();
		wrapper.viewList.addAll(list);
		return wrapper;
	}

	public String toJsonString() {
		ArrayList list = new ArrayList();
		for (BaseRichTextView view : viewList) {
			list.add(view.toHashMap());
		}
		return JSONValue.toJSONString(list);
	}
}
