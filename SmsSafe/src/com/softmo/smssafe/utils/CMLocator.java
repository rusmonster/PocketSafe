package com.softmo.smssafe.utils;

import android.content.Context;

import com.softmo.smssafe.dbengine.CMContact;
import com.softmo.smssafe.dbengine.CMDbEngine;
import com.softmo.smssafe.dbengine.CMDbTableContact;
import com.softmo.smssafe.dbengine.CMDbTableSetting;
import com.softmo.smssafe.dbengine.CMDbTableSms;
import com.softmo.smssafe.dbengine.CMSetting;
import com.softmo.smssafe.dbengine.CMSms;
import com.softmo.smssafe.dbengine.CMSmsGroup;
import com.softmo.smssafe.dbengine.IMContact;
import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.dbengine.IMDbTableContact;
import com.softmo.smssafe.dbengine.IMDbTableSetting;
import com.softmo.smssafe.dbengine.IMDbTableSms;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.IMSmsGroup;
import com.softmo.smssafe.dbengine.provider.CMDbProvider;
import com.softmo.smssafe.dbengine.provider.IMDbProvider;
import com.softmo.smssafe.main.CMDbWriter;
import com.softmo.smssafe.main.CMDispatcher;
import com.softmo.smssafe.main.CMEvent;
import com.softmo.smssafe.main.CMEventErr;
import com.softmo.smssafe.main.CMEventSimpleID;
import com.softmo.smssafe.main.CMMain;
import com.softmo.smssafe.main.CMPassHolder;
import com.softmo.smssafe.main.IMDbWriterInternal;
import com.softmo.smssafe.main.IMDispatcherSender;
import com.softmo.smssafe.main.IMEvent;
import com.softmo.smssafe.main.IMEventErr;
import com.softmo.smssafe.main.IMEventSimpleID;
import com.softmo.smssafe.main.IMMain;
import com.softmo.smssafe.main.IMPassHolder;
import com.softmo.smssafe.main.importer.CMImporter;
import com.softmo.smssafe.main.importer.IMImporter;
import com.softmo.smssafe.main.notificator.CMNotificatorSound;
import com.softmo.smssafe.main.notificator.IMNotificatorSound;
import com.softmo.smssafe.sec.CMAes;
import com.softmo.smssafe.sec.CMBase64;
import com.softmo.smssafe.sec.CMRsa;
import com.softmo.smssafe.sec.CMSha256;
import com.softmo.smssafe.sec.IMAes;
import com.softmo.smssafe.sec.IMBase64;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.sec.IMSha256;
import com.softmo.smssafe.smssender.CMSmsSender;
import com.softmo.smssafe.smssender.IMSmsSender;

public class CMLocator implements IMLocator {

	public IMDbTableSetting createDbTableSetting() {
		return new CMDbTableSetting();
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

	public IMDbEngine createDbEngine() {
		return new CMDbEngine(this);
	}

	public IMMain createMain() {
		return new CMMain(this);
	}

	public IMSmsGroup createSmsGroup() {
		return new CMSmsGroup();
	}

	public IMContact createContact() {
		return new CMContact();
	}

	public IMDbTableContact createDbTableContact() {
		return new CMDbTableContact(this);
	}

	public IMDispatcherSender createDispatcher() {
		return new CMDispatcher();
	}

	public IMEventSimpleID createEventSimpleID() {
		return new CMEventSimpleID();
	}

	public IMDbWriterInternal createDbWriter() {
		return new CMDbWriter(this);
	}

	public IMSmsSender createSmsSender() {
		return new CMSmsSender();
	}

	public IMEvent createEvent() {
		return new CMEvent();
	}

	public IMEventErr createEventErr() {
		return new CMEventErr();
	}

	public IMNotificatorSound createSmsNotificator() {
		return new CMNotificatorSound();
	}

	public IMBase64 createBase64() {
		return new CMBase64();
	}

	public IMRsa createRsa() {
		return new CMRsa(this);
	}

	public IMAes createAes() {
		return new CMAes(this);
	}

	public IMPassHolder createPassHolder() {
		return new CMPassHolder(this);
	}

	public IMTimer createTimer() {
		return new CMTimer();
		//return new CMTimer2();
	}
	
	public IMTimerWakeup createTimerWakeup() {
		return new CMTimerWakeup();
	}

	public IMSha256 createSha256() {
		return new CMSha256();
	}

	public IMDbProvider createDbProvider(Context context) {
		return new CMDbProvider(context);
	}

	public IMImporter createImporter() {
		return new CMImporter(this);
	}


}
