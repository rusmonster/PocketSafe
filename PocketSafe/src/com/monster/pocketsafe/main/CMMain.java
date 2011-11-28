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
	private IMSms mSendingSms;

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



	public void SendSms(IMSms sms) throws MyException {
		
		sms.setDate( new Date() );
		sms.setDirection( TTypDirection.EOutgoing );
		sms.setFolder( TTypFolder.EOutbox );
		sms.setIsNew(TTypIsNew.EReaded);
		
		int id = mDbEngine.TableSms().Insert(sms);
		sms.setId(id);
		
		IMEventSimpleID insertEv = mLocator.createEventSimpleID();
		insertEv.setTyp(TTypEvent.ESmsOutboxAdded);
		mDispatcher.pushEvent(insertEv);
		
		mSmsSender.sendSms(sms.getPhone(), sms.getText());
		mSendingSms = sms;
		
		IMEvent ev = mLocator.createEvent();
		ev.setTyp(TTypEvent.ESmsSendStart);
		mDispatcher.pushEvent(ev);
	}



	public void handleSmsRecieved(int id) {
		NotificationManager NotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.android_happy;
        CharSequence tickerText ="New sms message"; //mContext.getResources().getText( R.string.new_sms );
      
        long when = System.currentTimeMillis();
        Context context = mContext;//.getApplicationContext();  
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



	public void SmsSenderSent(CMSmsSender sender) {
		mSendingSms.setFolder( TTypFolder.ESent );
		
		
		try {
			mDbEngine.TableSms().Update(mSendingSms);
		} catch (MyException e) {
			IMEventSimpleID errEv = mLocator.createEventSimpleID();
			errEv.setTyp(TTypEvent.EErrMyException);
			errEv.setId(e.getId().Value);
			mDispatcher.pushEvent(errEv);
		}
		
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSent);
		ev.setId(mSendingSms.getId());
		mDispatcher.pushEvent(ev);
		mSendingSms = null;
		
	}



	public void SmsSenderSentError(CMSmsSender sender, int err) {
		//TODO
		IMEventSimpleID ev = mLocator.createEventSimpleID();
		ev.setTyp(TTypEvent.ESmsSendError);
		ev.setId(mSendingSms.getId());
		mDispatcher.pushEvent(ev);
		mSendingSms = null;
	}



	public void SmsSenderDelivered(CMSmsSender sender) {
		// TODO Auto-generated method stub
		
	}



	public void SmsSenderDeliverError(CMSmsSender sender, int err) {
		// TODO Auto-generated method stub
		
	}


}
