package com.softmo.smssafe.dbengine;

import android.content.ContentResolver;

import com.softmo.smssafe.utils.IMLocator;


public class CMDbEngine implements IMDbEngine {
	private IMLocator mLocator;
	private ContentResolver mCr;
	private IMDbTableSetting mTabSetting;
	private IMDbTableSms mTabSms;
	private IMDbTableContact mTabContact;
	
	public CMDbEngine(IMLocator locator) {
		mLocator = locator;
	}
	
	public void Open(ContentResolver cr) {
		
		mCr = cr;
		
		mTabSetting = mLocator.createDbTableSetting();
		mTabSetting.SetContentResolver(mCr);
		
		mTabSms = mLocator.createDbTableSms();
		mTabSms.SetContentResolver(mCr);
		
		mTabContact = mLocator.createDbTableContact();
		mTabContact.SetContentResolver(mCr);
		
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