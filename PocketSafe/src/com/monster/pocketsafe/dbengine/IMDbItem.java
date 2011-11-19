package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbItem {
	int getId();
	void setId(int id) throws MyException;
}
