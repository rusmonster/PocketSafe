package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbQuerySms extends IMDbDataSet {
	boolean getById(IMSms dest, int id) throws MyException;
	void QueryGroupByPhoneOrderByDatDesc(IMSms[] dest, int start, int count);
}
