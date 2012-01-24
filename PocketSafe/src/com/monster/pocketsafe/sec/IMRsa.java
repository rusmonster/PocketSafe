package com.monster.pocketsafe.sec;

import com.monster.pocketsafe.utils.MyException;

public interface IMRsa {
	
	public void setObserver(IMRsaObserver _observer);
	
	public String getPrivateKey() throws MyException;
	public void setPrivateKey(String _key) throws MyException;
	
	public String getPublicKey() throws MyException;
	public void setPublicKey(String _key) throws MyException;
	
	public void startGenerateKeyPair() throws MyException;
	
	public byte[] EncryptBuffer(byte[] _data) throws MyException;
	public byte[] DecryptBuffer(byte[] _data) throws MyException;

}
