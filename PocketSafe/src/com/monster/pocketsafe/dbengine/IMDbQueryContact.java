package com.monster.pocketsafe.dbengine;

public interface IMDbQueryContact extends IMDbDataSet {
	IMContact getByPhone(String phone);
}
