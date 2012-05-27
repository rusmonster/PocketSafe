package com.softmo.smssafe.sec;

import java.math.BigInteger;

import com.softmo.smssafe.utils.MyException;

public interface IMRsa {
	
	public void setObserver(IMRsaObserver _observer);
	
	public String getPublicKey() throws MyException;
	public void setPublicKey(String _key) throws MyException;
	
	public void startGenerateKeyPair() throws MyException;
	
	public byte[] EncryptBuffer(byte[] _data) throws MyException;
	public byte[] DecryptBuffer(BigInteger _key, byte[] _data) throws MyException;

}
