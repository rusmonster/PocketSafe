package com.softmo.libsafe.dbengine;

import java.util.Date;

public interface IMSmsGroup extends IMDbItem {
	
	String getHash();
	void setHash(String hash);
	
	String getPhone();
	void setPhone(String phone);
	
	int getCount();
	void setCount(int count);
	
	int getCountNew();
	void setCountNew(int countNew);
	
	Date getDate();
	void setDate(Date dat);
}
