package com.monster.pocketsafe.dbengine;

import android.content.ContentResolver;

import com.monster.pocketsafe.utils.MyException;

public interface IMDbDataSet {
	public void SetContentResolver(ContentResolver cr);
	int getCount() throws MyException;
}
