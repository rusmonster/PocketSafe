package com.monster.pocketsafe.main;

import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.utils.MyException;

public interface IMDbWriter {
	void SmsUpdate(IMSms sms) throws MyException; 	
}
