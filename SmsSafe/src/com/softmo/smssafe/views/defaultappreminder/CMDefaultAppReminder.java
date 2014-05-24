package com.softmo.smssafe.views.defaultappreminder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;

public class CMDefaultAppReminder implements IMDefaultAppReminder {
	private static IMDefaultAppReminder sInstance;

	private boolean sNeedRemind = true;

	public static IMDefaultAppReminder getInstance() {
		if (sInstance != null) {
			return sInstance;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			sInstance = new CMDefaultAppReminder();
		} else {
			sInstance = new IMDefaultAppReminder() {
				@Override
				public boolean showIfNeeded(Context context) {
					return false;
				}
			};
		}

		return sInstance;
	}

	private CMDefaultAppReminder() {
		//do nothing
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public boolean showIfNeeded(final Context context) {
		if (!sNeedRemind) {
			return false;
		}
		sNeedRemind = false;

		final String myPackageName = context.getPackageName();
		if (Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {
			return false;
		}

		Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
		intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
		context.startActivity(intent);

		return true;
	}
}
