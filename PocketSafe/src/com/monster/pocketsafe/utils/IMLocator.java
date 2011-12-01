package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.dbengine.IMContact;
import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbTableContact;
import com.monster.pocketsafe.dbengine.IMDbTableSetting;
import com.monster.pocketsafe.dbengine.IMDbTableSms;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.IMSmsGroup;
import com.monster.pocketsafe.main.IMDbWriterInternal;
import com.monster.pocketsafe.main.IMDispatcherSender;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMEventErr;
import com.monster.pocketsafe.main.IMEventSimpleID;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.sms.sender.IMSmsSender;

public interface IMLocator {
	IMDbTableSetting createDbTableSetting();
	IMDbTableSms createDbTableSms();
	IMDbTableContact createDbTableContact();
	IMSms createSms();
	IMSmsGroup createSmsGroup();
	IMContact createContact();
	IMSetting createSetting();
	IMDbEngine createDbEngine();
	IMMain createMain();
	IMDispatcherSender createDispatcher();
	IMEvent createEvent();
	IMEventSimpleID createEventSimpleID();
	IMEventErr createEventErr();
	IMDbWriterInternal createDbWriter();
	IMSmsSender createSmsSender();
}
