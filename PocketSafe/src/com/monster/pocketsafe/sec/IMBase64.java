package com.monster.pocketsafe.sec;

import com.monster.pocketsafe.utils.MyException;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data) throws MyException;
	public byte[] decode(byte[] _data) throws MyException;

}
