package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbQuerySetting extends IMDbDataSet {
	public enum TTypSetting {
		EDbZeroSetting,
		EDbVersion,
		EDbPassTimout
	}
	void getById(IMSetting dest, TTypSetting id) throws MyException;
}
