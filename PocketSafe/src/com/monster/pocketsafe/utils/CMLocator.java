package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.dbengine.CMDbTableSetting;
import com.monster.pocketsafe.dbengine.CMDbTableSms;
import com.monster.pocketsafe.dbengine.CMSdkDbConnection;
import com.monster.pocketsafe.dbengine.CMSetting;
import com.monster.pocketsafe.dbengine.CMSms;
import com.monster.pocketsafe.dbengine.IMDbTableSetting;
import com.monster.pocketsafe.dbengine.IMDbTableSms;
import com.monster.pocketsafe.dbengine.IMSdkDbConection;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;

public class CMLocator implements IMLocator {

	public IMDbTableSetting createDbTableSetting() {
		return new CMDbTableSetting();
	}

	public IMSdkDbConection createSdkDbConnection() {
		return new CMSdkDbConnection();
	}

	public IMDbTableSms createDbTableSms() {
		return new CMDbTableSms(this);
	}

	public IMSetting createSetting() {
		return new CMSetting();
	}

	public IMSms createSms() {
		return new CMSms();
	}


}
