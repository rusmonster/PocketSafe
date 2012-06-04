package com.softmo.smssafe.sec;

import com.softmo.smssafe.utils.MyException;

public interface IMSha256 {
	String getHash(String _data) throws MyException;
}
