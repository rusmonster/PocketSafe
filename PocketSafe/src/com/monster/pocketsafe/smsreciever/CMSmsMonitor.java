package com.monster.pocketsafe.smsreciever;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.main.TTypEvent;
import com.monster.pocketsafe.main.TTypEventStrings;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.sec.IMRsa;
import com.monster.pocketsafe.sec.IMSha256;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
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
				
				
				Log.d("!!!","SMS Message from: "+smsfrom+"; text: "+smstext);
				
				mDbEngine.Open( context.getContentResolver() );

				if (ProcessMessage(context, smsfrom, smstext)) {
					Log.d("!!!", "SMS stored");
					writeFile(context, smsfrom+": "+smstext);
					abortBroadcast();
				}
	
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in CMSmsMonitor.onReceive");
			e.printStackTrace();
		}
	}

	private static void writeFile(Context context, String text) {
		try {
			File fileName = null;
			String sdState = android.os.Environment.getExternalStorageState();
			if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			    File sdDir = android.os.Environment.getExternalStorageDirectory();
			    fileName = new File(sdDir, "sms.txt");
		
				if (!sdDir.exists())
					sdDir.mkdirs();
				
			    FileWriter f = new FileWriter(fileName, true);
			    Date dat = new Date();
			    String str = new String("["+dat.toString()+"] "+text+"\n\n");
			    f.write(str);
			    f.flush();
			    f.close();
			    Log.d("!!!", "writeFile ok: "+str);
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in writeFile: "+e.getMessage());
		}
	}
};

