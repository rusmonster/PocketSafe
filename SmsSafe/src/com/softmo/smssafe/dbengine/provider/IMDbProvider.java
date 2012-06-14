package com.softmo.smssafe.dbengine.provider;

import com.softmo.smssafe.utils.MyException;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public interface IMDbProvider {

	public abstract int delete(Uri uri, String where, String[] whereArgs);

	public abstract Uri insert(Uri uri, ContentValues values);

	public abstract Cursor query(Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sort);

	public abstract int update(Uri uri, ContentValues values, String where,
			String[] whereArgs);

	public abstract void exec(String sql) throws MyException;

}