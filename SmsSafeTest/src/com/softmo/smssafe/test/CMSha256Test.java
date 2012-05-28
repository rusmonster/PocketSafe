package com.softmo.smssafe.test;

import android.util.Log;

import com.softmo.libsafe.sec.CMSha256;
import com.softmo.libsafe.utils.MyException;
import com.softmo.libsafe.utils.MyException.TTypMyException;

import junit.framework.TestCase;

public class CMSha256Test extends TestCase {
	
	private CMSha256 mSha;

	protected void setUp() throws Exception {
		super.setUp();
		mSha = new CMSha256();
	}
	
	public void testSame() throws MyException {
		String phone1 = "+123456789";
		
		String sha1 = mSha.getHash(phone1);
		String sha2 = mSha.getHash(phone1);
		
		assertEquals(sha1, sha2);
		Log.d("!!!", "testSame: "+sha1);
	}
	
	public void testDiff() throws MyException {
		String phone1 = "+123456789";
		String phone2 = "+123456788";
		
		String sha1 = mSha.getHash(phone1);
		String sha2 = mSha.getHash(phone2);
		
		assertFalse( sha1.equals(sha2) );
		
		Log.d("!!!", "testDiff1: "+sha1);
		Log.d("!!!", "testDiff2: "+sha2);
	}
	
	public void testZero() throws MyException {
		
		String sha1 = mSha.getHash("");

		assertNotNull(sha1);
		Log.d("!!!", "testZero: "+sha1);
		Log.d("!!!", "Len: : "+sha1.length());
	}
	
	public void testNull() throws MyException {

		MyException exp=null;
		try {
			mSha.getHash(null);
		} catch (MyException e) {
			exp=e;
		}

		assertNotNull(exp);
		assertEquals(TTypMyException.EErrSha256NullArgument, exp.getId());
	}

}
