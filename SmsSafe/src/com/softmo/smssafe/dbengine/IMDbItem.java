package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.utils.MyException;

public interface IMDbItem {
	int getId();
	void setId(int id) throws MyException;
}
