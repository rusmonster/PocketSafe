package com.monster.pocketsafe.dbengine;

import java.util.ArrayList;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbQuerySms extends IMDbDataSet {
	void getById(IMSms dest, int id) throws MyException;
	String getHashById(int id)  throws MyException;
	int  getCountNew() throws MyException;
	void QueryByFolderOrderByDatDesc(ArrayList<IMSms> dest, int folder, int start, int count) throws MyException;
	void QueryGroupByHashOrderByMaxDatDesc(ArrayList<IMSmsGroup> dest, int start, int count);
	void QueryByHashOrderByDat(ArrayList<IMSms> dest, String phone, int start, int count) throws MyException;
}
