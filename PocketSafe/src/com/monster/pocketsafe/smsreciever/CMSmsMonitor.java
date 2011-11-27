package com.monster.pocketsafe.smsreciever;

import java.util.Date;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.safeservice.IMSafeService;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;

public class CMSmsMonitor extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private IMSafeService myService=null;
	
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
			
			IMLocator locator  = new CMLocator();
			IMDbEngine db = locator.createDbEngine();
			db.Open( context.getContentResolver() );
			try {
				IMSms sms = locator.createSms();
				sms.setDirection(TTypDirection.EIncoming);
				sms.setFolder(TTypFolder.Einbox);
				sms.setIsNew(TTypIsNew.EJustRecv);
				sms.setPhone(smsfrom);
				sms.setText(smstext);
				sms.setDate( new Date() );
				
				db.TableSms().Insert(sms);
			Log.d("!!!", "SMS stored");
			} catch (MyException e) {
				e.printStackTrace();
			}

			/*
	        Intent a = new Intent(context, CMSafeService.class);
	        context.startService(a);
	        // Binding ..this block can also start service if not started already
	        //Intent bindIntent = new Intent(context, CMSafeService.class);
	        //context.bindService(bindIntent, serviceConncetion, Context.BIND_AUTO_CREATE);
	        
	        if (myService==null) {
	        	Log.d("!!!", "myService is null");
	        	return;
	        }
	        
	        try {
				if (myService.handleSms(smsfrom, smstext)) {
					abortBroadcast();
				}
			} catch (MyException e) {
				e.printStackTrace();
			}
*/
			
			/*
			if (isOrderedBroadcast()) {
				Log.d("!!!","isOrderedBroadcast() == true");
				//abortBroadcast();
			}
			else
				Log.d("!!!","isOrderedBroadcast() == false");
			*/
		}
		
	}
	
    private ServiceConnection serviceConncetion = new ServiceConnection() {

    	public void onServiceConnected(ComponentName name, IBinder service) {
    		myService = ((CMSafeService.MyBinder)service).getService();
    	}

	    public void onServiceDisconnected(ComponentName name) {
	        myService = null;
	    }

};

}
