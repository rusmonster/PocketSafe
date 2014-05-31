package com.softmo.smssafe2.views.defaultappreminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;

public class CMDefaultAppReminder implements IMDefaultAppReminder {
	public static final int REQUEST_CODE = 2001;

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
				public boolean showIfNeeded(Activity activity) {
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
	public boolean showIfNeeded(final Activity activity) {
		if (!sNeedRemind) {
			return false;
		}
		sNeedRemind = false;

		final String myPackageName = activity.getPackageName();
		if (Telephony.Sms.getDefaultSmsPackage(activity).equals(myPackageName)) {
			return false;
		}

		Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
		intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
		activity.startActivityForResult(intent, REQUEST_CODE);

		return true;
	}
}
