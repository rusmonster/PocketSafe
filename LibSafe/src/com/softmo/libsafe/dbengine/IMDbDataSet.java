package com.softmo.libsafe.dbengine;

import android.content.ContentResolver;

import com.softmo.libsafe.utils.MyException;

public interface IMDbDataSet {
	public void SetContentResolver(ContentResolver cr);
	int getCount() throws MyException;
}
