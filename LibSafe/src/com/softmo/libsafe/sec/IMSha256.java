package com.softmo.libsafe.sec;

import com.softmo.libsafe.utils.MyException;

public interface IMSha256 {
	String getHash(String _data) throws MyException;
}
