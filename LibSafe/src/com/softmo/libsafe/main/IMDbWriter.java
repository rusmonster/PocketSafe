package com.softmo.libsafe.main;

import com.softmo.libsafe.dbengine.IMSms;
import com.softmo.libsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.libsafe.utils.MyException;

public interface IMDbWriter {
	void SmsUpdate(IMSms sms) throws MyException; 	
	void SmsDelAll() throws MyException;
	void SmsDeleteByHash(String phone) throws MyException;
	void SmsDelete(int sms_id) throws MyException;
	
	void UpdateSetting(TTypSetting typ, String val) throws MyException;
}
