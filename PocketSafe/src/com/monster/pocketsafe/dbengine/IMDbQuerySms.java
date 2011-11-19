package com.monster.pocketsafe.dbengine;

import java.util.ArrayList;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbQuerySms extends IMDbDataSet {
	void getById(IMSms dest, int id) throws MyException;
	void QueryByFolderOrderByDatDesc(ArrayList<IMSms> dest, int folder, int start, int count) throws MyException;
}
