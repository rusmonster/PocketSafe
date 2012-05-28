package com.softmo.libsafe.dbengine;

import com.softmo.libsafe.utils.MyException;
import com.softmo.libsafe.utils.MyException.TTypMyException;

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
