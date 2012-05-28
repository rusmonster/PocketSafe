package com.softmo.libsafe.dbengine;

public interface IMDbQueryContact extends IMDbDataSet {
	IMContact getByPhone(String phone);
}
