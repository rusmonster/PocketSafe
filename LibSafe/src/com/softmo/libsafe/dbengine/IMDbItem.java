package com.softmo.libsafe.dbengine;

import com.softmo.libsafe.utils.MyException;

public interface IMDbItem {
	int getId();
	void setId(int id) throws MyException;
}
