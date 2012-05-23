package com.monster.pocketsafe.testlong.rsa;

import java.math.BigInteger;
import java.util.Date;
import java.util.Random;

import android.os.AsyncTask;
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
		
		assertEquals(len, dec.length);
		
		for (int i=0; i<len; i++)
			assertEquals(data[i], dec[i]);
	}
	
	/*
	public void testEncDecFindLimit() throws Exception {
		
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		int len=0;
		try {
			while(++len<1000) {
				Log.d("!!!", "len="+len);
				byte[] data = new byte[len];
				Log.i("!!!", "encript start");
				byte[] enc = mRsa.EncryptBuffer(data);
				Log.i("!!!", "encrypt finish");
				
				Log.i("!!!", "decrypt start");
				byte[] dec = mRsa.DecryptBuffer(mKey, enc);
				Log.i("!!!", "decrypt finish");
				
				assertEquals(len, dec.length);
				
				for (int i=0; i<len; i++)
					assertEquals(data[i], dec[i]);
			}
		} catch (MyException e) {
			Log.i("!!!", "Fail limit: "+len+"; with: "+e.getId()); //returns 246 => 245 id last succes value
		}
	}
	*/
	
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


	private int mThreadOk;
	private int mThreadFinished;
	
	private class EncDecThread extends AsyncTask<Void, Void, Boolean> {

		private int mId;
		private byte[] mData;
		
		public EncDecThread(byte[] data, int id) {
			mData = data;
			mId = id;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Boolean res = Boolean.FALSE;
			try {
				Log.i("!!!", "encript start" + mId);
				byte[] enc = mRsa.EncryptBuffer(mData);
				Log.i("!!!", "encrypt finish" + mId);
				
				Log.i("!!!", "decrypt start" + mId);
				byte[] dec = mRsa.DecryptBuffer(mKey, enc);
				Log.i("!!!", "decrypt finish" + mId);
				
				if (mData.length != dec.length)
					throw new RuntimeException("threadId: "+mId+": Assert len failed");
				
				for (int i=0; i<mData.length; i++)
					if (mData[i] != dec[i])
						throw new RuntimeException("threadId: "+mId+": Assert data failed i="+i);
				
				res = Boolean.TRUE;
			} catch (MyException e) {
				Log.e("!!!", "Thread: "+mId+"; MyException: "+e.getId());
			} catch (Exception e) {
				Log.e("!!!", "Thread: "+mId+"; Exception: "+e.getMessage());
			}
			return res;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mThreadFinished++;
			
			if (result)
				mThreadOk++;
			
			super.onPostExecute(result);
		}
		
		
		
	}
	public void testEncDec100Thread() throws Exception {
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		int cnt=20;
		
		mThreadFinished = 0;
		mThreadOk = 0;
		
		Date dat = new Date();
		Random random = new Random( dat.getTime() );
		
		for (int i=0; i<cnt; i++) {
			int rnd = Math.abs(random.nextInt())%500;
			Log.i("!!!", "rnd="+rnd);
			
			byte[] data = new byte[rnd];
			for (int j=0; j<rnd; j++)
				data[j] = (byte) (random.nextInt()&0xFF);
			
			(new EncDecThread(data, i)).execute();
		}

		int n=0;
		while (mThreadFinished<cnt && n++ <60)
		{
			Thread.sleep(1000);
			Log.d("!!!", "n="+n);
		}
		
		Log.d("!!!", "after while: "+n);
		
		assertEquals(cnt, mThreadFinished);
		assertEquals(cnt, mThreadOk);
	}

}
