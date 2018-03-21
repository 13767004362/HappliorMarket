package com.zhongke.market.util;

import android.util.Log;

/**
 * Log
 * 
 * @author lilijun
 * 
 */
public class DLog
{
	private static String TAG = "DLog";

	public static boolean DEBUG = true;

	/** 日志保存路径*/
	public static String logDirPath = "";

	public static void d(String tag, String msg){
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void d(String msg){
		if (DEBUG) {
			Log.d(TAG, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr){
		if (DEBUG) {
			Log.d(tag,msg,tr);
		}
	}

	public static void i(String tag, String msg){
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static void i(String msg){
		if (DEBUG) {
			Log.i(TAG, msg);
		}
	}

	public static void v(String tag, String msg){
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void v(String msg){
		if (DEBUG) {
			Log.v(TAG, msg);
		}
	}

	public static void w(String tag, String msg){
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static void w(String msg){
		if (DEBUG) {
			Log.v(TAG, msg);
		}
	}

	public static void e(String tag, String error){
		if (DEBUG) {
			Log.e(tag, error);
		}
	}

	public static void e(String error){
		if (DEBUG) {
			Log.e(TAG, error);
		}
	}
	public static void e(String error, Throwable tr){
		if (DEBUG) {
			Log.e(TAG, error,tr);
		}
	}

	public static void e(String tag, String msg, Throwable tr){
		if (DEBUG) {
			Log.e(tag,msg,tr);
		}
	}
}
