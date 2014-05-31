package com.softmo.smssafe2.dbengine;

import java.util.ArrayList;

import com.softmo.smssafe2.utils.MyException;

public interface IMDbQuerySms extends IMDbDataSet {
	void getById(IMSms dest, int id) throws MyException;
	void getGroupById(IMSmsGroup dest, int id) throws MyException;
	String getHashById(int id)  throws MyException;
	IMSms getBySmsId(int smsId) throws MyException;
	int  getCountNew() throws MyException;
	int  getCountByHash(String hash) throws MyException;
	void QueryByFolderOrderByDatDesc(ArrayList<IMSms> dest, int folder, int start, int count) throws MyException;
	void QueryGroupOrderByMaxDatDesc(ArrayList<IMSmsGroup> dest, int start, int count) throws MyException;
	void QueryByHashOrderByDat(ArrayList<IMSms> dest, String hash, int start, int count) throws MyException;
	int getCountGroup() throws MyException;
}
