package com.softmo.smssafe2.main;

public class CMEventErr extends CMEventSimpleID implements IMEventErr {

	int mErr;
	
	public int getErr() {
		return mErr;
	}

	public void setErr (int err) {
		mErr = err;
	}

}
