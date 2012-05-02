package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbTableSms extends IMDbQuerySms {
	int Insert(IMSms item) throws MyException;
	void Update(IMSms item) throws MyException;
	void Delete(int id) throws MyException;
	void DeleteByHash(String hash) throws MyException;
	void Clear() throws MyException;
}
