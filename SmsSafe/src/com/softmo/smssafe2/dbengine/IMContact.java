package com.softmo.smssafe2.dbengine;

public interface IMContact extends IMDbItem {
	
	String getName();
	void setName(String name);
	
	String getPhone();
	void setPhone(String phone);

}
