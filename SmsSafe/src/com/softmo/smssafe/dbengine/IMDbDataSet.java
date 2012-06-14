package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.dbengine.provider.IMDbProvider;
import com.softmo.smssafe.utils.MyException;

public interface IMDbDataSet {
	public void SetDbProvider(IMDbProvider dbp);
	int getCount() throws MyException;
}
