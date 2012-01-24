package com.monster.pocketsafe.sec;

import com.monster.pocketsafe.utils.MyException;

public interface IMAes {
	public String encrypt(String seed, String cleartext) throws MyException;
	public String decrypt(String seed, String encrypted) throws MyException;
}
