package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbTableSetting;
import com.monster.pocketsafe.dbengine.IMDbTableSms;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;

public interface IMLocator {
	IMDbTableSetting createDbTableSetting();
	IMDbTableSms createDbTableSms();
	IMSms createSms();
	IMSetting createSetting();
	IMDbEngine createDbEngine();
}
