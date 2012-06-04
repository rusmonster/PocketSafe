package com.softmo.smssafe.dbengine;

public interface IMDbReader {
	IMDbQuerySetting QuerySetting();
	IMDbQuerySms QuerySms();
	IMDbQueryContact QueryContact();
}
