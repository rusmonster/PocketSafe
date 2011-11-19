package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

import android.database.Cursor;

public interface IMSdkDbConection {
	void Open(String filename) throws MyException;
	void ExecSQL(String sql) throws MyException;
	void ExecSQL(String sql,  Object[] bindArgs) throws MyException;
	Cursor Query(String sql) throws MyException;
	void Close();
}
