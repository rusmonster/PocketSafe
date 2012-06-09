package com.softmo.smssafe.testlong.importer;

import com.softmo.smssafe.main.importer.CMImporter;
import com.softmo.smssafe.main.importer.IMImporter;
import com.softmo.smssafe.main.importer.IMImporterObserver;
import com.softmo.smssafe.testlong.utils.CMTestThread;
import com.softmo.smssafe.utils.CMLocator;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;

import android.test.AndroidTestCase;
import android.util.Log;

public class CMImporterTestLong extends AndroidTestCase implements IMImporterObserver {

	IMLocator mLocator = new CMLocator();
	
	IMImporter mImporter;
	protected void setUp() throws Exception {
		super.setUp();
		mImporter = new CMImporter();
		mImporter.setObserver(this);
		mImporter.setContentResolver(getContext().getContentResolver());
		mImporter.setDbWriter(mLocator.createDbWriter());
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testImport() {
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
	}

	@Override
	public void importerStart() {
		Log.d("!!!", "importerStart");
	}

	@Override
	public void importerProgress(int perc) {
		Log.d("!!!", "importerProgress");
	}

	@Override
	public void importerFinish() {
		Log.d("!!!", "importerFinish");		
	}

	@Override
	public void importerError() {
		Log.d("!!!", "importerError");		
	}

}
