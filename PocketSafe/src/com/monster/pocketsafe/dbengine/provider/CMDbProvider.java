package com.monster.pocketsafe.dbengine.provider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class CMDbProvider extends ContentProvider {

	public static final String PROVIDER_NAME = "com.monster.pocketsafe.dbengine.provider.cmdbprovider";
	
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
		uriMatcher.addURI(PROVIDER_NAME, SMSGROUP_PATH,							CODE_SMSGROUP);
	}
	
	@Override
	public boolean onCreate() {
		 mDb = (new CMSQLiteOnlineHelper(getContext())).getWritableDatabase();
	     return (mDb == null) ? false : true;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int retVal = 0;
		
	    switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
	    	 retVal = mDb.delete(CMSQLiteOnlineHelper.TABLE_SETTING, where, whereArgs);
	         getContext().getContentResolver().notifyChange(uri, null);
	        break;
	    case CODE_SMS:
	    	 retVal = mDb.delete(CMSQLiteOnlineHelper.TABLE_SMS, where, whereArgs);
	         getContext().getContentResolver().notifyChange(uri, null);
	        break;
	    default: throw new SQLException("Failed to delete from " + uri);
	    }
	    
		return retVal;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
	    Uri _uri = null; 
	    long id=-1;
	    
	    switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
	        id = mDb.insert(CMSQLiteOnlineHelper.TABLE_SETTING, CMSQLiteOnlineHelper.SETTING_VAL, values);
	        if (id>0){
	            _uri = ContentUris.withAppendedId(CONTENT_URI_SETTING, id);
	            getContext().getContentResolver().notifyChange(_uri, null);    
	        }
	        break;
	    case CODE_SMS:
	        id = mDb.insert(CMSQLiteOnlineHelper.TABLE_SMS, CMSQLiteOnlineHelper.SMS_PHONE, values);
	        if (id>0){
	            _uri = ContentUris.withAppendedId(CONTENT_URI_SMS, id);
	            getContext().getContentResolver().notifyChange(_uri, null);    
	        }
	        break;
	    default: throw new SQLException("Failed to insert row into " + uri);
	    }
	    return _uri; 
	}

	@Override
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
	        c.setNotificationUri(getContext().getContentResolver(), uri);
	        break;
	    case CODE_SMS:
	    	c = mDb.query(CMSQLiteOnlineHelper.TABLE_SMS, projection, selection, selectionArgs, null, null, orderBy);
	        c.setNotificationUri(getContext().getContentResolver(), uri);	       
	        break;
	    case CODE_SMSGROUP:
	    	c = mDb.query(CMSQLiteOnlineHelper.QUERY_SMSGROUP, projection, selection, selectionArgs, null, null, orderBy);
	        c.setNotificationUri(getContext().getContentResolver(), uri);	       
	        break;
	    default: throw new SQLException("Failed to query from "+uri);
	    }
	        
		return c;
	}
	
	

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		int retVal = 0;
		
        switch (uriMatcher.match(uri)){
	    case CODE_SETTING:
			mDb.update(CMSQLiteOnlineHelper.TABLE_SETTING, values, where, whereArgs); 
	        getContext().getContentResolver().notifyChange(uri, null);
	        break;
	    case CODE_SMS:
			mDb.update(CMSQLiteOnlineHelper.TABLE_SMS, values, where, whereArgs); 
	        getContext().getContentResolver().notifyChange(uri, null);
	        break;
	    default: throw new SQLException("Failed to query from "+uri);
	    }
        

        return retVal;
    }

	@Override
	public String getType(Uri arg0) {
		return null;
	}
	
}
