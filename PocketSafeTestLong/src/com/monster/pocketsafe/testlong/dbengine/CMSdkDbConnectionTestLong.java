package com.monster.pocketsafe.testlong.dbengine;

import java.io.File;

import android.util.Log;

import com.monster.pocketsafe.dbengine.CMSdkDbConnection;
import com.monster.pocketsafe.dbengine.IMSdkDbConection;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import junit.framework.TestCase;

public class CMSdkDbConnectionTestLong extends TestCase {
	
	private IMSdkDbConection mConn;
	private static final String mDbFName = "testdb";

	protected void setUp() throws Exception {
		super.setUp();
		
		File file = new File(mDbFName);
		file.delete();
		
		mConn = new CMSdkDbConnection();
		mConn.Open(mDbFName);
		mConn.ExecSQL("DROP TABLE IF EXISTS TESTTABLE");
		mConn.ExecSQL("CREATE TABLE TESTTABLE(ID INTEGER PRIMARY KEY AUTOINCREMENT, VAL VARCHAR(50))");
	}

	protected void tearDown() throws Exception {
		mConn.Close();
		
		File file = new File(mDbFName);
		file.delete();
		
		super.tearDown();
	}

	public void testGetLastInsertIDSuccess() throws MyException {
		mConn.ExecSQL("INSERT INTO TESTTABLE (VAL) VALUES('xxx')");
		int id = mConn.getLastInsertID();
		Log.v("!!!", "ID1 = "+id);
		assertEquals(1,id);
		
		mConn.ExecSQL("INSERT INTO TESTTABLE (VAL) VALUES('xxx')");
		id = mConn.getLastInsertID();
		Log.v("!!!", "ID2 = "+id);
		assertEquals(2,id);
	}
	
	public void testGetLastInsertIDFail() throws MyException {
		
		TTypMyException err = TTypMyException.ENoError;
		try {
			mConn.getLastInsertID();
		} catch (MyException e) {
			err = e.getId();
		}
		
		assertEquals( TTypMyException.EDbErrorGetLastID, err );
	}


}
