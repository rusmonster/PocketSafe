package com.softmo.smssafe2.test;

import android.util.Log;

import com.softmo.smssafe2.sec.CMBase64;
import com.softmo.smssafe2.utils.MyException;

import junit.framework.TestCase;

public class CMBase64Test extends TestCase {

	private CMBase64 mBase64;
	
	protected void setUp() throws Exception {
		super.setUp();
		mBase64 = new CMBase64();
	}

	public void testEncDecEmpty() throws MyException {
		String text = "";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess1() throws MyException {
		String text = "1";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess2() throws MyException {
		String text = "12";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess3() throws MyException {
		String text = "123";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess4() throws MyException {
		String text = "1234";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess5() throws MyException {
		String text = "12345";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDecSuccess6() throws MyException {
		String text = "123456";
		Log.i("!!!", "text: "+text);
		
		byte[] enc = mBase64.encode(text.getBytes());
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		
		assertEquals(text, res);
	}
	
	public void testEncDec300K() throws MyException {
		byte[] src = new byte[300*1024];
		for (int i=0; i<src.length; i++)
			src[i] = (byte) (i&0xFF);
		
		byte[] enc = mBase64.encode(src);
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		for (int i=0; i<src.length; i++)
			assertEquals(src[i], dec[i]);
	}
	
	public void testEncDec() throws MyException {
		byte[] src = "������".getBytes();
		
		byte[] enc = mBase64.encode(src);
		Log.i("!!!", "encoded: "+new String(enc));
		
		byte[] dec = mBase64.decode(enc);
		String res = new String(dec);
		Log.i("!!!", "decoded: "+res);
		
		for (int i=0; i<src.length; i++)
			assertEquals(src[i], dec[i]);
	}
}
