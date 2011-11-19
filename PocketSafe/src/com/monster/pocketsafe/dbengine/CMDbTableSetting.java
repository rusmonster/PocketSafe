package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.database.Cursor;

public class CMDbTableSetting implements IMDbTableSetting {
	private IMSdkDbConection mConn;
	
	public void Update(IMSetting setting) throws MyException {
		String sql = "UPDATE M__SETTING SET VAL = ? WHERE ID="+setting.getId();
		
		Object[] args = new String[] { setting.getStrVal() };
		mConn.ExecSQL(sql, args );
	}

	public void getById(IMSetting dest, TTypSetting id) throws MyException {
		String sql = "select ID,VAL from M__SETTING where ID="+id.ordinal();
		Cursor c = mConn.Query(sql);
		
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

	public void SetConnection(IMSdkDbConection conn) {
		mConn = conn;
	}

	public int getCount() throws MyException {
		String sql = "SELECT COUNT(*) FROM M__SETTING";
		Cursor c = mConn.Query(sql);
		try {
			if (c.moveToFirst())
				return c.getInt(0);
			
			throw new MyException(TTypMyException.EDbErrGetCountSetting);
		} finally {
			c.close();
		}	
	}

}
