package com.overtake.utils;

import android.util.Log;

public final class OTLog {

	public static boolean DEBUG = true;

	public static void i(Object o, String msg) {
		if (DEBUG) {
			Log.i(o.getClass().getSimpleName(), msg);
		}
	}

	public static void e(Object o, String msg) {
		if (DEBUG) {
			Log.e(o.getClass().getSimpleName(), msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			Log.e(tag, msg, tr);
		}
	}

	public static void w(Object o, String msg) {

		if (DEBUG) {
			Log.w(o.getClass().getSimpleName(), msg);
		}
	}

	public static void i(String tag, String msg) {

		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void i(String tag, Object obj) {

		if (DEBUG) {
			Log.i(tag, obj.toString());
		}
	}

	public static void e(String tag, String msg) {

		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}
}
