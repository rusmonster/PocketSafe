package com.softmo.smssafe.main;

import android.content.Context;

import com.softmo.smssafe.dbengine.IMDbReader;
import com.softmo.smssafe.utils.MyException;

public interface IMMain {
	
	enum TMainState {
		EIdle,
		EImport
	}
	
	TMainState getState();
	IMDbReader DbReader();
	IMDbWriter DbWriter();
	
	IMDispatcher Dispatcher();
	
	void Open(Context context) throws MyException;
	void SendSms(String phone, String text) throws MyException;
	void handleSmsRecieved(int smsId);
	void Close();
	void changePass(String oldPass, String newPass) throws MyException;
	void enterPass(String _pass) throws MyException;
	String decryptString(String data) throws MyException;
	boolean isPassValid();
	void lockNow();
	void ResendSms(int id) throws MyException;
	void guiPause();
	void guiResume();
	void importSms() throws MyException;

}
