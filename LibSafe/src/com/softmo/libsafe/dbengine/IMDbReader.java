package com.softmo.libsafe.dbengine;

public interface IMDbReader {
	IMDbQuerySetting QuerySetting();
	IMDbQuerySms QuerySms();
	IMDbQueryContact QueryContact();
}
