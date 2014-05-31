package com.softmo.smssafe2.testlong.rsa;

import android.util.Log;
import com.softmo.smssafe2.sec.CMRsa;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.sec.IMRsaObserver;
import com.softmo.smssafe2.testlong.utils.CMTestThread;
import com.softmo.smssafe2.utils.CMLocator;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;
import junit.framework.TestCase;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
		
		Random random = new Random();
		for (int j=0; j<len; j++)
			data[j] = (byte) (random.nextInt() & 0xFF);
		
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


	private AtomicInteger mThreadOk = new AtomicInteger(0);

	private class ThreadRunnable implements Runnable {

        private int mId;
        private byte[] mData;

        public ThreadRunnable(byte[] data, int id) {
            mData = data;
            mId = id;
        }

        @Override
        public void run() {
            Boolean res = Boolean.FALSE;
            Log.d("!!!", "Thread: "+mId+"; mId: "+mId);

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
                Log.i("!!!", "task finish" + mId);
            } catch (MyException e) {
                Log.e("!!!", "Thread: "+mId+"; MyException: "+e.getId());
            } catch (Exception e) {
                Log.e("!!!", "Thread: "+mId+"; Exception: "+e.getMessage());
            }

            Log.i("!!!", "task return" + mId);
            if (res)
                mThreadOk.incrementAndGet();
        }
    };

	public void testEncDec100Thread() throws Exception {
		Log.i("!!!", "generate start");
		testGenerateKeyPair();
		Log.i("!!!", "generate finish");
		
		int cnt=100;
		
		mThreadOk.set(0);
		
		Date dat = new Date();
		Random random = new Random( dat.getTime() );

        List<Thread> threads = new ArrayList<Thread>(cnt);

		for (int i=0; i<cnt; i++) {
			int rnd = Math.abs(random.nextInt())%1000;
			Log.i("!!!", "rnd="+rnd);
			
			byte[] data = new byte[rnd];
			for (int j=0; j<rnd; j++)
				data[j] = (byte) (random.nextInt() & 0xFF);
			
			Thread thread = new Thread(new ThreadRunnable(data, i));
            threads.add(thread);
            thread.start();
		}

        for (Thread thread : threads) {
            thread.join();
        }
		
		Log.d("!!!", "after loop: ");

		assertEquals(cnt, mThreadOk.get());
	}

}
