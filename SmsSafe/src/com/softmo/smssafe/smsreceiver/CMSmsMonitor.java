package com.softmo.smssafe.smsreceiver;

import java.util.Date;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.TTypDirection;
import com.softmo.smssafe.dbengine.TTypFolder;
import com.softmo.smssafe.dbengine.TTypIsNew;
import com.softmo.smssafe.dbengine.TTypStatus;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.main.TTypEvent;
import com.softmo.smssafe.main.TTypEventStrings;
import com.softmo.smssafe.safeservice.CMSafeService;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.sec.IMSha256;
import com.softmo.smssafe.utils.CMLocator;
import com.softmo.smssafe.utils.IMLocator;

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
					deleteMessageFromAndroidDb(context, messages);
				}
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in CMSmsMonitor.onReceive: ");
			e.printStackTrace();
		}
	}

	private void deleteMessageFromAndroidDb(Context context, SmsMessage[] messages) {
		if (messages.length == 0) {
			return;
		}

		SmsMessage message = messages[0];
		Log.d("!!!", "getIndexOnIcc: " + message.getIndexOnIcc());
		Log.d("!!!", "getIndexOnSim: " + message.getIndexOnSim());
		Log.d("!!!", "getTimestampMillis: " + message.getTimestampMillis());

		ContentResolver cr = context.getContentResolver();
		StringBuilder where = new StringBuilder();
		where.append("date_sent=" + message.getTimestampMillis());
		where.append(" and address='" + message.getOriginatingAddress() + "'");
		where.append(" and type=1");

		String selection = where.toString();
		Cursor cursor = cr.query(Uri.parse("content://sms"), null ,null, null, "date DESC LIMIT 10");

		if (!cursor.moveToFirst())
			return;

		StringBuilder sb = new StringBuilder();

		String[] names = cursor.getColumnNames();
		do {
			for (String name : names) {
				int col = cursor.getColumnIndex(name);
				String val  = cursor.getString(col);

				sb.append(name);
				sb.append(": ");
				sb.append(val);
				sb.append("; ");
			}

			sb.append('\n');
		} while (cursor.moveToNext());
		cursor.close();

		Log.d("!!!", "LAST 10:\n" + sb.toString());

		Log.d("!!!", "selection: " + selection);
		cursor = cr.query(Uri.parse("content://sms"), null ,selection, null, "date DESC LIMIT 10");

		if (!cursor.moveToFirst())
			return;

		sb = new StringBuilder();

		names = cursor.getColumnNames();
		do {
			for (String name : names) {
				int col = cursor.getColumnIndex(name);
				String val  = cursor.getString(col);

				sb.append(name);
				sb.append(": ");
				sb.append(val);
				sb.append("; ");
			}

			sb.append('\n');
		} while (cursor.moveToNext());
		cursor.close();

		Log.d("!!!", "before delete");
		cr.delete(Uri.parse("content://sms"), selection, null);
		Log.d("!!!", "after delete");
	}
};

