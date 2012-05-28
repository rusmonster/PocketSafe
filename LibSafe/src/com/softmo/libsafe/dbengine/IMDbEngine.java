package com.softmo.libsafe.dbengine;

import android.content.ContentResolver;

public interface IMDbEngine extends IMDbReader {
	final static String ENCODING = "ISO_8859_1";
	
	void Open(ContentResolver cr);
	
	IMDbQuerySetting QuerySetting();
	IMDbTableSetting TableSetting();
	
	IMDbQuerySms QuerySms();
	IMDbTableSms TableSms();
}
