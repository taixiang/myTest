package com.overtake.richtext;

import java.util.HashMap;

import cn.ikinder.androidlibs.R;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class RichTextViewUtil {

	private static RichTextViewUtil mInstance;
	private static HashMap<String, Drawable> iconCacheHashMap = new HashMap<String, Drawable>();

	public int imageHeight;
	public int pxSmile;
	public int textSize;

	public static RichTextViewUtil getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new RichTextViewUtil(context);
		}
		return mInstance;
	}

	private RichTextViewUtil(Context context) {
		pxSmile = context.getResources().getDimensionPixelSize(R.dimen.sizeSmile);
		textSize = context.getResources().getDimensionPixelSize(R.dimen.richtext_textsize);
		imageHeight = pxSmile + (int) textSize;
	}

	public static Drawable getImgageDrawable(Context context, String iconStr) {
		int pos = iconStr.indexOf(".");
		if (pos != -1) {
			iconStr = iconStr.substring(0, pos);
		}
		if (!iconCacheHashMap.containsKey(iconStr)) {
			int res = context.getResources().getIdentifier(iconStr, "drawable", context.getPackageName());
			if (res == 0) {
				return null;
			} else {
				Drawable drawable = context.getResources().getDrawable(res);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
				iconCacheHashMap.put(iconStr, drawable);
			}
		}
		return iconCacheHashMap.get(iconStr);
	}
}
