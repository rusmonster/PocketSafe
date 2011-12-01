package com.monster.pocketsafe.main;

import java.util.Date;

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
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.sms.sender.CMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSenderObserver;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMMain implements IMMain, IMSmsSenderObserver {
	
	private Context mContext;
	private IMLocator mLocator;
	private IMDbEngine mDbEngine;
	private IMDbWriterInternal mDbWriter;
	private IMDispatcherSender mDispatcher;
	private IMSmsSender mSmsSender;
	NotificationManager mNotifyMgr;
	Notification mNotification;

	public CMMain(IMLocator locator) {
		super();
		mLocator = locator;
		mDbEngine = mLocator.createDbEngine();
		mDispatcher = mLocator.createDispatcher();
		
		mDbWriter = mLocator.createDbWriter();
		mDbWriter.SetDbEngine(mDbEngine);
		mDbWriter.SetDispatcher(mDispatcher);
		
		mSmsSender = mLocator.createSmsSender();
	}
	


	public void Open(Context context) throws MyException {
		mContext = context;
		mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		mDbEngine.Open(mContext.getContentResolver());
		
		mSmsSender.SetObserver(this);
		mSmsSender.SetContext(mContext);
		mSmsSender.open();
	}
	
	public IMDbReader DbReader() {
		return mDbEngine;
	}
	
	public IMDbWriter DbWriter() {
		return mDbWriter;
	}



	public void SendSms(String phone, String text) throws MyException {
		
		if (phone.length()==0)
			throw new MyException(TTypMyException.ESmsErrSendNoPhone);
		
		if (text.length()==0)
			throw new MyException(TTypMyException.ESmsErrSendNoText);
		
		IMSms sms = mLocator.createSms();
		sms.setPhone(phone);
		sms.setText(text);
		sms.setDate( new Date() );
		sms.setDirection( TTypDirection.EOutgoing );
		sms.setFolder( TTypFolder.EOutbox );
		sms.setIsNew(TTypIsNew.ENew);
		
		int id = mDbEngine.TableSms().Insert(sms);
		sms.setId(id);
		
		mSmsSender.sendSms(sms.getPhone(), sms.getText(), sms.getId());
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSendStart);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);
		
		IMEventSimpleID insertEv = mLocator.createEventSimpleID();
		insertEv.setTyp(TTypEvent.ESmsOutboxAdded);
		insertEv.setId(sms.getId());
		mDispatcher.pushEvent(insertEv);
	}

	public void checkNewNotificator() {
		int new_cnt = -1;
		
		try {
			new_cnt = mDbEngine.TableSms().getCountNew();
		} catch (MyException e1) {
			e1.printStackTrace();
		}
		
		if (new_cnt==0) {
			mNotifyMgr.cancel(TTypEvent.ESmsRecieved.Value);
			mNotification=null;
		} else if (mNotification != null) {
			CharSequence new_sms = mContext.getResources().getText( R.string.sms_new );
			CharSequence contentText=mContext.getResources().getText( R.string.sms_new_cnt )+Integer.toString(new_cnt);
	        Intent notificationIntent = new Intent(mContext, SmsMainActivity.class);
	        notificationIntent.putExtra(TTypEventStrings.EVENT_TYP, TTypEvent.ESmsRecieved.Value);
	        notificationIntent.putExtra(TTypEventStrings.EVENT_ID, 0);
	        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0); 
	        mNotification.setLatestEventInfo(mContext, new_sms, contentText, contentIntent);
		}
	}

	public void handleSmsRecieved(int id) {
        int icon = R.drawable.android_happy;
        CharSequence new_sms = mContext.getResources().getText( R.string.sms_new );
        
        CharSequence tickerText =  new_sms;
      
        long when = System.currentTimeMillis();
        Context context = mContext;//.getApplicationContext();  
        CharSequence contentTitle = new_sms; 
        CharSequence contentText = "";
        
		try {
			int new_cnt = mDbEngine.TableSms().getCountNew();
        	contentText=mContext.getResources().getText( R.string.sms_new_cnt )+Integer.toString(new_cnt);
		} catch (MyException e1) {
			e1.printStackTrace();
		}
    
        Intent notificationIntent = new Intent(context, SmsMainActivity.class);
        notificationIntent.putExtra(TTypEventStrings.EVENT_TYP, TTypEvent.ESmsRecieved.Value);
        notificationIntent.putExtra(TTypEventStrings.EVENT_ID, id);
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            
        mNotification = new Notification(icon, tickerText, when);
        mNotification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        mNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
            
        mNotifyMgr.notify(TTypEvent.ESmsRecieved.Value, mNotification);
        
        
        try {
        	IMSms sms = mLocator.createSms();
			mDbEngine.TableSms().getById(sms, id);
			sms.setIsNew(TTypIsNew.ENew);
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
	        IMEventSimpleID ev = mLocator.createEventSimpleID();
	        ev.setTyp(TTypEvent.EErrMyException);
	        ev.setId(ev.getId());
	        mDispatcher.pushEvent(ev);
		}
        
        IMEventSimpleID ev = mLocator.createEventSimpleID();
        ev.setTyp(TTypEvent.ESmsRecieved);
        ev.setId(id);
        mDispatcher.pushEvent(ev);

	}



	public IMDispatcher Dispatcher() {
		return mDispatcher;
	}



	public void SmsSenderSent(CMSmsSender sender, int tag) {
		
		IMSms sms = mLocator.createSms();

		try {
			mDbEngine.TableSms().getById(sms, tag);
			sms.setFolder( TTypFolder.ESent );
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
			IMEventSimpleID errEv = mLocator.createEventSimpleID();
			errEv.setTyp(TTypEvent.EErrMyException);
			errEv.setId(e.getId().Value);
			mDispatcher.pushEvent(errEv);
		}
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSent);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);
		
	}



	public void SmsSenderSentError(CMSmsSender sender, int tag,  int err) {
		IMEventErr ev = mLocator.createEventErr();
		ev.setTyp(TTypEvent.ESmsSendError);
		ev.setId(tag);
		ev.setErr(err);
		mDispatcher.pushEvent(ev);
	}



	public void SmsSenderDelivered(CMSmsSender sender, int tag) {
		IMSms sms = mLocator.createSms();

		try {
			mDbEngine.TableSms().getById(sms, tag);
			sms.setIsNew( TTypIsNew.EOld );
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
			IMEventSimpleID errEv = mLocator.createEventSimpleID();
			errEv.setTyp(TTypEvent.EErrMyException);
			errEv.setId(e.getId().Value);
			mDispatcher.pushEvent(errEv);
		}
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsDelivered);
		ev.setId(sms.getId());
		mDispatcher.pushEvent(ev);		
	}



	public void SmsSenderDeliverError(CMSmsSender sender, int tag, int err) {
		IMEventErr ev = mLocator.createEventErr();
		ev.setTyp(TTypEvent.ESmsDeliverError);
		ev.setId(tag);
		ev.setErr(err);
		mDispatcher.pushEvent(ev);		
	}



	public void Close() {
		mSmsSender.close();
	}


}
