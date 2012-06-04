package com.softmo.smssafe.sec;

import com.softmo.smssafe.utils.MyException;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data) throws MyException;
	public byte[] decode(byte[] _data) throws MyException;

}
