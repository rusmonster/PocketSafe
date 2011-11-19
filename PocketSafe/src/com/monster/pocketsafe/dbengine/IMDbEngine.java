package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbEngine extends IMDbReader {
	void Open(String filename) throws MyException;
	IMDbTableSms TableSms();
	void Close();
}
