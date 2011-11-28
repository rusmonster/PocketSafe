package com.monster.pocketsafe.sms.sender;

import com.monster.pocketsafe.utils.MyException;

import android.content.Context;

public interface IMSmsSender {
	void SetObserver(IMSmsSenderObserver observer);
	void SetContext(Context context);
	void open() throws MyException;
	void sendSms(String phone, String text) throws MyException;
	void close();
}
