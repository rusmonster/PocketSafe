package com.monster.pocketsafe.test;

import android.util.Log;

import com.monster.pocketsafe.sec.CMAes;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import junit.framework.TestCase;

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
		
		Log.i("!!!", "clear: "+clear);
		assertEquals(text, clear);
	}

}
