package com.monster.pocketsafe.sec;

public interface IMBase64 {
	
	public byte[] encode(byte[] _data);
	public byte[] decode(byte[] _data);

}
