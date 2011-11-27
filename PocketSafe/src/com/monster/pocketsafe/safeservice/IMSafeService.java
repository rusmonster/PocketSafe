package com.monster.pocketsafe.safeservice;

import com.monster.pocketsafe.utils.MyException;

public interface IMSafeService {
	public boolean handleSms(String from, String text) throws MyException;

}
