package com.monster.pocketsafe.testlong.rsa;

import java.math.BigInteger;
import java.util.Random;

import android.util.Log;

import com.monster.pocketsafe.sec.CMRsa;
import com.monster.pocketsafe.sec.IMRsa;
import com.monster.pocketsafe.sec.IMRsaObserver;
import com.monster.pocketsafe.testlong.utils.CMTestThread;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import junit.framework.TestCase;

public class CMRsaTestLong extends TestCase {

	private CMRsa mRsa;
	BigInteger mKey;
		
	private IMRsaObserver mObserver = new IMRsaObserver() {
		
		@Override
		public void RsaKeyPairGenerated(IMRsa _sender, BigInteger _key) throws Exception {
			mKey = _key;
			Log.i("!!!", "RsaKeyPairGenerated");
		}
		
		@Override
		public void RsaKeyPairGenerateError(IMRsa _sender, int _err) throws Exception {
			Log.i("!!!", "RsaKeyPairGenerateError");
		}
	};
	
	protected void setUp() throws Exception {
		super.setUp();
		mKey=null;
		mRsa = new CMRsa(new CMLocator());
		mRsa.setObserver(mObserver);
	}

	public void testGenerateKeyPair() throws Exception {
		
		CMTestThread th = new CMTestThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					mRsa.startGenerateKeyPair();
				} catch (MyException e) {
					throw new RuntimeException("Error in startGenerateKeyPair");
				}
			}
		});
		th.start();
		
		int k=0;
		while (mKey==null && k++<30)
			Thread.sleep(1000);
		th.stopThread();
		
		String pub = mRsa.getPublicKey();
		
		assertNotNull(mKey);
		assertNotNull(pub);
		
		assertTrue(pub.length()>0);
	}
	
	public void testEncDecSuccess() throws Exception {
		String text = "hello world";
		byte[] data = text.getBytes();
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		Log.i("!!!", "encript start");
		byte[] enc = mRsa.EncryptBuffer(data);
		Log.i("!!!", "encrypt finish");
		
		Log.i("!!!", "decrypt start");
		byte[] dec = mRsa.DecryptBuffer(mKey, enc);
		Log.i("!!!", "decrypt finish");
		
		String newtxt = new String(dec);
		assertEquals(text, newtxt);
	}
	
	public void testEncDecSuccess100K() throws Exception {
		int len = 100*1024;
		byte[] data = new byte[len];
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		Log.i("!!!", "encript start");
		byte[] enc = mRsa.EncryptBuffer(data);
		Log.i("!!!", "encrypt finish");
		
		Log.i("!!!", "decrypt start");
		byte[] dec = mRsa.DecryptBuffer(mKey, enc);
		Log.i("!!!", "decrypt finish");
		
		for (int i=0; i<len; i++)
			assertEquals(data[i], dec[i]);
	}
	
	public void testEncDecSuccess100() throws Exception {
		String text = "Привет мир";
		byte[] data = text.getBytes();
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		for (int i=0; i<100; i++) {
			Log.i("!!!", "encript start");
			byte[] enc = mRsa.EncryptBuffer(data);
			Log.i("!!!", "encrypt finish");
			
			String encStr = new String(enc, "ISO_8859_1");
			
			byte[] forDec = encStr.getBytes("ISO_8859_1");
			
			Log.i("!!!", "decrypt start");
			byte[] dec = mRsa.DecryptBuffer(mKey, forDec);
			Log.i("!!!", "decrypt finish");
			
			String newtxt = new String(dec);
			assertEquals(text, newtxt);
		}
	}
	
	public void testEncDecWrongKey() throws Exception {
		String text = "hello world";
		byte[] data = text.getBytes();
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		Log.i("!!!", "encript start");
		byte[] enc = mRsa.EncryptBuffer(data);
		Log.i("!!!", "encrypt finish");
		
		byte[] dec = new byte[0];
		MyException exp=null;
		
		Log.i("!!!", "decrypt start");
		try {
			BigInteger invalidKey = new BigInteger(128, new Random());
			dec = mRsa.DecryptBuffer(invalidKey, enc);
		} catch(MyException e) {
			exp=e;
		}
		Log.i("!!!", "decrypt finish");
		
		assertNotNull(exp);
		assertEquals(TTypMyException.ERsaErrDecrypt, exp.getId());
		
		String newtxt = new String(dec);
		assertFalse(text.equals(newtxt));
	}
}
