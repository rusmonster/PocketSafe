package com.monster.pocketsafe.testlong.rsa;

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
	private boolean mKeyGenerated;
		
	private IMRsaObserver mObserver = new IMRsaObserver() {
		
		@Override
		public void RsaKeyPairGenerated(IMRsa _sender) throws Exception {
			mKeyGenerated=true;
			Log.i("!!!", "RsaKeyPairGenerated");
		}
		
		@Override
		public void RsaKeyPairGenerateError(IMRsa _sender, int _err) throws Exception {
			Log.i("!!!", "RsaKeyPairGenerateError");
		}
	};
	
	protected void setUp() throws Exception {
		super.setUp();
		mKeyGenerated=false;
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
		while (!mKeyGenerated && k++<30)
			Thread.sleep(1000);
		th.stopThread();
		
		if (!mKeyGenerated)
			fail("Generate failed");
		
		String priv = mRsa.getPrivateKey();
		String pub = mRsa.getPublicKey();
		
		assertNotNull(priv);
		assertNotNull(pub);
		
		assertTrue(priv.length()>0);
		assertTrue(pub.length()>0);
	}
	
	public void testSetPublicKey() throws Exception {
		testGenerateKeyPair();
		
		String priv = mRsa.getPrivateKey();
		Log.i("!!!", "priv="+priv);
		
		mRsa.setPublicKey(priv);
		String pub = mRsa.getPublicKey();
		Log.i("!!!", "pub="+pub);
		
		assertEquals(priv, pub);
	}
	

	public void testSetPrivateKey() throws Exception {
		testGenerateKeyPair();
		
		String pub = mRsa.getPublicKey();
		
		mRsa.setPrivateKey(pub);
		String priv = mRsa.getPrivateKey();
		
		assertEquals(pub, priv);
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
		byte[] dec = mRsa.DecryptBuffer(enc);
		Log.i("!!!", "decrypt finish");
		
		String newtxt = new String(dec);
		assertEquals(text, newtxt);
	}
	
	public void testEncDecWrongKey() throws Exception {
		String text = "hello world";
		byte[] data = text.getBytes();
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		mRsa.setPrivateKey(mRsa.getPublicKey());
		
		Log.i("!!!", "encript start");
		byte[] enc = mRsa.EncryptBuffer(data);
		Log.i("!!!", "encrypt finish");
		
		byte[] dec = new byte[0];
		MyException exp=null;
		
		Log.i("!!!", "decrypt start");
		try {
			dec = mRsa.DecryptBuffer(enc);
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
