package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.dbengine.IMDbTableSetting;
import com.monster.pocketsafe.dbengine.IMDbTableSms;
import com.monster.pocketsafe.dbengine.IMSdkDbConection;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;

public interface IMLocator {
	IMSdkDbConection createSdkDbConnection();
	IMDbTableSetting createDbTableSetting();
	IMDbTableSms createDbTableSms();
	IMSms createSms();
	IMSetting createSetting();
}
