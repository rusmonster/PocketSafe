package com.softmo.smssafe.testlong.importer;

import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.dbengine.provider.CMDbProvider;
import com.softmo.smssafe.main.importer.CMImporter;
import com.softmo.smssafe.main.importer.IMImporter;
import com.softmo.smssafe.main.importer.IMImporterObserver;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.testlong.utils.CMTestThread;
import com.softmo.smssafe.utils.CMLocator;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;

import android.content.ContentResolver;
import android.test.AndroidTestCase;
import android.util.Log;

public class CMImporterTestLong extends AndroidTestCase implements IMImporterObserver {

	IMLocator mLocator = new CMLocator();
	
	IMImporter mImporter;
	
	private boolean mFinished;
	private boolean mSuccess;
	
	protected void setUp() throws Exception {
		super.setUp();
		mFinished = false;
		mSuccess = false;
		mImporter = new CMImporter(mLocator);
		mImporter.setObserver(this);
		
		ContentResolver cr = getContext().getContentResolver();
		mImporter.setContentResolver( cr );
		
		IMDbEngine db = mLocator.createDbEngine();
		db.Open( new CMDbProvider(getContext()) );
		
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

	@Override
	public void importerStart() {
		Log.d("!!!", "importerStart");
	}

	@Override
	public void importerProgress(int perc) {
		Log.d("!!!", "importerProgress: "+perc);
	}

	@Override
	public void importerFinish() {
		Log.d("!!!", "importerFinish");	
		mFinished = true;
		mSuccess = true;
	}

	@Override
	public void importerError() {
		Log.d("!!!", "importerError");	
		mFinished = true;
	}

}
