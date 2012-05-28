package com.softmo.libsafe.sec;

import com.softmo.libsafe.utils.MyException;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data) throws MyException;
	public byte[] decode(byte[] _data) throws MyException;

}
