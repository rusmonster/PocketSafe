package com.softmo.libsafe.sec;

import java.math.BigInteger;


public interface IMRsaObserver {
	public void RsaKeyPairGenerated(IMRsa _sender, BigInteger _key) throws Exception;
	public void RsaKeyPairGenerateError(IMRsa _sender, int _err) throws Exception;
}
