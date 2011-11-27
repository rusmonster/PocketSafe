package com.monster.pocketsafe.main;

import android.content.Context;

import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSms;

public interface IMMain {
	
	IMDbReader DbReader();
	IMDbWriter DbWriter();
	
	IMDispatcher Dispatcher();
	
	void Open(Context context);
	void SendSms(IMSms sms);
	void handleSmsRecieved(int id);

}
