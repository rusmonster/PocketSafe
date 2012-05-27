package com.softmo.smssafe.utils;

import com.softmo.smssafe.dbengine.IMContact;
import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.dbengine.IMDbTableContact;
import com.softmo.smssafe.dbengine.IMDbTableSetting;
import com.softmo.smssafe.dbengine.IMDbTableSms;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.IMSmsGroup;
import com.softmo.smssafe.main.IMDbWriterInternal;
import com.softmo.smssafe.main.IMDispatcherSender;
import com.softmo.smssafe.main.IMEvent;
import com.softmo.smssafe.main.IMEventErr;
import com.softmo.smssafe.main.IMEventSimpleID;
import com.softmo.smssafe.main.IMMain;
import com.softmo.smssafe.main.IMPassHolder;
import com.softmo.smssafe.main.notificator.IMSmsNotificator;
import com.softmo.smssafe.sec.IMAes;
import com.softmo.smssafe.sec.IMBase64;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.sec.IMSha256;
import com.softmo.smssafe.sms.sender.IMSmsSender;

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
	IMSmsNotificator createSmsNotificator();
	IMBase64 createBase64();
	IMRsa createRsa();
	IMAes createAes();
	IMPassHolder createPassHolder();
	IMTimer createTimer();
	IMSha256 createSha256();
}
