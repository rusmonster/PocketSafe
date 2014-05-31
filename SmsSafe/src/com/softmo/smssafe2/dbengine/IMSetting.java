package com.softmo.smssafe2.dbengine;

public interface IMSetting extends IMDbItem {
	int getIntVal();
	void setIntVal(int id);
	String getStrVal();
	void setStrVal(String val);
}
