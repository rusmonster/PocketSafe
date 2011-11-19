package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbTableSetting extends IMDbQuerySetting {
	void Update(IMSetting setting) throws MyException;
}
