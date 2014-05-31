package com.softmo.smssafe2.dbengine;

import android.content.Context;

public interface IMDbEngine extends IMDbReader {
	final static String ENCODING = "ISO_8859_1";
	
	void Open(Context context);
	
	IMDbQuerySetting QuerySetting();
	IMDbTableSetting TableSetting();
	
	IMDbQuerySms QuerySms();
	IMDbTableSms TableSms();
}
