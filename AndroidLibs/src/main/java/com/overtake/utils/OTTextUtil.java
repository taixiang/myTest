package com.overtake.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class OTTextUtil {
	public interface IGetText<T> {
		public String getText(T obj);
	}

	private static int currentYear = -1;
	private static int currentDay = -1;

	public static String formatTimestamp(long seconds) {
		if (currentYear == -1) {
			Calendar currentTime = Calendar.getInstance();
			currentYear = currentTime.get(Calendar.YEAR);
			currentDay = currentTime.get(Calendar.DAY_OF_YEAR);
		}

		Calendar targetTime = Calendar.getInstance();
		targetTime.setTimeInMillis(seconds * 1000);
		targetTime.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		int targetYear = targetTime.get(Calendar.YEAR);
		int targetDay = targetTime.get(Calendar.DAY_OF_YEAR);

		String format;
		if (targetYear != currentYear) {
			format = "yyyy-MM-dd HH:mm";
		} else if (targetDay != currentDay) {
			format = "MM-dd HH:mm";
		} else {
			format = "HH:mm";
		}

		Date date = targetTime.getTime();
		SimpleDateFormat dateformat = new SimpleDateFormat(format, Locale.CHINA);
		String timeLabelStr = dateformat.format(date);
		return timeLabelStr;
	}
	
	public static int getCurrentDayOfWeekFromTimestamp(){
		Calendar currentTime = Calendar.getInstance();
		return currentTime.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getCurrentDayOfMonthFromTimestamp(){
		Calendar currentTime = Calendar.getInstance();
		return currentTime.get(Calendar.DAY_OF_MONTH);
	}
	
	public static String getCurrentTime(){
		Calendar currentTime = Calendar.getInstance();
		SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm", Locale.CHINA);
		String timeLableStr = dateformat.format(currentTime.getTime());
		
		return timeLableStr;
	}

    public static int getDaysBetween(long last, long current){
        long between_days=(current-last)/(1000*3600*24);
        return (int)between_days;
    }

	/**
	 * Returns a string containing the tokens joined by delimiters.
	 * 
	 * @param <T>
	 * @param tokens
	 *            an array objects to be joined. Strings will be formed from the
	 *            objects by calling object.toString().
	 */
	public static <T> void join(StringBuilder sb, CharSequence delimiter, T[] tokens, IGetText<T> util) {
		boolean firstTime = true;
		for (T token : tokens) {
			if (firstTime) {
				firstTime = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(util.getText(token));
		}
	}

	public static <T> void join(StringBuilder sb, CharSequence delimiter, List<T> tokens, IGetText<T> util) {
		boolean firstTime = true;
		for (T token : tokens) {
			if (firstTime) {
				firstTime = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(util.getText(token));
		}
	}

	public static String join(String[] input, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (String value : input) {
			sb.append(value);
			sb.append(delimiter);
		}
		int length = sb.length();
		if (length > 0) {
			sb.setLength(length - delimiter.length());
		}
		return sb.toString();
	}

	public static Date parseDate(String timeStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.CHINA);
		Date birthDate = null;
		try {
			birthDate = dateFormat.parse(timeStr);
		} catch (Exception e) {
		}
		return birthDate;
	}

	public static String getAgeString(String dateStr) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		Date birthDate = null;
		try {
			birthDate = dateFormat.parse(dateStr);
		} catch (Exception e) {
			return "";
		}

		Date now = new Date();
		String description = "";
		Long span = (now.getTime() - birthDate.getTime()) / 1000;

		int year = (int) (span / (86400 * 365));
		int seconds = (int) (span % (86400 * 365));
		if (year > 0) {
			description += String.format(seconds <= 86400 ? "%d岁整" : "%d岁", year);
		}
		int month = seconds / (86400 * 30);
		seconds = seconds % (86400 * 30);
		int day = seconds / (86400);
		if (month > 0) {
			description += month + "个月";
		}
		if (day > 0) {
			description += day + "天";
		}
		return description;
	}

	public static String getAgeStringAtDate(String birthDate, String atDate) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date birth = null;
		Date at = null;
		try {
			birth = dateFormat.parse(birthDate);
			at = dateFormat.parse(atDate);
		} catch (Exception e) {
			return "";
		}

		String description = "";
		Long span = (at.getTime() - birth.getTime()) / 1000;

		int year = (int) (span / (86400 * 365));
		int seconds = (int) (span % (86400 * 365));
		if (year > 0) {
			description += String.format(seconds <= 86400 ? "%d岁整" : "%d岁", year);
		}
		int month = seconds / (86400 * 30);
		seconds = seconds % (86400 * 30);
		int day = seconds / (86400);
		if (month > 0) {
			description += month + "个月";
		}
		if (day > 0) {
			description += day + "天";
		}

		if (span < 0) {
			description = "还没出生";
		} else if (year == 0 && month == 0 && day == 0) {
			description = "刚刚出生";
		}

		return description;
	}
}
