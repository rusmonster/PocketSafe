package com.softmo.smssafe2.dbengine;

import com.softmo.smssafe2.utils.MyException;

public interface IMDbItem {
	int getId();
	void setId(int id) throws MyException;
}
