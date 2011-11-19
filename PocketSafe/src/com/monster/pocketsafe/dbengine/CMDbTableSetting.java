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

	public boolean getById(IMSetting dest, TTypSetting id) throws MyException {
		boolean res = false;
		
		String sql = "select ID,VAL from M__SETTING where ID="+id.ordinal();
		Cursor c = mConn.Query(sql);
		
		try {
			if ( c.moveToFirst() ) {
				int n = c.getInt(0);
				dest.setId(n);
				String val = c.getString(1);
				dest.setStrVal(val);
				res = true;
			}
		} finally {
			c.close();
		}
		
		if (!res) 
			throw  new MyException(TTypMyException.EDbIdNotFoundSetting);
		
		return res;
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
