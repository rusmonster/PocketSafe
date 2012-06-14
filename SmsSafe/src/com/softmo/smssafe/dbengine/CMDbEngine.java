package com.softmo.smssafe.dbengine;

import android.content.Context;

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
	
	public void Open(Context context) {
		
		mDbp = mLocator.createDbProvider(context);
		
		mTabSetting = mLocator.createDbTableSetting();
		mTabSetting.SetDbProvider(mDbp);
		
		mTabSms = mLocator.createDbTableSms();
		mTabSms.SetDbProvider(mDbp);
		
		mTabContact = mLocator.createDbTableContact();
		mTabContact.SetContentResolver(context.getContentResolver());
		
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
