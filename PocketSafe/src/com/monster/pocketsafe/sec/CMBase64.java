package com.monster.pocketsafe.sec;

import android.util.Base64;

public class CMBase64 implements IMBase64 {

	public byte[] encode(byte[] _data) {
		return Base64.encode(_data, Base64.DEFAULT);
	}

	public byte[] decode(byte[] _data) {
		return Base64.decode(_data, Base64.DEFAULT);
	}

}
