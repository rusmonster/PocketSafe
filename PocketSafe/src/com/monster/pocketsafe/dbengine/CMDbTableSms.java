package com.monster.pocketsafe.dbengine;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.monster.pocketsafe.dbengine.provider.CMDbProvider;
import com.monster.pocketsafe.dbengine.provider.CMSQLiteOnlineHelper;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMDbTableSms implements IMDbTableSms {
	private IMLocator mLocator;
	private ContentResolver mCr;
	
    private static final String[] mContent = new String[] {
        CMSQLiteOnlineHelper._ID, 
        CMSQLiteOnlineHelper.SMS_DIRECTION,
        CMSQLiteOnlineHelper.SMS_FOLDER,
        CMSQLiteOnlineHelper.SMS_ISNEW,
        CMSQLiteOnlineHelper.SMS_PHONE,
        CMSQLiteOnlineHelper.SMS_TEXT,
        CMSQLiteOnlineHelper.SMS_DATE
    };
    
    private static final String[] mContentGroup = new String[] {
    	CMSQLiteOnlineHelper.SMSGROUP_PHONE,
    	CMSQLiteOnlineHelper.SMSGROUP_COUNT,
    	CMSQLiteOnlineHelper.SMSGROUP_COUNTNEW,
    	CMSQLiteOnlineHelper.SMSGROUP_MAXDATE
    };
    
    private static final String[] mCount = new String[] {
        "count(*) as count"
    };
    
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
		
		Date dat = new Date(c.getLong(6));
		dest.setDate(dat);
	}
	
	public void Delete(int id) throws MyException {
		mCr.delete(CMDbProvider.CONTENT_URI_SMS, CMSQLiteOnlineHelper._ID+"="+id, null);
	}
	
	public void DeleteByPhone(String phone) throws MyException {
		mCr.delete(CMDbProvider.CONTENT_URI_SMS, CMSQLiteOnlineHelper.SMS_PHONE+"=?", new String[] {phone});
	}

	public int Insert(IMSms item) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMS_DIRECTION, item.getDirection());
        values.put(CMSQLiteOnlineHelper.SMS_FOLDER, item.getFolder());
        values.put(CMSQLiteOnlineHelper.SMS_ISNEW, item.getIsNew());
        values.put(CMSQLiteOnlineHelper.SMS_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMS_TEXT, item.getText());
        values.put(CMSQLiteOnlineHelper.SMS_DATE, item.getDate().getTime());
        
        Uri uriId = mCr.insert(CMDbProvider.CONTENT_URI_SMS, values);
        if (uriId == null) throw new MyException(TTypMyException.EDbErrInsertSms);
        
        int id = (int) ContentUris.parseId(uriId);
        if (id<0) throw new MyException(TTypMyException.EDbErrInsertSms);
        
		return id;
	}


	public void getById(IMSms dest, int id) throws MyException {
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper._ID+"="+id, null, null);
		
		try {
			if ( c.moveToFirst() ) {
				Load(dest, c);
				return;
			}
			
			throw  new MyException(TTypMyException.EDbIdNotFoundSms);
		} finally {
			c.close();
		}
	}

	public void SetContentResolver(ContentResolver cr) {
		mCr = cr;
	}

	public void Clear() throws MyException {
		mCr.delete(CMDbProvider.CONTENT_URI_SMS, null, null);
	}

	public int getCount() throws MyException { 
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMS, mCount, null, null, null);
		
		try {
			if ( c.moveToFirst() ) {
				int cnt = c.getInt(0);
				return cnt;
			}
			
			throw  new MyException(TTypMyException.EDbErrGetCountSms);
		} finally {
			c.close();
		}
	}

	public void QueryByFolderOrderByDatDesc(ArrayList<IMSms> dest, int folder, int start,
			int count) throws MyException  {
		
		dest.clear();
		
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper.SMS_FOLDER+"="+folder, null, CMSQLiteOnlineHelper.SMS_DATE+" DESC"); 
		
		try {
			if (!c.moveToFirst()) return;
			if ( !c.move(start) ) return;
			
			int end = start+count;
			for (int i=start; i<end; i++) {
				IMSms sms = mLocator.createSms();
				Load(sms, c);
				dest.add(sms);
				if (!c.moveToNext()) return;
			}
		} finally {
			c.close();
		}
	}

	public void QueryGroupByPhoneOrderByMaxDatDesc(ArrayList<IMSmsGroup> dest, int start, int count) {
		dest.clear();
		
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMSGROUP, mContentGroup, null, null, CMSQLiteOnlineHelper.SMSGROUP_MAXDATE+" DESC"); 
		
		try {
			if (!c.moveToFirst()) return;
			if ( !c.move(start) ) return;
			
			int end = start+count;
			for (int i=start; i<end; i++) {
				IMSmsGroup gr = mLocator.createSmsGroup();
				gr.setPhone(c.getString(0));
				gr.setCount(c.getInt(1));
				gr.setCountNew(c.getInt(2));
				gr.setDate( new Date(c.getLong(3)));
				dest.add(gr);
				if (!c.moveToNext()) return;
			}
		} finally {
			c.close();
		}	
	}

	public void Update(IMSms item) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMS_DIRECTION, item.getDirection());
        values.put(CMSQLiteOnlineHelper.SMS_FOLDER, item.getFolder());
        values.put(CMSQLiteOnlineHelper.SMS_ISNEW, item.getIsNew());
        values.put(CMSQLiteOnlineHelper.SMS_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMS_TEXT, item.getText());
        values.put(CMSQLiteOnlineHelper.SMS_DATE, item.getDate().getTime());
        
        mCr.update(CMDbProvider.CONTENT_URI_SMS, values, CMSQLiteOnlineHelper._ID + "="+item.getId(), null);	
	}

	public void QueryByPhoneOrderByDat(ArrayList<IMSms> dest, String phone,	int start, int count) throws MyException {
		
		dest.clear();
		
		String[] args = new String[] { phone };
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper.SMS_PHONE+"=?", args , CMSQLiteOnlineHelper.SMS_DATE); 
		
		try {
			if (!c.moveToFirst()) return;
			if ( !c.move(start) ) return;
			
			int end = start+count;
			for (int i=start; i<end; i++) {
				IMSms sms = mLocator.createSms();
				Load(sms, c);
				dest.add(sms);
				if (!c.moveToNext()) return;
			}
		} finally {
			c.close();
		}
	}

	public int getCountNew() throws MyException {
		Cursor c = mCr.query(CMDbProvider.CONTENT_URI_SMS, mCount, 
				CMSQLiteOnlineHelper.SMS_ISNEW+">="+TTypIsNew.ENew+" and "+CMSQLiteOnlineHelper.SMS_FOLDER+"="+TTypFolder.EInbox, null, null);
		
		try {
			if ( c.moveToFirst() ) {
				int cnt = c.getInt(0);
				return cnt;
			}
			
			throw  new MyException(TTypMyException.EDbErrGetCountSmsNew);
		} finally {
			c.close();
		}
	}

}
