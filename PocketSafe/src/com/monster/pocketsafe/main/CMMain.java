package com.monster.pocketsafe.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.monster.pocketsafe.R;
import com.monster.pocketsafe.SmsMainActivity;
import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

public class CMMain implements IMMain {
	
	private Context mContext;
	private IMLocator mLocator;
	private IMDbEngine mDbEngine;
	private IMDbWriterInternal mDbWriter;
	private IMDispatcherSender mDispatcher;

	public CMMain(IMLocator locator) {
		super();
		mLocator = locator;
		mDbEngine = mLocator.createDbEngine();
		mDispatcher = mLocator.createDispatcher();
		
		mDbWriter = mLocator.createDbWriter();
		mDbWriter.SetDbEngine(mDbEngine);
		mDbWriter.SetDispatcher(mDispatcher);
	}
	


	public void Open(Context context) {
		mContext = context;
		mDbEngine.Open(mContext.getContentResolver());
	}
	
	public IMDbReader DbReader() {
		return mDbEngine;
	}
	
	public IMDbWriter DbWriter() {
		return mDbWriter;
	}



	public void SendSms(IMSms sms) {
		// TODO Auto-generated method stub
		
	}



	public void handleSmsRecieved(int id) {
		NotificationManager NotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.android_happy;
        CharSequence tickerText ="New sms message"; //mContext.getResources().getText( R.string.new_sms );
      
        long when = System.currentTimeMillis();
        Context context = mContext.getApplicationContext();  
        CharSequence contentTitle ="New sms message";// mContext.getResources().getText( R.string.new_sms );;
        CharSequence contentText = "";
    
        Intent notificationIntent = new Intent(context, SmsMainActivity.class);
        notificationIntent.putExtra(TTypEventStrings.EVENT_TYP, TTypEvent.ESmsRecieved.Value);
        notificationIntent.putExtra(TTypEventStrings.EVENT_ID, id);
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            
        Notification notification = new Notification(icon, tickerText, when);
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            
        NotifyMgr.notify(TTypEvent.ESmsRecieved.Value, notification);
        
        try {
        	IMSms sms = mLocator.createSms();
			mDbEngine.TableSms().getById(sms, id);
			sms.setIsNew(TTypIsNew.Enew);
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
			e.printStackTrace();
		}
        
        IMEventSimpleID ev = mLocator.createEventSimpleID();
        ev.setTyp(TTypEvent.ESmsRecieved);
        ev.setId(id);
        mDispatcher.pushEvent(ev);
	}



	public IMDispatcher Dispatcher() {
		return mDispatcher;
	}


}
