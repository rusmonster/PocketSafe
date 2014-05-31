package com.softmo.smssafe2.sec;

import com.softmo.smssafe2.utils.MyException;

public interface IMSha256 {
	String getHash(String _data) throws MyException;
}
