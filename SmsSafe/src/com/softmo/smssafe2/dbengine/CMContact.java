package com.softmo.smssafe2.dbengine;

import com.softmo.smssafe2.utils.MyException;

public class CMContact implements IMContact {
	
	private String mName;
	private String mPhone;

	public int getId() {
		return 0;
	}

	public void setId(int id) throws MyException {
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getPhone() {
		return mPhone;
	}

	public void setPhone(String phone) {
		mPhone = phone;
	}

}
