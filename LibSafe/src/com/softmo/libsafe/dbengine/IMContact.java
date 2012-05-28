package com.softmo.libsafe.dbengine;

public interface IMContact extends IMDbItem {
	
	String getName();
	void setName(String name);
	
	String getPhone();
	void setPhone(String phone);

}
