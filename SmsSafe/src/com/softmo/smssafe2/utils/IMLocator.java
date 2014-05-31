package com.softmo.smssafe2.utils;

import android.content.Context;

import com.softmo.smssafe2.dbengine.IMContact;
import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.dbengine.IMDbTableContact;
import com.softmo.smssafe2.dbengine.IMDbTableSetting;
import com.softmo.smssafe2.dbengine.IMDbTableSms;
import com.softmo.smssafe2.dbengine.IMSetting;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.IMSmsGroup;
import com.softmo.smssafe2.dbengine.provider.IMDbProvider;
import com.softmo.smssafe2.main.IMDbWriterInternal;
import com.softmo.smssafe2.main.IMDispatcherSender;
import com.softmo.smssafe2.main.IMEvent;
import com.softmo.smssafe2.main.IMEventErr;
import com.softmo.smssafe2.main.IMEventSimpleID;
import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.main.IMPassHolder;
import com.softmo.smssafe2.main.importer.IMImporter;
import com.softmo.smssafe2.main.notificator.IMNotificatorSound;
import com.softmo.smssafe2.sec.IMAes;
import com.softmo.smssafe2.sec.IMBase64;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.sec.IMSha256;
import com.softmo.smssafe2.smssender.IMSmsSender;

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
	IMNotificatorSound createSmsNotificator();
	IMBase64 createBase64();
	IMRsa createRsa();
	IMAes createAes();
	IMPassHolder createPassHolder();
	IMTimer createTimer();
	IMTimerWakeup createTimerWakeup();
	IMSha256 createSha256();
	IMDbProvider createDbProvider(Context context);
	IMImporter createImporter();
}
