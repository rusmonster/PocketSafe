package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CMSdkDbConnection implements IMSdkDbConection {
	private SQLiteDatabase mDb;

	public void Close() {
		if (mDb != null) {
			mDb.close();
			mDb=null;
		}
		
	}

	public void Open(String filename) throws MyException {
		if (mDb != null)
			throw new MyException(TTypMyException.EDbAlreadyOpened);
		
		Context context = MyAppContext.getAppContext();
		mDb = context.openOrCreateDatabase(filename, 0 /*MODE_WORLD_WRITEABLE*/, null);
	}
	
	public void ExecSQL(String sql) throws MyException {
		if (mDb == null || !mDb.isOpen())
			throw new MyException(TTypMyException.EDbNotOpened);
		
		mDb.execSQL(sql);
	}

	public void ExecSQL(String sql, Object[] bindArgs) throws MyException {
		if (mDb == null || !mDb.isOpen())
			throw new MyException(TTypMyException.EDbNotOpened);
		
		mDb.execSQL(sql, bindArgs);
	}

	public Cursor Query(String sql) throws MyException {
		if (mDb == null || !mDb.isOpen())
			throw new MyException(TTypMyException.EDbNotOpened);
		
		return mDb.rawQuery(sql, null);
	}
	
	public int getLastInsertID() throws MyException {
		int id = -1;
		String sql = "SELECT last_insert_rowid();";
		
		Cursor c = Query(sql);
		try {
			c.moveToFirst();
			id = c.getInt(0);
		} catch (Exception e){}
		
		c.close();
		
		if (id<=0)
			throw new MyException(TTypMyException.EDbErrorGetLastID);
		
		return id;
	}
}
