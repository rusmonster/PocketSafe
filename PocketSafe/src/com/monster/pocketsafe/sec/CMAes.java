package com.monster.pocketsafe.sec;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMAes implements IMAes {
	
	private IMLocator mLocator;
	private IMBase64 mBase64;
	
	public CMAes(IMLocator _locator) {
		mLocator = _locator;
		mBase64 = mLocator.createBase64();
	}
	
	public String encrypt(String seed, String cleartext) throws MyException {
		try {
	        byte[] rawKey = getRawKey(seed.getBytes());
	        byte[] result = encrypt(rawKey, cleartext.getBytes());
	        String ret = new String( mBase64.encode(result) );
	        return ret;
		} catch (Exception e) {
			throw new MyException(TTypMyException.EAesErrEncrypt);
		}
	}
	
	public String decrypt(String seed, String encrypted) throws MyException {
		try {
	        byte[] rawKey = getRawKey(seed.getBytes());
	        byte[] enc = mBase64.decode(encrypted.getBytes());
	        byte[] result = decrypt(rawKey, enc);
	        return new String(result);
		} catch (Exception e) {
			throw new MyException(TTypMyException.EAesErrDecrypt);
		}
	}
	
	private byte[] getRawKey(byte[] seed) throws Exception {
	    KeyGenerator kgen = KeyGenerator.getInstance("AES");
	    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	    sr.setSeed(seed);
	    kgen.init(256, sr); // 192 and 256 bits may not be available
	    SecretKey skey = kgen.generateKey();
	    byte[] raw = skey.getEncoded();
	    
	    return raw;
	}
	
	
	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
	    return encrypted;
	}
	
	private byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
	    return decrypted;
	}
	
}
