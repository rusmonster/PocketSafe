package com.softmo.smssafe2.test;

import com.softmo.smssafe2.dbengine.CMSms;
import com.softmo.smssafe2.utils.MyException;
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
