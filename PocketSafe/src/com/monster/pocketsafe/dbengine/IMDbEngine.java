package com.monster.pocketsafe.dbengine;

import android.content.ContentResolver;

public interface IMDbEngine extends IMDbReader {
	void Open(ContentResolver cr);
	
	IMDbQuerySetting QuerySetting();
	IMDbTableSetting TableSetting();
	
	IMDbQuerySms QuerySms();
	IMDbTableSms TableSms();
}
