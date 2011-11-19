package com.monster.pocketsafe.testlong;

import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import junit.framework.TestCase;

public class CMDbEngineTestLong extends TestCase {

	private IMLocator mLocator;
	private CMDbEngine mDbEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mLocator = new CMLocator();
		mDbEngine = new CMDbEngine(mLocator);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		mDbEngine.Close();
	}

	public void testOpenClose() throws MyException {
		mDbEngine.Open("mydb1.db");
		mDbEngine.Close();
		mDbEngine.Open("mydb1.db");
		mDbEngine.Close(); //
	}
}
