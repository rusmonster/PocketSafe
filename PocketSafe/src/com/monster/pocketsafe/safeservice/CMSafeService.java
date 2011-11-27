package com.monster.pocketsafe.safeservice;

import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.main.TTypEvent;
import com.monster.pocketsafe.main.TTypEventStrings;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
		mMain.Open(this);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent==null) return START_STICKY;
		
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
}
