package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.dbengine.provider.IMDbProvider;

public interface IMDbEngine extends IMDbReader {
	final static String ENCODING = "ISO_8859_1";
	
	void Open(IMDbProvider cr);
	
	IMDbQuerySetting QuerySetting();
	IMDbTableSetting TableSetting();
	
	IMDbQuerySms QuerySms();
	IMDbTableSms TableSms();
}
