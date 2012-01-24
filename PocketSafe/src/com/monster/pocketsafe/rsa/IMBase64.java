package com.monster.pocketsafe.rsa;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data);
	public byte[] decode(byte[] _data);

}
