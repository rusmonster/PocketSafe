package com.softmo.smssafe2.dbengine;

import com.softmo.smssafe2.dbengine.provider.CMDbProvider;
import com.softmo.smssafe2.dbengine.provider.CMSQLiteOnlineHelper;
import com.softmo.smssafe2.dbengine.provider.IMDbProvider;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import android.content.ContentValues;
import android.database.Cursor;

public class CMDbTableSetting implements IMDbTableSetting {
	private IMDbProvider mDbp;
	
    private static final String[] mContent = new String[] {
        CMSQLiteOnlineHelper._ID, 
        CMSQLiteOnlineHelper.SETTING_VAL
    };
    
    private static final String[] mCount = new String[] {
        "count(*) as count"
    };
	
	public void Update(IMSetting setting) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper._ID, setting.getId());
        values.put(CMSQLiteOnlineHelper.SETTING_VAL, setting.getStrVal());
        
		mDbp.update(CMDbProvider.CONTENT_URI_SETTING, values, CMSQLiteOnlineHelper._ID+"=" + setting.getId(), null);
	}

	public void getById(IMSetting dest, TTypSetting id) throws MyException {
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SETTING, mContent, CMSQLiteOnlineHelper._ID+"=" + id.ordinal(), null, null);
		
		try {
			if ( c.moveToFirst() ) {
				int n = c.getInt(0);
				dest.setId(n);
				String val = c.getString(1);
				dest.setStrVal(val);
				return;
			}
			
			throw  new MyException(TTypMyException.EDbIdNotFoundSetting);
		} finally {
			c.close();
		}
	}

	public int getCount() throws MyException {
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SETTING, mCount, null, null, null);
				
		try {
			if ( c.moveToFirst() ) {
				int cnt = c.getInt(0);
				return cnt;
			}
			
			throw  new MyException(TTypMyException.EDbErrGetCountSetting);
		} finally {
			c.close();
		}	
	}

	public void SetDbProvider(IMDbProvider dbp) {
		mDbp = dbp;
	}

}
