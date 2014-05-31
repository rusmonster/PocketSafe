package com.softmo.smssafe2.smsreceiver;

import java.util.Date;

import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.dbengine.IMSetting;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.TTypDirection;
import com.softmo.smssafe2.dbengine.TTypFolder;
import com.softmo.smssafe2.dbengine.TTypIsNew;
import com.softmo.smssafe2.dbengine.TTypStatus;
import com.softmo.smssafe2.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe2.main.TTypEvent;
import com.softmo.smssafe2.main.TTypEventStrings;
import com.softmo.smssafe2.safeservice.CMSafeService;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.sec.IMSha256;
import com.softmo.smssafe2.utils.CMLocator;
import com.softmo.smssafe2.utils.IMLocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

public class CMSmsMonitor extends BroadcastReceiver {
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final IMLocator mLocator = new CMLocator();
	private static final IMDbEngine mDbEngine = mLocator.createDbEngine();
	private static final IMRsa mRsa = mLocator.createRsa();
	private static final IMSha256 mSha = mLocator.createSha256();
	
	private boolean ProcessMessage(Context context, String phone, String text) throws Exception {
		
		IMSetting set = mLocator.createSetting();
		mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPub);
		String pub = set.getStrVal();
		if (pub.length()==0)
			return false;
		
		mRsa.setPublicKey(pub);
		
		byte[] cPhone = mRsa.EncryptBuffer(phone.getBytes());
		byte[] cText = mRsa.EncryptBuffer(text.getBytes());
		
		IMSms sms = mLocator.createSms();
		sms.setDirection(TTypDirection.EIncoming);
		sms.setFolder(TTypFolder.EInbox);
		sms.setIsNew(TTypIsNew.EJustRecv);
		sms.setPhone(new String(cPhone, IMDbEngine.ENCODING));
		sms.setHash(mSha.getHash(phone));
		sms.setText(new String(cText, IMDbEngine.ENCODING));
		sms.setDate( new Date() );
		sms.setStatus(TTypStatus.ERecv);
		
		int id = mDbEngine.TableSms().Insert(sms);
		
		Intent intent = new Intent(context, CMSafeService.class);
		intent.putExtra(TTypEventStrings.EVENT_TYP, TTypEvent.ESmsRecieved.getValue());
		intent.putExtra(TTypEventStrings.EVENT_ID, id);
        context.startService(intent);
        
        return true;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
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
				
				
				mDbEngine.Open( context );

				if (ProcessMessage(context, smsfrom, smstext)) {
					Log.d("!!!", "SMS stored ");
					abortBroadcast();
				}
	
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in CMSmsMonitor.onReceive: ");
			e.printStackTrace();
		}
	}
};

