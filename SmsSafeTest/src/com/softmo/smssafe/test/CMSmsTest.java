package com.softmo.smssafe.test;

import com.softmo.smssafe.dbengine.CMSms;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

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
	
}
