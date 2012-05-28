package com.softmo.libsafe.utils;

import com.softmo.libsafe.dbengine.CMContact;
import com.softmo.libsafe.dbengine.CMDbEngine;
import com.softmo.libsafe.dbengine.CMDbTableContact;
import com.softmo.libsafe.dbengine.CMDbTableSetting;
import com.softmo.libsafe.dbengine.CMDbTableSms;
import com.softmo.libsafe.dbengine.CMSetting;
import com.softmo.libsafe.dbengine.CMSms;
import com.softmo.libsafe.dbengine.CMSmsGroup;
import com.softmo.libsafe.dbengine.IMContact;
import com.softmo.libsafe.dbengine.IMDbEngine;
import com.softmo.libsafe.dbengine.IMDbTableContact;
import com.softmo.libsafe.dbengine.IMDbTableSetting;
import com.softmo.libsafe.dbengine.IMDbTableSms;
import com.softmo.libsafe.dbengine.IMSetting;
import com.softmo.libsafe.dbengine.IMSms;
import com.softmo.libsafe.dbengine.IMSmsGroup;
import com.softmo.libsafe.main.CMDbWriter;
import com.softmo.libsafe.main.CMDispatcher;
import com.softmo.libsafe.main.CMEvent;
import com.softmo.libsafe.main.CMEventErr;
import com.softmo.libsafe.main.CMEventSimpleID;
import com.softmo.libsafe.main.CMMain;
import com.softmo.libsafe.main.CMPassHolder;
import com.softmo.libsafe.main.IMDbWriterInternal;
import com.softmo.libsafe.main.IMDispatcherSender;
import com.softmo.libsafe.main.IMEvent;
import com.softmo.libsafe.main.IMEventErr;
import com.softmo.libsafe.main.IMEventSimpleID;
import com.softmo.libsafe.main.IMMain;
import com.softmo.libsafe.main.IMPassHolder;
import com.softmo.libsafe.main.notificator.CMSmsNotificator;
import com.softmo.libsafe.main.notificator.IMSmsNotificator;
import com.softmo.libsafe.sec.CMAes;
import com.softmo.libsafe.sec.CMBase64;
import com.softmo.libsafe.sec.CMRsa;
import com.softmo.libsafe.sec.CMSha256;
import com.softmo.libsafe.sec.IMAes;
import com.softmo.libsafe.sec.IMBase64;
import com.softmo.libsafe.sec.IMRsa;
import com.softmo.libsafe.sec.IMSha256;
import com.softmo.libsafe.smssender.CMSmsSender;
import com.softmo.libsafe.smssender.IMSmsSender;

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

	public IMSmsNotificator createSmsNotificator() {
		return new CMSmsNotificator();
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
	}

	public IMSha256 createSha256() {
		return new CMSha256();
	}


}
