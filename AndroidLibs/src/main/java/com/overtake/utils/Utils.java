package com.overtake.utils;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

@SuppressLint("SimpleDateFormat")
@SuppressWarnings("deprecation")
public final class Utils {

	public static boolean isNullOrEmpty(String s) {

		return s == null || s.length() == 0;
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float corner) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xFF424242;
		final Paint paint = new Paint();

		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, corner, corner, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Long getRandom() {
		Random random = new Random(System.currentTimeMillis());

		return random.nextLong();
	}

	public static Long getCurrentTimeInSecond() {
		Long millis = System.currentTimeMillis();

		return millis / 1000;
	}

	public static String getDateString(long timestampMillis) {
		String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(timestampMillis));

		return date;
	}

	/**
	 * 从url query中取得key=>value http://baby.com?key=value key1=value1&key2=value2
	 * 
	 * @param url
	 * @return
	 */
	public static HashMap<String, String> parseQuery(String url) {
		int idx = url.indexOf("?");
		if (idx != -1) {
			url = url.substring(idx + 1);

			url = URLDecoder.decode(url);
		}
		String keyValue[] = url.split("&");

		HashMap<String, String> hashMap = new HashMap<String, String>();
		for (String kvStr : keyValue) {
			String kv[] = kvStr.split("=");
			if (kv.length == 2) {
				hashMap.put(kv[0], URLDecoder.decode(kv[1]));
			}
		}

		return hashMap;
	}
}
