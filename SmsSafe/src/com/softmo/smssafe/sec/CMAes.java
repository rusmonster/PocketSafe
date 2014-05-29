package com.softmo.smssafe.sec;

import android.os.SystemClock;
import android.util.Log;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

public class CMAes implements IMAes {
	private static final String TAG = "CMAes";
	private static final int ITERATION_COUNT = 10000;
	private static final int KEY_LEN = 256;
	private static final int SALT_LEN = KEY_LEN / 8;
	private static final String DELIMITER = "]";
	private static final String UTF8 = "UTF-8";

	private IMLocator mLocator;
	
	public CMAes(IMLocator _locator) {
		mLocator = _locator;
	}
	
	public String encrypt(String seed, String cleartext) throws MyException {
		Log.i("!!!", "encrypting... ");
		long tim = SystemClock.elapsedRealtime();
		try {
			SecureRandom random = new SecureRandom();
			byte[] salt = new byte[SALT_LEN];
			random.nextBytes(salt);
			SecretKey key = deriveKeyPbkdf2(salt, seed);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[cipher.getBlockSize()];
			random.nextBytes(iv);
			IvParameterSpec ivParams = new IvParameterSpec(iv);

			cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
			byte[] result = cipher.doFinal(cleartext.getBytes(UTF8));

			IMBase64 base64 = mLocator.createBase64();
			StringBuilder sb = new StringBuilder();
	        sb.append(new String(base64.encode(salt), UTF8));
			sb.append(DELIMITER);
			sb.append(new String(base64.encode(iv), UTF8));
			sb.append(DELIMITER);
			sb.append(new String(base64.encode(result), UTF8));

			tim = SystemClock.elapsedRealtime() - tim;
			Log.i("!!!", "encrypting done all: " + tim);
			return sb.toString();
		} catch (Exception e) {
			throw new MyException(TTypMyException.EAesErrEncrypt);
		}
	}
	
	public String decrypt(String seed, String encrypted) throws MyException {
		Log.i("!!!", "decrypting... ");
		long tim = SystemClock.elapsedRealtime();
		try {
			IMBase64 base64 = mLocator.createBase64();
			String[] fields = encrypted.split(DELIMITER);
			byte[] salt = base64.decode(fields[0].getBytes(UTF8));
			byte[] iv = base64.decode((fields[1].getBytes(UTF8)));
			byte[] cipherBytes = base64.decode(fields[2].getBytes(UTF8));
			SecretKey key = deriveKeyPbkdf2(salt, seed);

			IvParameterSpec ivParams = new IvParameterSpec(iv);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
			byte[] plaintext = cipher.doFinal(cipherBytes);

			tim = SystemClock.elapsedRealtime() - tim;
			Log.i("!!!", "decrypting done all: " + tim);
	        return new String(plaintext, UTF8);
		} catch (Exception e) {
			throw new MyException(TTypMyException.EAesErrDecrypt);
		}
	}
	
	private static SecretKey deriveKeyPbkdf2(byte[] salt, String password) throws Exception {
		KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LEN);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
		SecretKey key = new SecretKeySpec(keyBytes, "AES");
		return key;
	}
}
