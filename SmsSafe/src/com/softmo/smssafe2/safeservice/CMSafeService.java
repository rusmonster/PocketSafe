package com.softmo.smssafe2.safeservice;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.main.TTypEvent;
import com.softmo.smssafe2.main.TTypEventStrings;
import com.softmo.smssafe2.utils.CMLocator;
import com.softmo.smssafe2.utils.IMLocator;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.views.SmsNewActivity;

public class CMSafeService extends Service {

	private final IBinder binder = new MyBinder();
	
	private final IMLocator mLocator = new CMLocator();
	private IMMain mMain;
	

    public class MyBinder extends Binder{
    	public IMMain getMain(){
	        return mMain;
	    }
    }



	@Override
	public IBinder onBind(Intent intent) {
		Log.d("!!!", "service Binded");
		return binder;
	}


	@Override
	public void onRebind(Intent intent) {
		Log.d("!!!", "service Rebinded");
		super.onRebind(intent);
	}


	@Override
	public boolean onUnbind(Intent intent) {
		Log.d("!!!", "service Unbinded");
		return super.onUnbind(intent);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		mMain = mLocator.createMain();
		try {
			mMain.Open(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		Log.d("!!!", "Service created");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("!!!", "onStartCommand");
		if (intent==null) return START_STICKY;

		if (respondViaMessage(intent)) {
			return START_STICKY;
		}
		
		int typ = intent.getIntExtra(TTypEventStrings.EVENT_TYP, -1);
		if (typ == -1) return START_STICKY;
			
		TTypEvent evt = TTypEvent.from(typ);
		switch (evt) {
		case ESmsRecieved:
			int id = intent.getIntExtra(TTypEventStrings.EVENT_ID, -1);
			mMain.handleSmsRecieved(id);
			break;
		}

		return START_STICKY;//super.onStartCommand(intent, flags, startId);
	}

	private boolean respondViaMessage(Intent intent) {
		try {
			if (!TelephonyManager.ACTION_RESPOND_VIA_MESSAGE.equals(intent.getAction())) {
				return false;
			}
			Log.d("!!!", "respondViaMessage");

			String message = intent.getStringExtra(Intent.EXTRA_TEXT);

			Uri intentUri = intent.getData();
			String recipients = intentUri.getSchemeSpecificPart();
			int pos = recipients.indexOf('?');
			recipients = (pos == -1) ? recipients : recipients.substring(0, pos);

			boolean showUi = intent.getBooleanExtra("showUI", false);
			Log.d("!!!", "respondViaMessage showUI: " + showUi);
			if (showUi) {
				Intent i = new Intent(this, SmsNewActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setAction(Intent.ACTION_SEND);
				i.putExtra(Intent.EXTRA_TEXT, message);
				i.setData(intentUri);
				startActivity(i);
			} else {
				String[] dests = TextUtils.split(recipients, ";");
				String to = dests[0];

				if (TextUtils.isEmpty(message) || TextUtils.isEmpty(to)) {
					return false;
				}

				mMain.SendSms(to, message);
			}
		} catch (Exception e) {
			Log.e("!!!", "Error in respondViaMessage: ", e);
			return false;
		}

		return true;
	}


	@Override
	public void onDestroy() {
		mMain.Close();
		Log.d("!!!", "Service destroyed");
		super.onDestroy();
	}
}
