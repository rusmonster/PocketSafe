package com.softmo.smssafe.sec;

import com.softmo.smssafe.utils.MyException;

public interface IMAes {
	public String encrypt(String seed, String cleartext) throws MyException;
	public String decrypt(String seed, String encrypted) throws MyException;
}
