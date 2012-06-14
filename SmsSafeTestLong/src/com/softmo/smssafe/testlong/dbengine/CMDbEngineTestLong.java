package com.softmo.smssafe.testlong.dbengine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.test.AndroidTestCase;
import android.util.Log;

import com.softmo.smssafe.dbengine.CMDbEngine;
import com.softmo.smssafe.dbengine.IMContact;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.IMSmsGroup;
import com.softmo.smssafe.dbengine.TTypDirection;
import com.softmo.smssafe.dbengine.TTypFolder;
import com.softmo.smssafe.dbengine.TTypIsNew;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.dbengine.TTypStatus;
import com.softmo.smssafe.sec.IMSha256;
import com.softmo.smssafe.utils.CMLocator;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;


public class CMDbEngineTestLong extends AndroidTestCase {

	private IMLocator mLocator;
	private CMDbEngine mDbEngine;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mLocator = new CMLocator();
		mDbEngine = new CMDbEngine(mLocator);
		
		
		mDbEngine.Open( getContext() );
		mDbEngine.TableSms().Clear();
		int cnt = mDbEngine.TableSms().getCount();
		assertEquals(0, cnt);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSettingInsert() throws MyException {
		int id = TTypSetting.EPassTimout.ordinal();
		
		IMSetting orig = mLocator.createSetting();
		mDbEngine.TableSetting().getById(orig, TTypSetting.EPassTimout);
		
		IMSetting set = mLocator.createSetting();
		set.setId(id);
		set.setIntVal(orig.getIntVal()+50);
		mDbEngine.TableSetting().Update(set);
		
		IMSetting dest = mLocator.createSetting();
		mDbEngine.TableSetting().getById(dest, TTypSetting.EPassTimout);
		
		assertEquals(id, dest.getId());
		assertEquals(set.getIntVal(), dest.getIntVal());
		

		mDbEngine.TableSetting().Update(orig);
		mDbEngine.TableSetting().getById(dest, TTypSetting.EPassTimout);
		
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
		src.setIsNew(TTypIsNew.ENew);
		src.setPhone("1234567");
		src.setText("hellow world");
		src.setHash("myHash");
		src.setStatus(TTypStatus.ERecv);
		src.setSmsId(-1);
		
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
		assertEquals(src.getStatus(), dest.getStatus());
		assertEquals(src.getSmsId(), dest.getSmsId());
		assertEquals(src.getHash(), dest.getHash());
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
		IMSha256 sha = mLocator.createSha256();

		
		ArrayList<IMSms> list = new ArrayList<IMSms>();
		ArrayList<IMSms> res = new ArrayList<IMSms>();
		
		for (int i=0; i<5; i++) {
			sms  = mLocator.createSms();
			sms.setDate(dat);
			sms.setDirection(TTypDirection.EIncoming);
			sms.setFolder(TTypFolder.EInbox);
			sms.setIsNew(TTypIsNew.ENew);
			sms.setText("hellow world");
			sms.setPhone("1234567"+i);
			sms.setHash( sha.getHash(sms.getPhone()) );
			int id = mDbEngine.TableSms().Insert(sms);
			sms.setId(id);
			list.add(sms);
			
			dat = new Date(dat.getTime()+1);
			//Log.v("!!!", "SMSID="+sms.getId() );
		}
		
		assertEquals(mDbEngine.TableSms().getCount(),list.size());
		
		sms = mLocator.createSms();
		sms.setDate(dat);
		sms.setDirection(TTypDirection.EOutgoing);
		sms.setFolder(TTypFolder.EOutbox);
		sms.setIsNew(TTypIsNew.EOld);
		sms.setText("sended sms");
		sms.setPhone("12345679");
		sms.setHash( sha.getHash(sms.getPhone()) );
		int id = mDbEngine.TableSms().Insert(sms);
		sms.setId(id);
		
		
		int k=list.size()-1;
		
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EInbox, 0, 3);
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
			assertEquals(src.getHash(), dest.getHash());
			assertEquals(src.getText(), dest.getText());
		}
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EInbox, 3, 3);
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
			assertEquals(src.getHash(), dest.getHash());
			assertEquals(src.getText(), dest.getText());
		}
		
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.EInbox, 6, 3);
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
		assertEquals(sms.getHash(), dest.getHash());
		assertEquals(sms.getText(), dest.getText());
	
	}
	
	public void testSmsQueryGroupByHashOrderByMaxDatDesc() throws MyException {
		Date dat = new Date();
		
		IMSms sms  = null;
		IMSha256 sha = mLocator.createSha256();

		
		ArrayList<IMSms> list = new ArrayList<IMSms>();
		ArrayList<IMSmsGroup> res = new ArrayList<IMSmsGroup>();
		
		for (int i=0; i<5; i++) {
			sms  = mLocator.createSms();
			sms.setDate(dat);
			sms.setDirection(TTypDirection.EIncoming);
			sms.setFolder(TTypFolder.EInbox);
			sms.setIsNew(TTypIsNew.EOld);
			sms.setText("hellow world");
			sms.setPhone("1234567"+i);
			sms.setHash(sha.getHash(sms.getPhone()));
			int id = mDbEngine.TableSms().Insert(sms);
			sms.setId(id);
			list.add(sms);
			
			dat = new Date(dat.getTime()+1);
			//Log.v("!!!", "SMSID="+sms.getId() );
		}
		
		for (int i=0; i<3; i++) {
			sms  = mLocator.createSms();
			sms.setDate(dat);
			sms.setDirection(TTypDirection.EIncoming);
			sms.setFolder(TTypFolder.EInbox);
			sms.setIsNew(TTypIsNew.ENew);
			sms.setText("hellow world");
			sms.setPhone("1234567"+i);
			sms.setHash(sha.getHash(sms.getPhone()));
			int id = mDbEngine.TableSms().Insert(sms);
			sms.setId(id);
			list.add(sms);
			
			dat = new Date(dat.getTime()+1);
			//Log.v("!!!", "SMSID="+sms.getId() );
		}
		
		assertEquals(mDbEngine.TableSms().getCount(),list.size());
		
		ArrayList<IMSmsGroup> temp = new ArrayList<IMSmsGroup>();
		for (int i=0; i<5; i++) {
			IMSmsGroup gr = mLocator.createSmsGroup();
			gr.setPhone("1234567"+i);
			gr.setHash(sha.getHash(gr.getPhone()));
			
			int cnt=0;
			int cntnew=0;
			Date maxdat = new Date(0);
			for (int j=0; j<list.size(); j++) {
				sms = list.get(j);
				if (sms.getPhone().compareTo(gr.getPhone()) == 0) {
					cnt++;
					if (sms.getIsNew()>=TTypIsNew.ENew)
						cntnew++;
					if (sms.getDate().getTime()>maxdat.getTime())
						maxdat.setTime( sms.getDate().getTime() );
				}
			}
			
			gr.setCount(cnt);
			gr.setCountNew(cntnew);
			gr.setDate(maxdat);
			
			temp.add(gr);
		}
		
		Comparator<IMSmsGroup> comperator = new Comparator<IMSmsGroup>() {
			@Override
			public int compare(IMSmsGroup object1, IMSmsGroup object2) {
			
			return -1*object1.getDate().compareTo(object2.getDate());
			}
			};
		Collections.sort(temp, comperator);
		
		/*
		for (int i=0; i<temp.size(); i++) {
			IMSmsGroup dest = temp.get(i);
			Log.d("!!!", "I="+i+"; PHONE: "+dest.getPhone()+"; COUNT: "+dest.getCount()+"; NEW: "+dest.getCountNew()+"; DAT: "+dest.getDate().getTime());
		}
		*/
		//Log.v("!!!", "Checking...");
		
		mDbEngine.TableSms().QueryGroupByHashOrderByMaxDatDesc(res, 0, 3);
		assertEquals(3,res.size());
		
		for (int i=0; i<3; i++) {
			IMSmsGroup src = temp.get(i);
			IMSmsGroup dest = res.get(i);
			
			//Log.d("!!!", "I="+i+"; PHONE: "+dest.getPhone()+"; COUNT: "+dest.getCount()+"; NEW: "+dest.getCountNew()+"; DAT: "+dest.getDate().getTime());
			assertEquals(src.getHash(), dest.getHash());
			assertEquals(src.getPhone(), dest.getPhone());
			assertEquals(src.getCount(), dest.getCount());
			assertEquals(src.getCountNew(), dest.getCountNew());
			assertEquals(src.getDate(), dest.getDate());
		}
		
		mDbEngine.TableSms().QueryGroupByHashOrderByMaxDatDesc(res, 3, 3);
		assertEquals(2,res.size());
		
		for (int i=0; i<2; i++) {
			IMSmsGroup src = temp.get(i+3);
			IMSmsGroup dest = res.get(i);
			
			assertEquals(src.getHash(), dest.getHash());
			assertEquals(src.getPhone(), dest.getPhone());
			assertEquals(src.getCount(), dest.getCount());
			assertEquals(src.getCountNew(), dest.getCountNew());
			assertEquals(src.getDate(), dest.getDate());
		}
		
		mDbEngine.TableSms().QueryGroupByHashOrderByMaxDatDesc(res, 6, 3);
		assertEquals(0,res.size());
	}
	
	public void testContactGetByPhone() {
		IMContact dest = mDbEngine.QueryContact().getByPhone("5555");
		assertNotNull(dest);
		assertEquals("Monster", dest.getName());
	}
	
	public void testContactGetCount() throws MyException {
		int cnt = mDbEngine.QueryContact().getCount();
		Log.d("!!!", "Contact.getCount = "+cnt);
	}
	
	public void testSmsUpdateGroups() {
		
		Exception ex = null;
		
		try {
			mDbEngine.TableSms().updateGroups();
		} catch (Exception e) {
			ex=e;
		}
		
		assertNull(ex);
	}
}
