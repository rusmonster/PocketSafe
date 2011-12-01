package com.monster.pocketsafe;

import android.util.Log;

import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class ErrorDisplayer {
	public static String getErrStr(int err) {
		return "Error: "+err;
	}
	public static void displayError(int err) {
		Log.e("!!!", "ERROR for display: "+TTypMyException.from(err));
	}
}
