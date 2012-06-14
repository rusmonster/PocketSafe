package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.dbengine.provider.IMDbProvider;
import com.softmo.smssafe.utils.IMLocator;


public class CMDbEngine implements IMDbEngine {
	private IMLocator mLocator;
	private IMDbProvider mDbp;
	private IMDbTableSetting mTabSetting;
	private IMDbTableSms mTabSms;
	private IMDbTableContact mTabContact;
	
	public CMDbEngine(IMLocator locator) {
		mLocator = locator;
	}
	
	public void Open(IMDbProvider dbp) {
		
		mDbp = dbp;
		
		mTabSetting = mLocator.createDbTableSetting();
		mTabSetting.SetDbProvider(mDbp);
		
		mTabSms = mLocator.createDbTableSms();
		mTabSms.SetDbProvider(mDbp);
		
		mTabContact = mLocator.createDbTableContact();
		mTabContact.SetDbProvider(mDbp);
		
	}

	public IMDbTableSms TableSms() {
		return mTabSms;
	}

	public IMDbQuerySms QuerySms() {
		return mTabSms;
	}
	
	public IMDbTableSetting TableSetting() {
		return mTabSetting;
	}

	public IMDbQuerySetting QuerySetting() {
		return mTabSetting;
	}

	public IMDbQueryContact QueryContact() {
		return mTabContact;
	}

}
