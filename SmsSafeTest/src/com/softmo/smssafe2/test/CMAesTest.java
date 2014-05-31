package com.softmo.smssafe2.test;

import android.os.SystemClock;
import android.util.Log;

import com.softmo.smssafe2.sec.CMAes;
import com.softmo.smssafe2.sec.CMBase64;
import com.softmo.smssafe2.utils.CMLocator;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import junit.framework.TestCase;

import java.security.SecureRandom;

public class CMAesTest extends TestCase {

	private CMAes mAes;
	
	protected void setUp() throws Exception {
		super.setUp();
		CMLocator loc = new CMLocator();
		mAes = new CMAes(loc);
	}
	
	public void testEncDec() throws Exception {
		String pass = "12345";
		String text = "Hello world";
		
		String crypt = mAes.encrypt(pass, text);
		Log.i("!!!", "crypt: "+crypt);
		String clear = mAes.decrypt(pass, crypt);
		
		assertEquals(text, clear);
	}
	
	public void testEncDecWrongPass() throws Exception {
		String pass = "12345";
		String text = "Hello world";
		
		String crypt = mAes.encrypt(pass, text);
		
		MyException exp=null;
		try {
			mAes.decrypt("1111", crypt);
		}catch(MyException e) {
			exp=e;
		}
		
		assertNotNull(exp);
		assertEquals(TTypMyException.EAesErrDecrypt, exp.getId());
	}
	
	public void testEncDecRus() throws Exception {
		String pass = "12345";
		String text = "Привет мир";
		
		String crypt = mAes.encrypt(pass, text);
		String clear = mAes.decrypt(pass, crypt);

		Log.i("!!!", "crypt: "+crypt);
		Log.i("!!!", "clear: "+clear);
		assertEquals(text, clear);
	}

	public void testEncDec1000() throws Exception {
		int cnt = 50;
		String[] pass = new String[cnt];
		String[] text = new String[cnt];
		String[] coded = new String[cnt];

		SecureRandom sr = new SecureRandom();
		CMBase64 base64 = new CMBase64();

		byte[] dat = new byte[2048 / 8];

		Log.i("!!!", "generating... ");
		long tim = SystemClock.elapsedRealtime();
		for (int i = 0; i < cnt; i++) {
			sr.nextBytes(dat);
			pass[i] = new String(base64.encode(dat)).substring(0, 8);

			sr.nextBytes(dat);
			text[i] = new String(base64.encode(dat));
		}
		tim = SystemClock.elapsedRealtime() - tim;
		Log.i("!!!", "generating done all: " + tim + "; avg: " + String.valueOf(tim / cnt));

		Log.i("!!!", "encrypting... ");
		tim = SystemClock.elapsedRealtime();
		for (int i = 0; i < cnt; i++) {
			coded[i] = mAes.encrypt(pass[i], text[i]);
		}
		tim = SystemClock.elapsedRealtime() - tim;
		Log.i("!!!", "encrypting done all: " + tim + "; avg: " + String.valueOf(tim / cnt));

		Log.i("!!!", "decrypting... ");
		tim = SystemClock.elapsedRealtime();
		for (int i = 0; i < cnt; i++) {
			coded[i] = mAes.decrypt(pass[i], coded[i]);
		}
		tim = SystemClock.elapsedRealtime() - tim;
		Log.i("!!!", "decrypting done all: " + tim + "; avg: " + String.valueOf(tim / cnt));


		Log.i("!!!", "verifying... ");
		tim = SystemClock.elapsedRealtime();
		for (int i = 0; i < cnt; i++) {
			assertEquals(text[i], coded[i]);
		}
		tim = SystemClock.elapsedRealtime() - tim;
		Log.i("!!!", "verifying done all: " + tim + "; avg: " + String.valueOf(tim / cnt));
	}
}
