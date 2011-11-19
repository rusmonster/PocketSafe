package com.monster.pocketsafe.dbengine;

import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;

import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMDbTableSms implements IMDbTableSms {
	private IMLocator mLocator;
	private IMSdkDbConection mConn;
	private static final String mAllFieldsNoID = "DIRECTION,FOLDER,ISNEW,PHONE,TXT,DAT";
	private static final String mAllFields = "ID," + mAllFieldsNoID;
	
	public CMDbTableSms(IMLocator locator) {
		mLocator = locator;
	}
	
	protected void Load(IMSms dest, Cursor c) throws MyException {
		dest.setId(c.getInt(0));
		dest.setDirection(c.getInt(1));
		dest.setFolder(c.getInt(2));
		dest.setIsNew(c.getInt(3));
		dest.setPhone(c.getString(4));
		dest.setText(c.getString(5));
		
		Date dat = new Date();
		dat.setTime(c.getLong(6));
		dest.setDate(dat);
	}
	
	public void Delete(int id) throws MyException {
		String sql = "DELETE FROM M__SMS WHERE ID="+id;
		mConn.ExecSQL(sql);
	}

	public int Insert(IMSms item) throws MyException {
		String format = "INSERT INTO M__SMS(" + mAllFieldsNoID + ") " +
						"VALUES(%d, %d, %d, ?, ?, '%d')";
		
		String sql = String.format(format, item.getDirection(), item.getFolder(), item.getIsNew(), item.getDate().getTime());
		
		mConn.ExecSQL(sql, new String[] {item.getPhone(), item.getText()});
		int id = mConn.getLastInsertID();
		return id;

	}


	public void getById(IMSms dest, int id) throws MyException {
		String sql = "SELECT "+mAllFields+" FROM M__SMS WHERE ID="+id;
		Cursor c = mConn.Query(sql);
		try {
			if (c.moveToFirst())
			{
				Load(dest, c);
				return;
			}
			
			throw new MyException(TTypMyException.EDbIdNotFoundSms);
		} finally {
			c.close();
		}
	}

	public void SetConnection(IMSdkDbConection conn) {
		mConn = conn;
		
	}

	public void Clear() throws MyException {
		String sql = "DELETE FROM M__SMS";
		mConn.ExecSQL(sql);
	}

	public int getCount() throws MyException { 
		String sql = "SELECT COUNT(*) FROM M__SMS";
		Cursor c = mConn.Query(sql);
		try {
			if (c.moveToFirst())
				return c.getInt(0);
			
			throw new MyException(TTypMyException.EDbErrGetCountSms);
		} finally {
			c.close();
		}
	}

	public void QueryByFolderOrderByDatDesc(ArrayList<IMSms> dest, int folder, int start,
			int count) throws MyException {
		dest.clear();
		String format = "SELECT " + mAllFields + " FROM M__SMS WHERE FOLDER=%d ORDER BY DAT DESC LIMIT %d OFFSET %d";
		String sql = String.format(format, folder, count, start);
		Cursor c = mConn.Query(sql);
		try {
			if (!c.moveToFirst()) return;
			do {
				IMSms sms = mLocator.createSms();
				Load(sms,c);
				dest.add(sms);
			} while (c.moveToNext());
		} finally {
			c.close();
		}
	}

}
