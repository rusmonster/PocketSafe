package com.monster.pocketsafe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class ErrorDisplayer {
	public static String getErrStr(int err) {
		TTypMyException e = TTypMyException.from(err);
		Log.e("!!!", "ERROR for display: "+e);
		return "Error: "+e;
	}
	public static void displayError(Context context, int err) {
		
		String errstr = getErrStr(err);
		Toast.makeText(context, errstr, Toast.LENGTH_SHORT).show();
	}
}
