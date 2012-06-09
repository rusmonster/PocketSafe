package com.softmo.smssafe.main.importer;

import android.content.ContentResolver;

import com.softmo.smssafe.main.IMDbWriter;
import com.softmo.smssafe.utils.MyException;

public interface IMImporter {
	public void setObserver(IMImporterObserver observer);
	public void setDbWriter(IMDbWriter dbwriter);
	public void setContentResolver(ContentResolver cr);
	
	public void startImport() throws MyException;

}
