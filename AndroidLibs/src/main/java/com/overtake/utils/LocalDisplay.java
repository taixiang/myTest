package com.overtake.utils;

import com.overtake.utils.OTLog;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class LocalDisplay {
	public static int SCREEN_WIDTH_PIXELS;
	public static int SCREEN_HEIGHT_PIXELS;
	public static float SCREEN_DENSITY;
	public static int SCREEN_WIDTH_DP;
	public static int SCREEN_HEIGHT_DP;

	public static void init(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		
		SCREEN_WIDTH_PIXELS = dm.widthPixels;
		SCREEN_HEIGHT_PIXELS = dm.heightPixels;
		SCREEN_DENSITY = dm.density;
		SCREEN_WIDTH_DP = (int) (SCREEN_WIDTH_PIXELS / dm.density);
		SCREEN_HEIGHT_DP = (int) (SCREEN_HEIGHT_PIXELS / dm.density);

		OTLog.i("display", String.format("SCREEN PIXELS %s %s", SCREEN_WIDTH_PIXELS, SCREEN_HEIGHT_PIXELS));
		OTLog.i("display", String.format("SCREEN DP %s %s", SCREEN_WIDTH_DP, SCREEN_HEIGHT_DP));
		OTLog.i("display", String.format("density %s", dm.density));
	}

	public static int getScaledWidthDPByDP(int desingDP) {
		double v = desingDP / 320f * SCREEN_WIDTH_DP;
		return (int) v;
	}

	public static int getScaledWidthPixelsByDP(int desingDP) {
		double v = desingDP * SCREEN_DENSITY;
		return (int) v;
	}

	public static int getScaledWidthPixelsByDesignDP(int designDP) {
		double v = getScaledWidthPixelsByDP(getScaledWidthDPByDP(designDP));
		return (int) v;
	}
}
