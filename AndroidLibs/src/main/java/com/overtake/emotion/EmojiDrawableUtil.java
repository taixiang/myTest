package com.overtake.emotion;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.ikinder.androidlibs.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class EmojiDrawableUtil {
	
	private static EmojiDrawableUtil instance = null;
	
	private String emojiCodeList[]= null;
	
	private int position[] = null;
	
	private EmojiDrawableUtil(Context context){
		emojiCodeList = context.getResources().getStringArray(R.array.emoji_code);
		
		String as[] = context.getResources().getStringArray(R.array.emoji_file);
		
		position = new int[as.length];
		int i = 0;
		do{
			if( i >= as.length){
				return;
			}
			
			position[i] = Integer.parseInt(as[i]);
			i++;
		} while(true);
	}
	
	static Map<Integer, Drawable> activityViewCache = new LinkedHashMap<Integer, Drawable>(50, 0.75f, true) {

		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Entry<Integer, Drawable> eldest) {
			return true;
		}
	};
	
	public static EmojiDrawableUtil getInstance(Context context){
		if(instance == null){
			instance = new EmojiDrawableUtil(context);
		}
		return instance;
	}
	
	public static Drawable getEmojiDrawable(Context context, int pos){
		if(activityViewCache.containsKey(pos)){
			return activityViewCache.get(pos);
		}
		
		int j = context.getResources().getIdentifier("emoji_" + pos, "drawable", context.getPackageName());
		
		if(j == 0){
			return null;
		}
		
		BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(j);
		activityViewCache.put(pos, drawable);
		return drawable;
	}
	
	public static void init(){
		instance = null;
	}
	
	public static int[] getPosition(Context context){
		return EmojiDrawableUtil.getInstance(context).position;
	}
	
	public static String[] getCodeList(Context context){
		return EmojiDrawableUtil.getInstance(context).emojiCodeList;
	}
}
