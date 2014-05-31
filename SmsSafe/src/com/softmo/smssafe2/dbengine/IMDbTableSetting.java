package com.softmo.smssafe2.dbengine;

import com.softmo.smssafe2.utils.MyException;

public interface IMDbTableSetting extends IMDbQuerySetting {
	void Update(IMSetting setting) throws MyException;
}
