package com.softmo.smssafe2.dbengine;

public interface IMDbQueryContact extends IMDbDataSet {
	IMContact getByPhone(String phone);
}
