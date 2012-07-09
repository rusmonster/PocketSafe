package com.softmo.smssafe.dbengine;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.softmo.smssafe.dbengine.provider.CMDbProvider;
import com.softmo.smssafe.dbengine.provider.CMSQLiteOnlineHelper;
import com.softmo.smssafe.dbengine.provider.IMDbProvider;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

public class CMDbTableSms implements IMDbTableSms {
	private IMLocator mLocator;
	private IMDbProvider mDbp;
	
    private static final String[] mContent = new String[] {
        CMSQLiteOnlineHelper._ID, 
        CMSQLiteOnlineHelper.SMS_DIRECTION,
        CMSQLiteOnlineHelper.SMS_FOLDER,
        CMSQLiteOnlineHelper.SMS_ISNEW,
        CMSQLiteOnlineHelper.SMS_HASH,
        CMSQLiteOnlineHelper.SMS_PHONE,
        CMSQLiteOnlineHelper.SMS_TEXT,
        CMSQLiteOnlineHelper.SMS_DATE,
        CMSQLiteOnlineHelper.SMS_STATUS,
        CMSQLiteOnlineHelper.SMS_SMSID
    };
    
    private static final String[] mContentGroup = new String[] {
    	CMSQLiteOnlineHelper._ID, 
    	CMSQLiteOnlineHelper.SMSGROUP_HASH,
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
		dest.setHash(c.getString(4));
		dest.setPhone(c.getString(5));
		dest.setText(c.getString(6));
		
		Date dat = new Date(c.getLong(7));
		dest.setDate(dat);
		
		dest.setStatus(c.getInt(8));
		dest.setSmsId(c.getInt(9));
	}
	
	protected void LoadGroup(IMSmsGroup dest, Cursor c) throws MyException {
		dest.setId(c.getInt(0));
		dest.setHash(c.getString(1));
		dest.setPhone(c.getString(2));
		dest.setCount(c.getInt(3));
		dest.setCountNew(c.getInt(4));
		dest.setDate( new Date(c.getLong(5)));
	}
	
	public void Delete(int id) throws MyException {
		IMSms sms = mLocator.createSms();
		getById(sms, id);
		
		IMSmsGroup gr = mLocator.createSmsGroup();
		GetGroupByHash(gr, sms.getHash());
		
		gr.setCount( gr.getCount()-1 );
		if ((sms.getFolder()==TTypFolder.EInbox) && (sms.getIsNew()==TTypIsNew.EJustRecv || sms.getIsNew()==TTypIsNew.ENew))
			gr.setCountNew( gr.getCountNew()-1 );
		
		if (gr.getCount()<=0)
			mDbp.delete(CMDbProvider.CONTENT_URI_SMSGROUP, CMSQLiteOnlineHelper._ID+"="+gr.getId(), null);
		else
			UpdateGroup(gr);
		
		mDbp.delete(CMDbProvider.CONTENT_URI_SMS, CMSQLiteOnlineHelper._ID+"="+id, null);
	}
	
	public void DeleteByHash(String hash) throws MyException {
		mDbp.delete(CMDbProvider.CONTENT_URI_SMSGROUP, CMSQLiteOnlineHelper.SMSGROUP_HASH+"=?", new String[] {hash});
		mDbp.delete(CMDbProvider.CONTENT_URI_SMS, CMSQLiteOnlineHelper.SMS_HASH+"=?", new String[] {hash});
	}

	protected int InsertGroup(IMSmsGroup item) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMSGROUP_HASH, item.getHash());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_COUNT, item.getCount());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_COUNTNEW, item.getCountNew());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_MAXDATE, item.getDate().getTime());
        
        Uri uriId = mDbp.insert(CMDbProvider.CONTENT_URI_SMSGROUP, values);
        if (uriId == null) throw new MyException(TTypMyException.EDbErrInsertGroup);
        
        int id = (int) ContentUris.parseId(uriId);
        if (id<0) throw new MyException(TTypMyException.EDbErrInsertGroup);
        
		return id;	
	}
	
	public int Insert(IMSms item) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMS_DIRECTION, item.getDirection());
        values.put(CMSQLiteOnlineHelper.SMS_FOLDER, item.getFolder());
        values.put(CMSQLiteOnlineHelper.SMS_ISNEW, item.getIsNew());
        values.put(CMSQLiteOnlineHelper.SMS_HASH, item.getHash());
        values.put(CMSQLiteOnlineHelper.SMS_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMS_TEXT, item.getText());
        values.put(CMSQLiteOnlineHelper.SMS_DATE, item.getDate().getTime());
        values.put(CMSQLiteOnlineHelper.SMS_STATUS, item.getStatus());
        values.put(CMSQLiteOnlineHelper.SMS_SMSID, item.getSmsId());
        
        Log.d("!!!", "Inserting sms...");
        Uri uriId = mDbp.insert(CMDbProvider.CONTENT_URI_SMS, values);
        if (uriId == null) throw new MyException(TTypMyException.EDbErrInsertSms);
        Log.d("!!!", "Inserted");
        
        int id = (int) ContentUris.parseId(uriId);
        if (id<0) throw new MyException(TTypMyException.EDbErrInsertSms);
        
        IMSmsGroup gr = mLocator.createSmsGroup();
        try {
        	GetGroupByHash(gr, item.getHash());
        	gr.setCount( gr.getCount()+1 );
        	
        	if ((item.getFolder()==TTypFolder.EInbox) && (item.getIsNew() == TTypIsNew.EJustRecv || item.getIsNew() == TTypIsNew.ENew))
        		gr.setCountNew( gr.getCountNew()+1 );
        	gr.setDate(item.getDate());
        	
        	UpdateGroup(gr);
        } catch (MyException e) {
        	if (e.getId()==TTypMyException.EDbIdNotFoundGroup) {
        		gr.setHash(item.getHash());
        		gr.setPhone(item.getPhone());
        		gr.setCount(1);
        		if ((item.getFolder()==TTypFolder.EInbox) && (item.getIsNew() == TTypIsNew.EJustRecv || item.getIsNew() == TTypIsNew.ENew))
            		gr.setCountNew(1);
        		else
        			gr.setCountNew(0);
        		gr.setDate(item.getDate());
        		InsertGroup(gr);
        	} else
        		throw e;
        }
        
		return id;
	}


	public void getById(IMSms dest, int id) throws MyException {
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper._ID+"="+id, null, null);
		
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

	public String getHashById(int id) throws MyException {
		IMSms sms = mLocator.createSms();
		getById(sms, id);
		return sms.getHash();
	}
	
	public void SetDbProvider(IMDbProvider dbp) {
		mDbp = dbp;
	}

	public void Clear() throws MyException {
		mDbp.delete(CMDbProvider.CONTENT_URI_SMS, null, null);
		mDbp.delete(CMDbProvider.CONTENT_URI_SMSGROUP, null, null);
	}

	public int getCount() throws MyException { 
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mCount, null, null, null);
		
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
		
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper.SMS_FOLDER+"="+folder, null, CMSQLiteOnlineHelper.SMS_DATE+" DESC " + " LIMIT "+start+","+count); 
		
		try {
			if (!c.moveToFirst()) return;
			
			do {
				IMSms sms = mLocator.createSms();
				Load(sms, c);
				dest.add(sms);
			} while(c.moveToNext());

		} finally {
			c.close();
		}
	}

	public void QueryGroupByHashOrderByMaxDatDesc(ArrayList<IMSmsGroup> dest, int start, int count) throws MyException {
		dest.clear();
		
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMSGROUP, mContentGroup, null, null, CMSQLiteOnlineHelper.SMSGROUP_MAXDATE+" DESC LIMIT "+start+","+count); 
		
		try {
			if (!c.moveToFirst()) return;
			
			do {
				IMSmsGroup gr = mLocator.createSmsGroup();
				LoadGroup(gr, c);
				dest.add(gr);
			} while(c.moveToNext());
			
		} finally {
			c.close();
		}	
	}
	
	private void GetGroupByHash(IMSmsGroup dest, String hash) throws MyException {
		
		String[] args = new String[] { hash };		
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMSGROUP, mContentGroup, CMSQLiteOnlineHelper.SMSGROUP_HASH+"=?", args, null); 
		
		try {
			if ( c.moveToFirst() ) {
				LoadGroup(dest, c);
				return;
			}
			
			throw  new MyException(TTypMyException.EDbIdNotFoundGroup);
		} finally {
			c.close();
		}	
	}

	public void QueryByHashOrderByDat(ArrayList<IMSms> dest, String hash,	int start, int count) throws MyException {
		
		dest.clear();
		
		String[] args = new String[] { hash };
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper.SMS_HASH+"=?", args , CMSQLiteOnlineHelper.SMS_DATE + " LIMIT "+start+","+count); 
		
		try {
			if (!c.moveToFirst()) return;
			
			do {
				IMSms sms = mLocator.createSms();
				Load(sms, c);
				dest.add(sms);	
			} while(c.moveToNext());

		} finally {
			c.close();
		}
	}

	public int getCountNew() throws MyException {
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mCount, 
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
	
	public int getCountByHash(String hash) throws MyException {
		IMSmsGroup gr = mLocator.createSmsGroup();
		GetGroupByHash(gr, hash);
		return gr.getCount();
	}
	

	public void Update(IMSms item) throws MyException {
		IMSms sms = mLocator.createSms();
		getById(sms, item.getId());
		
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMS_DIRECTION, item.getDirection());
        values.put(CMSQLiteOnlineHelper.SMS_FOLDER, item.getFolder());
        values.put(CMSQLiteOnlineHelper.SMS_ISNEW, item.getIsNew());
        values.put(CMSQLiteOnlineHelper.SMS_HASH, item.getHash());
        values.put(CMSQLiteOnlineHelper.SMS_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMS_TEXT, item.getText());
        values.put(CMSQLiteOnlineHelper.SMS_DATE, item.getDate().getTime());
        values.put(CMSQLiteOnlineHelper.SMS_STATUS, item.getStatus());
        values.put(CMSQLiteOnlineHelper.SMS_SMSID, item.getSmsId());
        
        Log.d("!!!", "Updating sms...");
        mDbp.update(CMDbProvider.CONTENT_URI_SMS, values, CMSQLiteOnlineHelper._ID + "="+item.getId(), null);	
        Log.d("!!!", "Updated");
        
		if ((sms.getFolder()==TTypFolder.EInbox) && (sms.getIsNew()>=TTypIsNew.ENew && item.getIsNew()<TTypIsNew.ENew)) {
			IMSmsGroup gr = mLocator.createSmsGroup();
			GetGroupByHash(gr, item.getHash());
			gr.setCountNew(gr.getCountNew()-1);
			UpdateGroup(gr);
		} 
	}
	
	protected void UpdateGroup(IMSmsGroup item) throws MyException {
        ContentValues values = new ContentValues();
        
        values.put(CMSQLiteOnlineHelper.SMSGROUP_HASH, item.getHash());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_PHONE, item.getPhone());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_COUNT, item.getCount());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_COUNTNEW, item.getCountNew());
        values.put(CMSQLiteOnlineHelper.SMSGROUP_MAXDATE, item.getDate().getTime());
        
        mDbp.update(CMDbProvider.CONTENT_URI_SMSGROUP, values, CMSQLiteOnlineHelper._ID + "="+item.getId(), null);
	}

	public IMSms getBySmsId(int smsId) throws MyException {
		if (smsId<0)
			return null;
		
		Cursor c = mDbp.query(CMDbProvider.CONTENT_URI_SMS, mContent, CMSQLiteOnlineHelper.SMS_SMSID+"="+smsId, null, null);
		
		try {
			if ( c.moveToFirst() ) {
				IMSms res = mLocator.createSms();
				Load(res, c);
				return res;
			}
		} finally {
			c.close();
		}
		return null;
	}

	public void updateGroups() throws MyException {
		mDbp.exec("delete from "+CMSQLiteOnlineHelper.TABLE_SMSGROUP);
		mDbp.exec("insert into "+CMSQLiteOnlineHelper.TABLE_SMSGROUP+
				"("+CMSQLiteOnlineHelper.SMSGROUP_HASH	+ "," +
					CMSQLiteOnlineHelper.SMSGROUP_PHONE	+ "," +
					CMSQLiteOnlineHelper.SMSGROUP_COUNT	+ "," +
					CMSQLiteOnlineHelper.SMSGROUP_COUNTNEW	+ "," +
					CMSQLiteOnlineHelper.SMSGROUP_MAXDATE + ")" +
				" select * from "+CMSQLiteOnlineHelper.QUERY_SMSGROUP);
	}

	public void markAllRead() throws MyException {
		mDbp.exec("update "+CMSQLiteOnlineHelper.TABLE_SMS+" set "+CMSQLiteOnlineHelper.SMS_ISNEW+"="+TTypIsNew.EOld+
				" where "+CMSQLiteOnlineHelper.SMS_FOLDER+"="+TTypFolder.EInbox);
		updateGroups();
	}
}
