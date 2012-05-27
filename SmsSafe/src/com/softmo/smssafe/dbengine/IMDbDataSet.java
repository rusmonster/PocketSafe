package com.softmo.smssafe.dbengine;

import android.content.ContentResolver;

import com.softmo.smssafe.utils.MyException;

public interface IMDbDataSet {
	public void SetContentResolver(ContentResolver cr);
	int getCount() throws MyException;
}
