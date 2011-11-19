package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbDataSet {
	public void SetConnection(IMSdkDbConection conn);
	int getCount() throws MyException;
}
