package com.overtake.utils;

import android.util.Log;

public class ProfileTool {

	private static long mLast = 0;

	public static void profile(String label) {
		long curr = System.currentTimeMillis();
		long last = mLast;
		mLast = curr;
		if (label == null) {
			Log.i("ProfileTool", " Profile start");
			return;
		}
		Log.i("ProfileTool", label + " Time elapsed: " + (curr - last) + " ms");
	}
}
