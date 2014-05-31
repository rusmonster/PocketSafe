package com.softmo.smssafe2.bootreceiver;

import com.softmo.smssafe2.safeservice.CMSafeService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CMBootReceiver extends BroadcastReceiver {
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent!=null && intent.getAction()!=null && ACTION.compareToIgnoreCase(intent.getAction())==0) {
				
				Intent stIntent = new Intent(context, CMSafeService.class);
				context.startService(stIntent);
	
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in CMBootReceiver.onReceive: ");
			e.printStackTrace();
		}
	}

};

