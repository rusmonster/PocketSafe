package com.monster.pocketsafe.main;

import com.monster.pocketsafe.utils.MyException;

public interface IMPassHolder {
	public boolean isEmpty();
	public void setPass(String pass);
	public String getPass() throws MyException;
}
