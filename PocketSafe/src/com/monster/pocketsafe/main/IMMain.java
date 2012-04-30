package com.monster.pocketsafe.main;

import android.content.Context;

import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.utils.MyException;

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

}
