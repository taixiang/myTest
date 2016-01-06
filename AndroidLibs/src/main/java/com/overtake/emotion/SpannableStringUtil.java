package com.overtake.emotion;

import com.overtake.emotion.SmileArray.SmileValue;

import cn.ikinder.androidlibs.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.widget.TextSwitcher;
import android.widget.TextView;

public class SpannableStringUtil {
	
	private static float h = 0.2083333F;
	
	private final Context mContext;
	
	public SpannableStringUtil(Context context){
		mContext = context;
	}
	
	static Context a(SpannableStringUtil t1){
		return t1.mContext;
	}
	
	public static void a(){
		SmileArray.clear();
		EmojiDrawableUtil.init();
	}
	
	public static Bitmap getBitmap(Context context, String smile){
		SmileValue sv = SmileArray.getSmileByValue(context, smile);
		if(sv != null){
			BitmapDrawable drawable = (BitmapDrawable) SmileArray.getSmileDrawable(context, sv.pos);
			return drawable.getBitmap();
		}
		
		return null;
	}
	
	public static void setText(TextView textview, int txtRes){
		textview.setText(txtRes);
		showSpannableText(textview);
	}
	
	public static void setText(TextSwitcher textview, CharSequence text) {
		textview.setText(text);
		showSpannableText((TextView) textview.getCurrentView());
	}

	public static void setText(TextView textview, CharSequence text) {
		textview.setText(text);
		showSpannableText(textview);
	}
	
	public static SpannableStringBuilder showSpannableText(final TextView textView){
		String text = textView.getText().toString();
		float textSize = textView.getTextSize();
		Context context = textView.getContext();
		int align = DynamicDrawableSpan.ALIGN_BOTTOM;
		if(TextUtils.isEmpty(text)){
			return new SpannableStringBuilder(text);
		}
		
		SpannableStringBuilder spString = getSpannableString(context, text, textSize, align);
		textView.setText(spString);
		return spString;
	}
	
	public static SpannableStringBuilder getSpannableString(Context context, String text, float textSize, int align){
		SpannableStringBuilder spString = new SpannableStringBuilder(text);
		
		int pxEmoji = context.getResources().getDimensionPixelSize(R.dimen.emoji_size);
		
		int pxSmile = context.getResources().getDimensionPixelSize(R.dimen.sizeSmile);
		
		int imageHeight = pxSmile + (int) textSize;
		
		int index = -1;
		while(true){
			index = text.indexOf('[', index + 1);
			if(index != -1){
				if(index < -1 + text.length()){
					String subString = spString.subSequence(index, spString.length()).toString();
					SmileValue sv = SmileArray.getSmileByValue(context, subString);
					if(sv != null){
						Drawable drawable = SmileyDrawableUtil.getSmileDrawable(context, 0, (int)textSize, sv.pos);
						if(drawable != null){
							drawable.setBounds(0,0, imageHeight,imageHeight);
							spString.setSpan(new ImageSpan(drawable, align), index, index + sv.smileValue.length(), 33);
						}
					}
				}
			}else{
				break;
			}
		}
		
		char[] aobj = text.toCharArray();
		
		int j = 0;
		while(j < aobj.length){
			int emojiPos = EmojiUtil.getEmojiPos(aobj[j]);
			
			if(emojiPos == -1){
				if(aobj[j] == ' '){
					BitmapDrawable bk1 = new BitmapDrawable(context.getResources());
					bk1.setBounds(0, 0, (int) ((float) imageHeight * h), (int) ((float) imageHeight * h));
					spString.setSpan(new ImageSpan(bk1, align), j, j + 1, 33);
					
				}
			} else {
				Drawable drawable = SmileyDrawableUtil.getSmileDrawable(context, 1, (int) textSize, emojiPos);
				
				if(drawable != null){
					drawable.setBounds(0, 0, pxEmoji, pxEmoji);
					spString.setSpan(new ImageSpan(drawable, align), j, j + 1, 33);
				}
			}
			j++;
		}
		
		return spString;
	}
	
	public static SpannableStringBuilder getStringWithColor(TextView textView, int prefixLength, int postfixLength){
		SpannableStringBuilder spString = new SpannableStringBuilder(textView.getText());
		if(prefixLength > 0){
			spString.setSpan(new ForegroundColorSpan(Color.BLACK), (textView.getText().length() - postfixLength - prefixLength - 3), (textView.getText().length() - postfixLength - 3), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if (postfixLength > 0) {
			spString.setSpan(new ForegroundColorSpan(Color.BLACK), (textView.getText().length() - postfixLength), (textView.getText().length()), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		SpannableStringUtil.setText(textView, spString);
		return spString;
	}
}
