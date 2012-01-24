package com.monster.pocketsafe.rsa;


public interface IMRsaObserver {
	public void RsaKeyPairGenerated(IMRsa _sender) throws Exception;
	public void RsaKeyPairGenerateError(IMRsa _sender, int _err) throws Exception;
}
