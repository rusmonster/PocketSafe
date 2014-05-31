package com.softmo.smssafe2.main.importer;

import android.content.ContentResolver;

import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.utils.MyException;

public interface IMImporter {
	public void setObserver(IMImporterObserver observer);
	public void setDbEngine(IMDbEngine dbengine);
	public void setRsa(IMRsa rsa);
	public void setContentResolver(ContentResolver cr);
	
	public void startImport() throws MyException;
	public void cancelImport();

}
