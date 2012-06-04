package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.utils.MyException;

public interface IMDbTableSetting extends IMDbQuerySetting {
	void Update(IMSetting setting) throws MyException;
}
