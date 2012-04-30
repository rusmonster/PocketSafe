package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.dbengine.CMContact;
import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.dbengine.CMDbTableContact;
import com.monster.pocketsafe.dbengine.CMDbTableSetting;
import com.monster.pocketsafe.dbengine.CMDbTableSms;
import com.monster.pocketsafe.dbengine.CMSetting;
import com.monster.pocketsafe.dbengine.CMSms;
import com.monster.pocketsafe.dbengine.CMSmsGroup;
import com.monster.pocketsafe.dbengine.IMContact;
import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbTableContact;
import com.monster.pocketsafe.dbengine.IMDbTableSetting;
import com.monster.pocketsafe.dbengine.IMDbTableSms;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.IMSmsGroup;
import com.monster.pocketsafe.main.CMDbWriter;
import com.monster.pocketsafe.main.CMDispatcher;
import com.monster.pocketsafe.main.CMEvent;
import com.monster.pocketsafe.main.CMEventErr;
import com.monster.pocketsafe.main.CMEventSimpleID;
import com.monster.pocketsafe.main.CMMain;
import com.monster.pocketsafe.main.CMPassHolder;
import com.monster.pocketsafe.main.IMDbWriterInternal;
import com.monster.pocketsafe.main.IMDispatcherSender;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMEventErr;
import com.monster.pocketsafe.main.IMEventSimpleID;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.main.IMPassHolder;
import com.monster.pocketsafe.main.notificator.CMSmsNotificator;
import com.monster.pocketsafe.main.notificator.IMSmsNotificator;
import com.monster.pocketsafe.sec.CMAes;
import com.monster.pocketsafe.sec.CMBase64;
import com.monster.pocketsafe.sec.CMRsa;
import com.monster.pocketsafe.sec.CMSha256;
import com.monster.pocketsafe.sec.IMAes;
import com.monster.pocketsafe.sec.IMBase64;
import com.monster.pocketsafe.sec.IMRsa;
import com.monster.pocketsafe.sec.IMSha256;
import com.monster.pocketsafe.sms.sender.CMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSender;

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
