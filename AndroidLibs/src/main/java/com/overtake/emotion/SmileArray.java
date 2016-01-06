package com.overtake.emotion;

import cn.ikinder.androidlibs.R;
import android.content.Context;
import android.graphics.drawable.Drawable;

public class SmileArray {
	
	private static SmileArray instance = null;
	
	private String[] mSmileValues = null;
	
	private static int mSmileDimen;
	
	private SmileArray(Context context){
		mSmileValues = context.getResources().getStringArray(R.array.smile_values);
		mSmileDimen = context.getResources().getDimensionPixelOffset(R.dimen.smile_size);
	}
	
	public static SmileArray getInstance(Context context){
		if(instance == null){
			instance = new SmileArray(context);
		}
		
		return instance;
	}
	
	public static Drawable getSmileDrawable(Context context, int pos){
		String emotionPic = (new StringBuilder(10)).append("smile_").append(pos).toString();
		int res = context.getResources().getIdentifier(emotionPic, 
				 	"drawable", context.getPackageName());
		if(res == 0){
			return null;
		}else{
			Drawable drawable = context.getResources().getDrawable(res);
			if(drawable != null){
				drawable.setBounds(0, 0, mSmileDimen, mSmileDimen);
			}
			return drawable;
		}
	}
	
	public static SmileValue getSmileByValue(Context context, String defaultSmiley){
		SmileArray init = SmileArray.getInstance(context);
		int count = init.mSmileValues.length;
		SmileValue retValue = new SmileValue();
		for(int i = 0; i < count; i++){
			if(defaultSmiley.startsWith(init.mSmileValues[i])){
				retValue.pos = i;
				retValue.smileValue = init.mSmileValues[i];
				return retValue;
			}
		}
		
		return null;
	}
	
	public static void clear() {
		instance = null;
	}
	
	public static String[] getSmile(Context context){
		return SmileArray.getInstance(context).mSmileValues;
	}
	
	private static String[] smileWithBracket = null;
	
	public static String[] getSmileWithBracket(Context context){
		if(smileWithBracket == null){
			String[] smiles = getSmile(context);
			smileWithBracket = new String[smiles.length];
			int cnt = smiles.length;
			for(int i = 0; i < cnt; i++){
				smileWithBracket[i] = "[" + smiles[i] + "]";
			}
		}
		
		return smileWithBracket;
	}
	
	public static class SmileValue{
		public int pos = 0;
		public String smileValue = null;

		SmileValue() {
		}
	}
}
