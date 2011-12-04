package com.monster.pocketsafe.main;

import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.main.notificator.IMSmsNotificator;
import com.monster.pocketsafe.sms.sender.CMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSenderObserver;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMMain implements IMMain, IMSmsSenderObserver, IMListener {
	
	private Context mContext;
	private IMLocator mLocator;
	private IMDbEngine mDbEngine;
	private IMDbWriterInternal mDbWriter;
	private IMDispatcherSender mDispatcher;
	private IMSmsSender mSmsSender;
	private IMSmsNotificator mSmsNotificator;
	private Handler mHandler;
	
	private Runnable mRunUpdateNotificator = new Runnable() {
		public void run() {
			mSmsNotificator.Update( GetCountNewSms() );
		}
	};

	public CMMain(IMLocator locator) {
		super();
		mLocator = locator;
		mDbEngine = mLocator.createDbEngine();
		mDispatcher = mLocator.createDispatcher();
		
		mDbWriter = mLocator.createDbWriter();
		mDbWriter.SetDbEngine(mDbEngine);
		mDbWriter.SetDispatcher(mDispatcher);
		
		mSmsSender = mLocator.createSmsSender();
		
		mSmsNotificator = mLocator.createSmsNotificator();
		mHandler = new Handler();
    }

	public void Open(Context context) throws MyException {
		mContext = context;
		
		mDbEngine.Open(mContext.getContentResolver());
		
		mSmsSender.SetObserver(this);
		mSmsSender.SetContext(mContext);
		mSmsSender.open();
		
		mSmsNotificator.Init(mContext /*getApplicationContext(); */);
		
		mDispatcher.addListener(this);
	}
	
	public IMDbReader DbReader() {
		return mDbEngine;
	}
	
	public IMDbWriter DbWriter() {
		return mDbWriter;
	}

	public void SendSms(String phone, String text) throws MyException {
		
		if (phone==null || phone.length()==0)
			throw new MyException(TTypMyException.ESmsErrSendNoPhone);
		
		Log.d("!!!","SendSms phone: "+phone);
		
		if (!phone.matches("^[+]{0,1}[0-9]+$"))
			throw new MyException(TTypMyException.EErrPhoneFormat);
		
		if (text==null || text.length()==0)
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

	private void pushMyException(MyException e) {
		IMEventSimpleID ev = mLocator.createEventSimpleID();
        ev.setTyp(TTypEvent.EErrMyException);
        ev.setId(ev.getId());
        mDispatcher.pushEvent(ev);	
	}
	
	private int GetCountNewSms() {
		int new_cnt = -1;
		try {
			new_cnt = mDbEngine.TableSms().getCountNew();
		} catch (MyException e) {
			pushMyException(e);
	    }	
		return new_cnt;
	}
	
	public void handleSmsRecieved(int id) {
        try {
        	IMSms sms = mLocator.createSms();
			mDbEngine.TableSms().getById(sms, id);
			sms.setIsNew(TTypIsNew.ENew);
			mDbEngine.TableSms().Update(sms);
		} catch (MyException e) {
	        pushMyException(e);
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
			pushMyException(e);
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
			pushMyException(e);
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

	public void listenerEvent(IMEvent event) throws Exception {
		switch(event.getTyp()) {
		case ESmsRecieved:
			mSmsNotificator.Popup( GetCountNewSms() );
			break;
		case ESmsUpdated:
		case ESmsDelMany:
		case ESmsDeleted:
			mHandler.removeCallbacks(mRunUpdateNotificator);
			mHandler.postDelayed(mRunUpdateNotificator, 500);
			break;
		}
		
	}
}
