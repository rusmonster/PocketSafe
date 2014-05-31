package com.softmo.smssafe2.utils;

import android.content.Context;

import com.softmo.smssafe2.dbengine.CMContact;
import com.softmo.smssafe2.dbengine.CMDbEngine;
import com.softmo.smssafe2.dbengine.CMDbTableContact;
import com.softmo.smssafe2.dbengine.CMDbTableSetting;
import com.softmo.smssafe2.dbengine.CMDbTableSms;
import com.softmo.smssafe2.dbengine.CMSetting;
import com.softmo.smssafe2.dbengine.CMSms;
import com.softmo.smssafe2.dbengine.CMSmsGroup;
import com.softmo.smssafe2.dbengine.IMContact;
import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.dbengine.IMDbTableContact;
import com.softmo.smssafe2.dbengine.IMDbTableSetting;
import com.softmo.smssafe2.dbengine.IMDbTableSms;
import com.softmo.smssafe2.dbengine.IMSetting;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.IMSmsGroup;
import com.softmo.smssafe2.dbengine.provider.CMDbProvider;
import com.softmo.smssafe2.dbengine.provider.IMDbProvider;
import com.softmo.smssafe2.main.CMDbWriter;
import com.softmo.smssafe2.main.CMDispatcher;
import com.softmo.smssafe2.main.CMEvent;
import com.softmo.smssafe2.main.CMEventErr;
import com.softmo.smssafe2.main.CMEventSimpleID;
import com.softmo.smssafe2.main.CMMain;
import com.softmo.smssafe2.main.CMPassHolder;
import com.softmo.smssafe2.main.IMDbWriterInternal;
import com.softmo.smssafe2.main.IMDispatcherSender;
import com.softmo.smssafe2.main.IMEvent;
import com.softmo.smssafe2.main.IMEventErr;
import com.softmo.smssafe2.main.IMEventSimpleID;
import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.main.IMPassHolder;
import com.softmo.smssafe2.main.importer.CMImporter;
import com.softmo.smssafe2.main.importer.IMImporter;
import com.softmo.smssafe2.main.notificator.CMNotificatorSound;
import com.softmo.smssafe2.main.notificator.IMNotificatorSound;
import com.softmo.smssafe2.sec.CMAes;
import com.softmo.smssafe2.sec.CMBase64;
import com.softmo.smssafe2.sec.CMRsa;
import com.softmo.smssafe2.sec.CMSha256;
import com.softmo.smssafe2.sec.IMAes;
import com.softmo.smssafe2.sec.IMBase64;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.sec.IMSha256;
import com.softmo.smssafe2.smssender.CMSmsSender;
import com.softmo.smssafe2.smssender.IMSmsSender;

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
