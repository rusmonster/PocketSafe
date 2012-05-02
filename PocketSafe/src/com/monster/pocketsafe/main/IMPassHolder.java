package com.monster.pocketsafe.main;

import java.math.BigInteger;

import com.monster.pocketsafe.utils.MyException;

public interface IMPassHolder {
	public void setObserever(IMPassHolderObserver observer);
	public void setKey(String _key);;
	public BigInteger getKey() throws MyException;
	public void setPass(String pass) throws MyException;
	public String getPass() throws MyException;
	public boolean isPassValid();
	public void setInterval(long _ms) throws MyException;
	public void clearPass();
}
