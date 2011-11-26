package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.dbengine.provider.CMDbProvider;
import com.monster.pocketsafe.dbengine.provider.CMSQLiteOnenHelper;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

public class CMDbTableSetting implements IMDbTableSetting {
	private ContentResolver mCr;
	
    private static final String[] mContent = new String[] {
        CMSQLiteOnenHelper._ID, 
        CMSQLiteOnenHelper.SETTING_VAL
    };
    
    private static final String[] mCount = new String[] {
        "count(*) as count"
    };
	
	public void Update(IMSetting setting) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnenHelper._ID, setting.getId());
        values.put(CMSQLiteOnenHelper.SETTING_VAL, setting.getStrVal());
        
		mCr.update(CMDbProvider.CONTENT_URI_SETTING, values, CMSQLiteOnenHelper._ID+"=" + setting.getId(), null);
	}

	public void getById(IMSetting dest, TTypSetting id) throws MyException {
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SETTING, mContent, CMSQLiteOnenHelper._ID+"=" + id.ordinal(), null, null);
		
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
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SETTING, mCount, null, null, null);
				
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

	public void SetContentResolver(ContentResolver cr) {
		mCr = cr;
	}

}
