package com.monster.pocketsafe.sec;

import com.monster.pocketsafe.utils.MyException;

public interface IMSha256 {
	String getHash(String _data) throws MyException;
}
