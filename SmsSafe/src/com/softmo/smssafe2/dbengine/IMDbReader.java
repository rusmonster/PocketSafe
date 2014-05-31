package com.softmo.smssafe2.dbengine;

public interface IMDbReader {
	IMDbQuerySetting QuerySetting();
	IMDbQuerySms QuerySms();
	IMDbQueryContact QueryContact();
}
