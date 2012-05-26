package com.monster.pocketsafe.testlong.dbengine;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import com.monster.pocketsafe.dbengine.CMDbEngine;
import com.monster.pocketsafe.dbengine.IMDbEngine;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypFolder;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.dbengine.TTypStatus;
import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.sec.IMRsa;
import com.monster.pocketsafe.sec.IMSha256;
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
	
	public void testMarkSendError() throws MyException {
		Log.d("!!!","Starting testMarkSendError");
		
		ArrayList<IMSms> res = new ArrayList<IMSms>();
		mDbEngine.TableSms().QueryByFolderOrderByDatDesc(res, TTypFolder.ESent, 0, Integer.MAX_VALUE);
		int cnt = res.size();
		
		for (int i=0; i<cnt; i++) {
			IMSms sms = res.get(i);
			if (sms.getFolder() == TTypFolder.ESent) {
				sms.setFolder(TTypFolder.EOutbox);
				sms.setStatus(TTypStatus.ESendError);
				mDbEngine.TableSms().Update(sms);
				Log.i("!!!", "Mark SendError");
			}
		}
		
	}
	
	public void testAdd1000Sms() throws MyException, UnsupportedEncodingException {
		int cnt=1000;

	
		IMSetting set = mLocator.createSetting();
		mDbEngine.TableSetting().getById(set, TTypSetting.ERsaPub);
		String pub = set.getStrVal();
		if (pub.length()==0)
			fail("no rsa pub");

		IMRsa rsa = mLocator.createRsa();
		rsa.setPublicKey(pub);
		
		String phone = "testPhone";
		
		IMSha256 sha = mLocator.createSha256();
		String hash = sha.getHash(phone);
		
		byte[] cPhone = rsa.EncryptBuffer(phone.getBytes());
		phone = new String(cPhone, IMDbEngine.ENCODING);
		
		
		
		for (int i=0; i<cnt; i++) {
			String text = "testText"+i;
			Log.i("!!!", text);
			
			byte[] cText = rsa.EncryptBuffer(text.getBytes());
			
			IMSms sms = mLocator.createSms();
			sms.setDirection(TTypDirection.EIncoming);
			sms.setFolder(TTypFolder.EInbox);
			sms.setIsNew(TTypIsNew.EJustRecv);
			sms.setPhone(phone);
			sms.setHash(hash);
			sms.setText(new String(cText, IMDbEngine.ENCODING));
			sms.setDate( new Date() );
			sms.setStatus(TTypStatus.ERecv);
			
			try {
				mDbEngine.TableSms().Insert(sms);
			}catch(MyException e) {
				Log.e("!!!", "Error inserting: "+e.getId());
			}
		}
			
	}
}
