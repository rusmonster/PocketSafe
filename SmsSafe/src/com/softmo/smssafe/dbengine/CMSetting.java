package com.softmo.smssafe.dbengine;

import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

public class CMSetting implements IMSetting {
	private int mId;
	private String mVal;
	public int getIntVal() {
		return Integer.parseInt(mVal);
	}

	public void setIntVal(int id) {
		mVal = Integer.toString(id);
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) throws MyException {
		if (id<0)
			throw new MyException(TTypMyException.EInvalidDbId);
		mId = id;
	}

	public String getStrVal() {
		return mVal;
	}

	public void setStrVal(String val) {
		mVal = val;
	}

}
