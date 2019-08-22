package com.zzt.panorama.util;

import android.util.Log;

import com.zzt.panorama.BuildConfig;


public class LogHelper {
	private static boolean SHOW_CLICKABLE_TAGS = true;

	public static void v(String tag, Object msg) {
		if (BuildConfig.DEBUG) {
			Log.v(getTag(tag), msg == null ? "null" : msg.toString());
		}
	}

	public static void d(String tag, Object msg) {
		if (BuildConfig.DEBUG) {
			Log.d(getTag(tag), msg == null ? "null" : msg.toString());
		}
	}

	public static void i(String tag, Object msg) {
		if (BuildConfig.DEBUG) {
			Log.i(getTag(tag), msg == null ? "null" : msg.toString());
		}
	}

	public static void w(String tag, Object msg) {
		if (BuildConfig.DEBUG) {
			Log.w(getTag(tag), msg == null ? "null" : msg.toString());
		}
	}

	public static void e(String tag, Object msg) {
		if (BuildConfig.DEBUG) {
			Log.e(getTag(tag), msg == null ? "null" : msg.toString());
		}
	}

	public static void e(String tag, Throwable ex) {
		if (BuildConfig.DEBUG) {
			Log.e(getTag(tag), "", ex);
		}
	}

	public static void e(String tag, Object msg, Throwable ex) {
		if (BuildConfig.DEBUG) {
			Log.e(getTag(tag), msg == null ? "null" : msg.toString(), ex);
		}
	}

	private static String getTag(String original_tag) {
		if (SHOW_CLICKABLE_TAGS) {
			final StackTraceElement stackTrace = new Exception().getStackTrace()[2];
			return "(" + original_tag + ".java:" + stackTrace.getLineNumber() + ")";
		} else {
			return original_tag;
		}
	}
}
