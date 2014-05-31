package com.softmo.smssafe2.sec;

import com.softmo.smssafe2.utils.MyException;

public interface IMAes {
	public String encrypt(String seed, String cleartext) throws MyException;
	public String decrypt(String seed, String encrypted) throws MyException;
}
