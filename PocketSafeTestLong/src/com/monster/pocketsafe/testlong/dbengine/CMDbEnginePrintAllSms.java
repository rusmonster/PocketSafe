package com.monster.pocketsafe.testlong.dbengine;

import java.util.ArrayList;

import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;

import android.test.AndroidTestCase;
import android.util.Log;

public class CMDbEnginePrintAllSms extends AndroidTestCase {
	private IMLocator mLocator;
	private CMDbEngine mDbEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mLocator = new CMLocator();
		mDbEngine = new CMDbEngine(mLocator);
		mDbEngine.Open(getContext().getContentResolver());
	}
	
	public void testPrint() throws MyException {
		Log.d("!!!","Starting testPrint");
		
		ArrayList<IMSms> res = new ArrayList<IMSms>();
		int k=0;
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EInbox, k, 1000);
		while (res.size()>0) {
			for (int i=0; i<res.size(); i++) {
				IMSms sms = res.get(i);
				Log.d("!!!", "FROM: "+sms.getPhone()+";TEXT: "+sms.getText());
			}
			k+=1000;
			mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EInbox, k, 1000);
		}
		
	}
}