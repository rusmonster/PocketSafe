package com.monster.pocketsafe.dbengine;

import com.monster.pocketsafe.utils.MyException;

import android.database.Cursor;

public class CMDbTableSetting implements IMDbTableSetting {
	private IMSdkDbConection mConn;
	
	public void Update(IMSetting setting) throws MyException {
		String sql = "UPDATE M__SETTING SET VAL='"+setting.getStrVal()+
			"' WHERE ID="+setting.getId();
		
		mConn.ExecSQL(sql);
		
	}

	public boolean getById(IMSetting dest, TTypSetting id) throws MyException {
		boolean res = false;
		
		String sql = "select VAL from M__SETTING where ID="+id.ordinal();
		Cursor c = mConn.Query(sql);
		
		try {
			if ( c.moveToFirst() ) {
				dest.setId(id.ordinal());
				int val = c.getInt(0);
				dest.setIntVal(val);
				res = true;
			}
		} finally {
			c.close();
		}
		return res;
	}

	public void SetConnection(IMSdkDbConection conn) {
		mConn = conn;
	}

}
