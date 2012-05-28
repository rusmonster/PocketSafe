package com.softmo.libsafe.dbengine;

import com.softmo.libsafe.utils.MyException;

public interface IMDbTableSetting extends IMDbQuerySetting {
	void Update(IMSetting setting) throws MyException;
}
