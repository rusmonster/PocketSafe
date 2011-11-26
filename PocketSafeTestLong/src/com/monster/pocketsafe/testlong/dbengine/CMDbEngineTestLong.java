package com.monster.pocketsafe.testlong.dbengine;

import java.util.ArrayList;
import java.util.Date;

import android.test.AndroidTestCase;
import android.util.Log;

import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;


public class CMDbEngineTestLong extends AndroidTestCase {

	private IMLocator mLocator;
	private CMDbEngine mDbEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mLocator = new CMLocator();
		mDbEngine = new CMDbEngine(mLocator);
		
		
		mDbEngine.Open(getContext().getContentResolver());
		mDbEngine.TableSms().Clear();
		int cnt = mDbEngine.TableSms().getCount();
		assertEquals(0, cnt);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSettingInsert() throws MyException {
		int id = TTypSetting.EDbPassTimout.ordinal();
		
		IMSetting orig = mLocator.createSetting();
		mDbEngine.TableSetting().getById(orig, TTypSetting.EDbPassTimout);
		
		IMSetting set = mLocator.createSetting();
		set.setId(id);
		set.setIntVal(orig.getIntVal()+50);
		mDbEngine.TableSetting().Update(set);
		
		IMSetting dest = mLocator.createSetting();
		mDbEngine.TableSetting().getById(dest, TTypSetting.EDbPassTimout);
		
		assertEquals(id, dest.getId());
		assertEquals(set.getIntVal(), dest.getIntVal());
		

		mDbEngine.TableSetting().Update(orig);
		mDbEngine.TableSetting().getById(dest, TTypSetting.EDbPassTimout);
		
		assertEquals(orig.getId(), dest.getId());
		assertEquals(orig.getIntVal(), dest.getIntVal());
	}
	
	public void testSettingGetFailed() {
		IMSetting set = mLocator.createSetting();
		TTypMyException err = TTypMyException.ENoError;
		try {
			mDbEngine.TableSetting().getById(set, TTypSetting.EDbZeroSetting);
		} catch (MyException e) {
			err = e.getId();
		}
		
		assertEquals(TTypMyException.EDbIdNotFoundSetting, err);
	}
	
	public void testSmsInsert() throws MyException {
		Date dat = new Date();
		
		IMSms src = mLocator.createSms();
		src.setDate(dat);
		src.setDirection(TTypDirection.EIncoming);
		src.setFolder(TTypFolder.EOutbox);
		src.setIsNew(TTypIsNew.Enew);
		src.setPhone("1234567");
		src.setText("hellow world");
		
		int id = mDbEngine.TableSms().Insert(src);
		
		IMSms dest = mLocator.createSms();
		mDbEngine.TableSms().getById(dest, id);
		
		assertEquals(id, dest.getId());
		assertEquals(src.getDate(), dest.getDate());
		assertEquals(src.getDirection(), dest.getDirection());
		assertEquals(src.getFolder(), dest.getFolder());
		assertEquals(src.getIsNew(), dest.getIsNew());
		assertEquals(src.getPhone(), dest.getPhone());
		assertEquals(src.getText(), dest.getText());
	}
	
	public void testSmsGetFailed() {
		IMSms item = mLocator.createSms();
		TTypMyException err = TTypMyException.ENoError;
		try {
			mDbEngine.TableSms().getById(item, -1);
		} catch (MyException e) {
			err = e.getId();
		}
		
		assertEquals(TTypMyException.EDbIdNotFoundSms, err);
	}
	
	public void testSmsQueryByFolderOrderByDatDesc() throws MyException {
		Date dat = new Date();
		
		IMSms sms  = null;

		
		ArrayList<IMSms> list = new ArrayList<IMSms>();
		ArrayList<IMSms> res = new ArrayList<IMSms>();
		
		for (int i=0; i<5; i++) {
			sms  = mLocator.createSms();
			sms.setDate(dat);
			sms.setDirection(TTypDirection.EIncoming);
			sms.setFolder(TTypFolder.Einbox);
			sms.setIsNew(TTypIsNew.Enew);
			sms.setText("hellow world");
			sms.setPhone("1234567"+i);
			int id = mDbEngine.TableSms().Insert(sms);
			sms.setId(id);
			list.add(sms);
			
			dat = new Date(dat.getTime()+1);
			Log.v("!!!", "SMSID="+sms.getId() );
		}
		
		assertEquals(mDbEngine.TableSms().getCount(),list.size());
		
		sms = mLocator.createSms();
		sms.setDate(dat);
		sms.setDirection(TTypDirection.EOutgoing);
		sms.setFolder(TTypFolder.EOutbox);
		sms.setIsNew(TTypIsNew.EReaded);
		sms.setText("sended sms");
		sms.setPhone("12345679");
		int id = mDbEngine.TableSms().Insert(sms);
		sms.setId(id);
		
		
		int k=list.size()-1;
		
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.Einbox, 0, 3);
		assertEquals(3,res.size());
		
		for (int i=0; i<3; i++) {
			IMSms src = list.get(k--);
			IMSms dest = res.get(i);
			
			assertEquals(src.getId(), dest.getId());
			assertEquals(src.getDate(), dest.getDate());
			assertEquals(src.getDirection(), dest.getDirection());
			assertEquals(src.getFolder(), dest.getFolder());
			assertEquals(src.getIsNew(), dest.getIsNew());
			assertEquals(src.getPhone(), dest.getPhone());
			assertEquals(src.getText(), dest.getText());
		}
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.Einbox, 3, 3);
		assertEquals(2,res.size());
		
		for (int i=0; i<2; i++) {
			IMSms src = list.get(k--);
			IMSms dest = res.get(i);
			
			assertEquals(src.getId(), dest.getId());
			assertEquals(src.getDate(), dest.getDate());
			assertEquals(src.getDirection(), dest.getDirection());
			assertEquals(src.getFolder(), dest.getFolder());
			assertEquals(src.getIsNew(), dest.getIsNew());
			assertEquals(src.getPhone(), dest.getPhone());
			assertEquals(src.getText(), dest.getText());
		}
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.Einbox, 6, 3);
		assertEquals(0,res.size());
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EOutbox, 0, 3);
		assertEquals(1,res.size());
		IMSms dest = res.get(0);
		
		assertEquals(sms.getId(), dest.getId());
		assertEquals(sms.getDate(), dest.getDate());
		assertEquals(sms.getDirection(), dest.getDirection());
		assertEquals(sms.getFolder(), dest.getFolder());
		assertEquals(sms.getIsNew(), dest.getIsNew());
		assertEquals(sms.getPhone(), dest.getPhone());
		assertEquals(sms.getText(), dest.getText());
	
	}
}
