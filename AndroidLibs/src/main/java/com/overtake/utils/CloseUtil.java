package com.overtake.utils;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

import android.database.Cursor;
import android.os.Build;

import com.overtake.utils.OTLog;

public class CloseUtil {
	private final static String TAG = "CloseUtil";

	public static void close(Closeable closeable) {
		try {
			if (null != closeable) {
				closeable.close();
			}
		} catch (IOException e) {
			OTLog.e(TAG, "close");
		}
	}

	@SuppressWarnings("deprecation")
	public static void close(Cursor cursor) {
		if (null != cursor && Integer.parseInt(Build.VERSION.SDK) < 14) {
			cursor.close();
		}
	}

	public static void close(HttpURLConnection connection) {
		try {
			close(connection.getInputStream());
		} catch (Exception e) {
			OTLog.e(TAG, "close");
		}

		try {
			close(connection.getOutputStream());
		} catch (java.net.ProtocolException e) {
		} catch (Exception e) {
			OTLog.e(TAG, "close");
		}
	}
}
