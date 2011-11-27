package com.monster.pocketsafe.dbengine;

public interface IMDbReader {
	IMDbQuerySetting QuerySetting();
	IMDbQuerySms QuerySms();
	IMDbQueryContact QueryContact();
}
