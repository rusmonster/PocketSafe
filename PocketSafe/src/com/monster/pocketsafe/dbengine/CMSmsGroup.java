package com.monster.pocketsafe.dbengine;

import java.util.Date;

public class CMSmsGroup implements IMSmsGroup {
	
	private String mHash;
	private String mPhone;
	private int mCount;
	private int mCountNew;
	private Date mDate;

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		mPhone = phone;
	}

	public int getCount() {
		return mCount;
	}

	public void setCount(int count) {
		mCount = count;
	}

	public int getCountNew() {
		return mCountNew;
	}

	public void setCountNew(int countNew) {
		mCountNew = countNew;
	}

	public Date getDate() {
		return mDate;
	}

	public void setDate(Date dat) {
		mDate = dat;
	}

	public String getHash() {
		return mHash;
	}

	public void setHash(String hash) {
		mHash = hash;
	}

}
