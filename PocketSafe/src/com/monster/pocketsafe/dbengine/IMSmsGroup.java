package com.monster.pocketsafe.dbengine;

import java.util.Date;

public interface IMSmsGroup {
	
	String getPhone();
	void setPhone(String phone);
	
	int getCount();
	void setCount(int count);
	
	int getCountNew();
	void setCountNew(int countNew);
	
	Date getDate();
	void setDate(Date dat);
}