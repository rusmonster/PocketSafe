package com.monster.pocketsafe.main;

import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

public class CMPassHolder implements IMPassHolder {

	private IMLocator mLocator;
	private String mPass;
	
	public CMPassHolder(IMLocator locator) {
		mLocator = locator;
	}
	
	public boolean isEmpty() {
		return (mPass == null);
	}

	public void setPass(String pass) {
		mPass = pass;
	}

	public String getPass() throws MyException {
		return mPass;
	}

}
