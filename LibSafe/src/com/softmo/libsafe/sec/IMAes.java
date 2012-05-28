package com.softmo.libsafe.sec;

import com.softmo.libsafe.utils.MyException;

public interface IMAes {
	public String encrypt(String seed, String cleartext) throws MyException;
	public String decrypt(String seed, String encrypted) throws MyException;
}
