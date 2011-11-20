package com.monster.pocketsafe.smsreciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class CMSmsMonitor extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (intent!=null && intent.getAction()!=null && ACTION.compareToIgnoreCase(intent.getAction())==0) {
			
			Object pduArray[] = (Object[]) intent.getExtras().get("pdus");
			
			SmsMessage messages[] = new SmsMessage[pduArray.length];
			
			String smstext = new String();
			String smsfrom = new String();
		
			for (int i = 0; i<pduArray.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[])pduArray [i]);
				smstext += messages[i].getMessageBody();
			}
			
			if (messages.length>0)
				smsfrom = messages[0].getOriginatingAddress();
			
			
			Log.d("!!!","SMS Message from: "+smsfrom+"; text: "+smstext);
			if (isOrderedBroadcast()) {
				Log.d("!!!","isOrderedBroadcast() == true");
				//abortBroadcast();
			}
			else
				Log.d("!!!","isOrderedBroadcast() == false");
			
		}
		
	}
}
