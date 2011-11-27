package com.monster.pocketsafe.safeservice;

import java.util.Date;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CMSafeService extends Service implements IMSafeService {
	
	private final IMLocator mLocator = new CMLocator();
	private final IMDbEngine mDbEngine = mLocator.createDbEngine();
	private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder{
    	public IMSafeService getService(){
	        return CMSafeService.this;
	    }
    }



	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}



	public boolean handleSms(String from, String text) throws MyException {
		IMSms sms = mLocator.createSms();
		sms.setDirection(TTypDirection.EIncoming);
		sms.setFolder(TTypFolder.Einbox);
		sms.setIsNew(TTypIsNew.EJustRecv);
		sms.setPhone(from);
		sms.setText(text);
		sms.setDate( new Date() );
		
		mDbEngine.TableSms().Insert(sms);
		Log.d("!!!", "SMS stored by service");
		
		return false;
	}



	@Override
	public void onCreate() {
		super.onCreate();
		mDbEngine.Open(getContentResolver());
	}

}
