package com.softmo.smssafe2.smssender;

import com.softmo.smssafe2.utils.MyException;

import android.content.Context;

public interface IMSmsSender {
	void SetObserver(IMSmsSenderObserver observer);
	void SetContext(Context context);
	void open() throws MyException;
	void sendSms(String phone, String text, int tag) throws MyException;
	void close();
}
