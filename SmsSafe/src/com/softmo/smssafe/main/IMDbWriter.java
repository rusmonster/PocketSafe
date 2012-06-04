package com.softmo.smssafe.main;

import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.utils.MyException;

public interface IMDbWriter {
	void SmsUpdate(IMSms sms) throws MyException; 	
	void SmsDelAll() throws MyException;
	void SmsDeleteByHash(String phone) throws MyException;
	void SmsDelete(int sms_id) throws MyException;
	
	void UpdateSetting(TTypSetting typ, String val) throws MyException;
}
