package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbQuerySetting extends IMDbDataSet {
	public enum TTypSetting {
		EDbZeroSetting,
		EDbVersion
	}
	boolean getById(IMSetting dest, TTypSetting id) throws MyException;
}
