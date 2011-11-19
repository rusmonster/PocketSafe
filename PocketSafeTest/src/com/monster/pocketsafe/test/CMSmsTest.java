package com.monster.pocketsafe.test;

import com.monster.pocketsafe.dbengine.CMSms;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import junit.framework.TestCase;

public class CMSmsTest extends TestCase {

	private CMSms mSms;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mSms = new CMSms();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSetPhoneSuccess() throws MyException
	{
		mSms.setPhone("+123");
		assertEquals(mSms.getPhone(), "+123");
		
		mSms.setPhone("123");
		assertEquals(mSms.getPhone(), "123");
	}
	
	public void testSetPhoneErrFormat() {
		MyException ex=null;
		try {
			mSms.setPhone("+xxx");
		} catch (MyException e) {
			ex=e;
		}
		assertNotNull(ex);
		assertTrue(ex.getId() == TTypMyException.EPhoneInvalid);
		
		ex=null;
		try {
			mSms.setPhone("-333");
		} catch (MyException e) {
			ex=e;
		}
		assertNotNull(ex);
		assertTrue(ex.getId() == TTypMyException.EPhoneInvalid);

		ex=null;
		try {
			mSms.setPhone("+");
		} catch (MyException e) {
			ex=e;
		}
		assertNotNull(ex);
		assertTrue(ex.getId() == TTypMyException.EPhoneInvalid);

	}
	
	public void testSetPhoneErrTooLong() {
		MyException ex=null;
		String str = new String();
		for (int i=0; i<51; i++)
			str += Character.toString((char)(0x30+i%10));
		
		try {
			mSms.setPhone(str);
		} catch (MyException e) {
			ex=e;
		}
		assertNotNull(ex);
		assertTrue(ex.getId() == TTypMyException.EPhoneTooLong);
	}
	
}
