package com.softmo.smssafe2.testlong.importer;

import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.dbengine.IMSetting;
import com.softmo.smssafe2.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe2.main.importer.CMImporter;
import com.softmo.smssafe2.main.importer.IMImporter;
import com.softmo.smssafe2.main.importer.IMImporterObserver;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.testlong.utils.CMTestThread;
import com.softmo.smssafe2.utils.CMLocator;
import com.softmo.smssafe2.utils.IMLocator;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import android.content.ContentResolver;
import android.test.AndroidTestCase;
import android.util.Log;

public class CMImporterTestLong extends AndroidTestCase implements IMImporterObserver {

	IMLocator mLocator = new CMLocator();
	
	IMImporter mImporter;
	
	private boolean mFinished;
	private boolean mSuccess;

	private int mErrCode;
	private int mProcess;
	
	protected void setUp() throws Exception {
		super.setUp();
		mFinished = false;
		mSuccess = false;
		mErrCode = 0;
		mProcess = 0;
		
		mImporter = new CMImporter(mLocator);
		mImporter.setObserver(this);
		
		ContentResolver cr = getContext().getContentResolver();
		mImporter.setContentResolver( cr );
		
		IMDbEngine db = mLocator.createDbEngine();
		db.Open( getContext() );
		
		IMSetting set = mLocator.createSetting();
		db.QuerySetting().getById(set, TTypSetting.ERsaPub);
		mImporter.setDbEngine(db);
		
		IMRsa rsa = mLocator.createRsa();
		rsa.setPublicKey(set.getStrVal());
		mImporter.setRsa(rsa);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testImport() throws InterruptedException {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					mImporter.startImport();
				} catch (MyException e) {
					Log.d("!!!", "importerStart exception: "+e.getId());
				}
			}
		};
		CMTestThread th = new CMTestThread(r);
		th.start();
		
		int n=200;
		while (!mFinished && n-- >0 )
			Thread.sleep(1000);
		
		th.stopThread();
		
		assertTrue(mFinished);
		assertTrue(mSuccess);
	}
	
	public void testImportCancel() throws InterruptedException {
		Runnable r = new Runnable() {
			
			@Override
			public void run() {
				try {
					mImporter.startImport();
				} catch (MyException e) {
					Log.d("!!!", "importerStart exception: "+e.getId());
				}
			}
		};
		CMTestThread th = new CMTestThread(r);
		th.start();
		
		Thread.sleep(2000);
		
		int n=200;
		while (mProcess<3 && n-- >0 )
			Thread.sleep(1000);
		
		mImporter.cancelImport();
		
		n=3;
		while (!mFinished && n-- >0 )
			Thread.sleep(1000);
		
		th.stopThread();
		
		assertTrue(mFinished);
		assertFalse(mSuccess);
		assertEquals(TTypMyException.EImporterCancelled.getValue(), mErrCode);
	}

	@Override
	public void importerStart() {
		Log.d("!!!", "importerStart");
	}

	@Override
	public void importerProgress(int perc) {
		Log.d("!!!", "importerProgress: "+perc);
		mProcess = perc;
	}

	@Override
	public void importerFinish() {
		Log.d("!!!", "importerFinish");	
		mFinished = true;
		mSuccess = true;
	}

	@Override
	public void importerError(int err) {
		Log.d("!!!", "importerError");	
		mFinished = true;
		mErrCode = err;
	}

}
