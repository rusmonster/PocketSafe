package com.softmo.libsafe.main;

import android.content.Context;

import com.softmo.libsafe.dbengine.IMDbReader;
import com.softmo.libsafe.utils.MyException;

public interface IMMain {
	
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

}
