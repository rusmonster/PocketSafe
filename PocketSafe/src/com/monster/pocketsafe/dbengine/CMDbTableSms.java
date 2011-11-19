package com.monster.pocketsafe.dbengine;

import android.database.Cursor;

import com.monster.pocketsafe.utils.MyException;

public class CMDbTableSms implements IMDbTableSms {
	private IMSdkDbConection mConn;
	protected void Load(IMSms dest, Cursor from) throws MyException {
		dest.setId(from.getInt(0));
		//TTypDirection.
	}
	
	public void Delete(int id) throws MyException {
		String sql = "DELETE FROM M__SMS WHERE ID="+id;
		mConn.ExecSQL(sql);
	}

	public int Insert(IMSms item) {
		String format = "INSERT INTO M__SMS(ID,DIRECTION,ISNEW,PHONE,TXT,DAT) " +
						"VALUES(%d, %d, %d, '%s','%s',%s";
		/*
		sql.format(format, item.getId(), item.getDirection())
		mConn.ExecSQL(sql);
		*/
		return 0;

	}

	public void QueryGroupByPhoneOrderByDatDesc(IMSms[] dest, int start,
			int count) {
		// TODO Auto-generated method stub

	}

	public boolean getById(IMSms dest, int id) throws MyException {
		String sql = "SELECT ID,DIRECTION,ISNEW,PHONE,TXT,DAT FROM M__SMS WHERE ID="+id;
		Cursor c = mConn.Query(sql);
		boolean res=false;
		try {
			if (c.moveToFirst())
			{
				dest.setId(c.getInt(0));
				/*
				TTypDirection dir = c.getInt(1);
				
				dest.setDirection((TTypDirection)c.getInt(1));
				*/
				res=true;	
			}
		} finally {
			c.close();
		}
		return res;
	}

	public void SetConnection(IMSdkDbConection conn) {
		mConn = conn;
		
	}

	public void Clear() throws MyException {
		String sql = "DELETE FROM M__SMS";
		mConn.ExecSQL(sql);
	}

}
