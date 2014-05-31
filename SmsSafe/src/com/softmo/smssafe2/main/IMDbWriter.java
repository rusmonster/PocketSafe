package com.softmo.smssafe2.main;

import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe2.utils.MyException;

public interface IMDbWriter {
	void SmsUpdate(IMSms sms) throws MyException; 	
	void SmsDelAll() throws MyException;
	void SmsDeleteByHash(String phone) throws MyException;
	void SmsDelete(int sms_id) throws MyException;
	
	void UpdateSetting(TTypSetting typ, String val) throws MyException;
	void SmsMarkAllRead() throws MyException;
}
