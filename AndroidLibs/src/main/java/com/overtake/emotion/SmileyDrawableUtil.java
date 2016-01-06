package com.overtake.emotion;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class SmileyDrawableUtil {
	
	private static final int SIZE_POOL = 10;
	
	static Map<String, Drawable> pool = new LinkedHashMap<String, Drawable>(SIZE_POOL, 0.75f, true){
		private static final long serialVersionUID = 1L;
		
		@Override
		protected boolean removeEldestEntry(Entry<String, Drawable> eldest){
			if(size() > SIZE_POOL){
				return true;
			}
			
			return false;
		}
	};
	
	static Map<String, LinkedHashMap<String, String>> defaultSmileTypePool = new LinkedHashMap<String, LinkedHashMap<String, String>>(2, 0.75f, true) {

		private static final long serialVersionUID = 2L;

		@Override
		protected boolean removeEldestEntry(Entry<String, LinkedHashMap<String, String>> eldest) {
			if (size() > 10) {
				return true;
			}
			return false;
		}
	};
	
	static Map<String, LinkedHashMap<String, String>> emojiSmileTypePool = new LinkedHashMap<String, LinkedHashMap<String, String>>(2, 0.75f, true) {

		private static final long serialVersionUID = 3L;

		@Override
		protected boolean removeEldestEntry(Entry<String, LinkedHashMap<String, String>> eldest) {
			if (size() > 20) {
				return true;
			}
			return false;
		}
	};

	static Map<String, Drawable> smileDrawablePool = new LinkedHashMap<String, Drawable>(40, 0.75f, true) {

		private static final long serialVersionUID = -6821637213250330592L;

		@Override
		protected boolean removeEldestEntry(Entry<String, Drawable> eldest) {
			if (size() > 40) {
				return true;
			}
			return false;
		}
	};
	
	public static Drawable getSmileDrawable(Context context, int type, int textSize, int pos){
		Map<String, LinkedHashMap<String, String>> typePool = type == 0 ? defaultSmileTypePool : emojiSmileTypePool;
		
		String id = null;
		
		LinkedHashMap<String, String> posPool = typePool.get(String.valueOf(textSize));
		if(posPool == null){
			posPool = new LinkedHashMap<String, String>(40, 0.75f, true){
				private static final long serialVersionUID = 4L;
				@Override
				protected boolean removeEldestEntry(Entry<String, String> eldest){
					if(size() > 40){
						return true;
					}
					return false;
				}
			};
			
			typePool.put(String.valueOf(textSize), posPool);
		}else{
			id = posPool.get(String.valueOf(pos));
			if(smileDrawablePool.containsKey(id)){
				return smileDrawablePool.get(id);
			}
		}
		
		id = String.valueOf(type) + "_" + textSize + "_" + pos;
		Drawable srcDrawable = type == 0 ? SmileArray.getSmileDrawable(context, pos) : EmojiDrawableUtil.getEmojiDrawable(context, pos);
		Drawable destDrawable = createDrawable(context, srcDrawable, textSize);
		
		posPool.put(String.valueOf(pos), id);
		smileDrawablePool.put(id, destDrawable);
		
		return destDrawable;
	}
	
	public static Drawable createDrawable(Context context, Drawable drawable, int textSize) {
		if (drawable == null) {
			return null;
		}
		int allWidth = (int) (textSize * 1.2 + 0.5);
		int allHeight = allWidth;

		Bitmap image = Bitmap.createBitmap(allWidth, allHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(image);

		drawable.setBounds(0, 0, textSize + 2, textSize + 2);
		drawable.draw(canvas);
		image.setDensity(Bitmap.DENSITY_NONE);

		return new BitmapDrawable(context.getResources(), image);
	}
}
