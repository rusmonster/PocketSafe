package com.softmo.libsafe.utils;

import com.softmo.libsafe.dbengine.IMContact;
import com.softmo.libsafe.dbengine.IMDbEngine;
import com.softmo.libsafe.dbengine.IMDbTableContact;
import com.softmo.libsafe.dbengine.IMDbTableSetting;
import com.softmo.libsafe.dbengine.IMDbTableSms;
import com.softmo.libsafe.dbengine.IMSetting;
import com.softmo.libsafe.dbengine.IMSms;
import com.softmo.libsafe.dbengine.IMSmsGroup;
import com.softmo.libsafe.main.IMDbWriterInternal;
import com.softmo.libsafe.main.IMDispatcherSender;
import com.softmo.libsafe.main.IMEvent;
import com.softmo.libsafe.main.IMEventErr;
import com.softmo.libsafe.main.IMEventSimpleID;
import com.softmo.libsafe.main.IMMain;
import com.softmo.libsafe.main.IMPassHolder;
import com.softmo.libsafe.main.notificator.IMSmsNotificator;
import com.softmo.libsafe.sec.IMAes;
import com.softmo.libsafe.sec.IMBase64;
import com.softmo.libsafe.sec.IMRsa;
import com.softmo.libsafe.sec.IMSha256;
import com.softmo.libsafe.smssender.IMSmsSender;

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
