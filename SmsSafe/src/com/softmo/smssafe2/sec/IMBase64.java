package com.softmo.smssafe2.sec;

import com.softmo.smssafe2.utils.MyException;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data) throws MyException;
	public byte[] decode(byte[] _data) throws MyException;

}
