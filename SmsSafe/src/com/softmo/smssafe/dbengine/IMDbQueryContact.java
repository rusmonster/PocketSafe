package com.softmo.smssafe.dbengine;

public interface IMDbQueryContact extends IMDbDataSet {
	IMContact getByPhone(String phone);
}
