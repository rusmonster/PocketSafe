package com.softmo.smssafe.main.importer;

import android.content.ContentResolver;

import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.utils.MyException;

public interface IMImporter {
	public void setObserver(IMImporterObserver observer);
	public void setDbEngine(IMDbEngine dbengine);
	public void setRsa(IMRsa rsa);
	public void setContentResolver(ContentResolver cr);
	
	public void startImport() throws MyException;

}
