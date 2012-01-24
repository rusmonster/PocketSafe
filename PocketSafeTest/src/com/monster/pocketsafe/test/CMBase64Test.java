package com.monster.pocketsafe.test;

import android.util.Log;

import com.monster.pocketsafe.rsa.CMBase64;

import junit.framework.TestCase;

public class CMBase64Test extends TestCase {

	private CMBase64 mBase64;
	
	protected void setUp() throws Exception {
		super.setUp();
		mBase64 = new CMBase64();
	}

	public void testEncDecSuccess() {
		String text = "Hello world";
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		byte[] dec = mBase64.decode(enc);
		
		String res = new String(dec);
		
		
		assertEquals(text, res);
	}
}
