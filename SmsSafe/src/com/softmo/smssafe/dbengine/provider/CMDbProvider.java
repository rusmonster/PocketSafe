package com.softmo.smssafe.dbengine.provider;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class CMDbProvider implements IMDbProvider  {

	public static final String PROVIDER_NAME = "com.softmo.smssafe.dbengine.provider.cmdbprovider";
	
	private static final String SMSGROUP_PATH = CMSQLiteOnlineHelper.TABLE_SMS+"/group";
	
	public static final Uri CONTENT_URI_SETTING = Uri.parse("content://"+PROVIDER_NAME+"/" + CMSQLiteOnlineHelper.TABLE_SETTING);
	public static final Uri CONTENT_URI_SMS = Uri.parse("content://"+PROVIDER_NAME+"/" + CMSQLiteOnlineHelper.TABLE_SMS);
	public static final Uri CONTENT_URI_SMSGROUP = Uri.parse("content://"+PROVIDER_NAME+"/" + SMSGROUP_PATH);
	
	private SQLiteDatabase mDb; 

	private static final UriMatcher uriMatcher;
	private static final int CODE_SETTING		= 1;
	private static final int CODE_SMS			= 2;
	private static final int CODE_SMSGROUP		= 3;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, CMSQLiteOnlineHelper.TABLE_SETTING, 		CODE_SETTING);
		uriMatcher.addURI(PROVIDER_NAME, CMSQLiteOnlineHelper.TABLE_SMS, 			CODE_SMS);
		uriMatcher.addURI(PROVIDER_NAME, SMSGROUP_PATH,								CODE_SMSGROUP);
	}
	
	public CMDbProvider(Context context) {
		mDb = (new CMSQLiteOnlineHelper(context)).getWritableDatabase();
	}
	
	public int delete(Uri uri, String where, String[] whereArgs) {
		int retVal = 0;
		
	    switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
	    	 retVal = mDb.delete(CMSQLiteOnlineHelper.TABLE_SETTING, where, whereArgs);
	        break;
	    case CODE_SMS:
	    	 retVal = mDb.delete(CMSQLiteOnlineHelper.TABLE_SMS, where, whereArgs);
	        break;
	    case CODE_SMSGROUP:
	    	retVal = mDb.delete(CMSQLiteOnlineHelper.TABLE_SMSGROUP, where, whereArgs);
	    	break;
	    default: throw new SQLException("Failed to delete from " + uri);
	    }
	    
		return retVal;
	}

	public Uri insert(Uri uri, ContentValues values) {
	    Uri _uri = null; 
	    long id=-1;
	    
	    switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
	        id = mDb.insert(CMSQLiteOnlineHelper.TABLE_SETTING, CMSQLiteOnlineHelper.SETTING_VAL, values);
	        if (id>0){
	            _uri = ContentUris.withAppendedId(CONTENT_URI_SETTING, id);
	        }
	        break;
	    case CODE_SMS:
	        id = mDb.insert(CMSQLiteOnlineHelper.TABLE_SMS, CMSQLiteOnlineHelper.SMS_PHONE, values);
	        if (id>0){
	            _uri = ContentUris.withAppendedId(CONTENT_URI_SMS, id);
	        }
	        break;
	    case CODE_SMSGROUP:
	        id = mDb.insert(CMSQLiteOnlineHelper.TABLE_SMSGROUP, CMSQLiteOnlineHelper.SMSGROUP_PHONE, values);
	        if (id>0){
	            _uri = ContentUris.withAppendedId(CONTENT_URI_SMSGROUP, id);
	        }
	        break;
	    default: throw new SQLException("Failed to insert row into " + uri);
	    }
	    return _uri; 
	}

	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort)  {
		
		Cursor c = null;
		String orderBy;       
        if (TextUtils.isEmpty(sort)) {
            orderBy = CMSQLiteOnlineHelper._ID;
        } 
        else {
            orderBy = sort;
        }

        switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
	    	c = mDb.query(CMSQLiteOnlineHelper.TABLE_SETTING, projection, selection, selectionArgs, null, null, orderBy);
	        break;
	    case CODE_SMS:
	    	c = mDb.query(CMSQLiteOnlineHelper.TABLE_SMS, projection, selection, selectionArgs, null, null, orderBy);
	        break;
	    case CODE_SMSGROUP:
	    	c = mDb.query(CMSQLiteOnlineHelper.TABLE_SMSGROUP, projection, selection, selectionArgs, null, null, orderBy);
	        break;
	    default: throw new SQLException("Failed to query from "+uri);
	    }
	        
		return c;
	}
	
	

	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int retVal = 0;
		
        switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
			mDb.update(CMSQLiteOnlineHelper.TABLE_SETTING, values, where, whereArgs); 
	        break;
	    case CODE_SMS:
			mDb.update(CMSQLiteOnlineHelper.TABLE_SMS, values, where, whereArgs); 
	        break;
	    case CODE_SMSGROUP:
	        mDb.update(CMSQLiteOnlineHelper.TABLE_SMSGROUP, values, where, whereArgs); 
	        break;
	    default: throw new SQLException("Failed to query from "+uri);
	    }
        

        return retVal;
    }
	
}
