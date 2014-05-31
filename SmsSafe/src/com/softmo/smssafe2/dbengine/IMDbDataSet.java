package com.softmo.smssafe2.dbengine;

import com.softmo.smssafe2.dbengine.provider.IMDbProvider;
import com.softmo.smssafe2.utils.MyException;

public interface IMDbDataSet {
	public void SetDbProvider(IMDbProvider dbp);
	int getCount() throws MyException;
}
