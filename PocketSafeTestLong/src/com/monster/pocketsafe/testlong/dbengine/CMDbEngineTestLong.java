package com.monster.pocketsafe.testlong.dbengine;

import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class CMDbEngineTestLong extends TestCase {

	private IMLocator mLocator;
	private CMDbEngine mDbEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mLocator = new CMLocator();
		mDbEngine = new CMDbEngine(mLocator);
		
		mDbEngine.Open("mydb1.db");
	}

	protected void tearDown() throws Exception {
		mDbEngine.Close();
		super.tearDown();
	}

	public void testOpenClose() throws MyException {
		mDbEngine.Close();
		mDbEngine.Open("mydb1.db");
		mDbEngine.Close();
		mDbEngine.Open("mydb1.db");
		mDbEngine.Close(); 
	}
	
	public void testSettingInsert() throws MyException {
		int id = TTypSetting.EDbPassTimout.ordinal();
		IMSetting set = mLocator.createSetting();
		set.setId(id);
		set.setIntVal(250);
		mDbEngine.TableSetting().Update(set);
		
		IMSetting dest = mLocator.createSetting();
		mDbEngine.TableSetting().getById(dest, TTypSetting.EDbPassTimout);
		
		assertEquals(id, dest.getId());
		assertEquals(250, dest.getIntVal());
		
		set.setIntVal(300);
		mDbEngine.TableSetting().Update(set);
		mDbEngine.TableSetting().getById(dest, TTypSetting.EDbPassTimout);
		
		assertEquals(id, dest.getId());
		assertEquals(300, dest.getIntVal());
	}
}
