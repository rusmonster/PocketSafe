package com.softmo.smssafe.main.importer;

import com.softmo.smssafe.main.IMDbWriter;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.content.ContentResolver;
import android.os.AsyncTask;

public class CMImporter extends AsyncTask<Void, Integer, Boolean>implements IMImporter {
	
	private IMImporterObserver mObserver;
	private IMDbWriter mDbWriter;
	private ContentResolver mCr;
	
	private enum TImporterState {
		EIdle,
		EBusy
	}

	TImporterState mState = TImporterState.EIdle;
	
	public void setObserver(IMImporterObserver observer) {
		mObserver = observer;
	}

	public void startImport() throws MyException {
		if (mState != TImporterState.EIdle)
			throw new MyException(TTypMyException.EImporterErrBusy);
		
		if (mObserver==null || mDbWriter==null || mCr==null)
			throw new MyException(TTypMyException.EImporterNullParam); 
		
		execute();
		mState = TImporterState.EIdle;
		try {
			mObserver.importerStart();
		} catch(Exception e){};
	}

	private void doImport() {
		/*
		
		Cursor cursor = mCr.query(Uri.parse("content://sms"), new String[] {"count(*)"}, null, null, null);
		try {
			cursor.moveToFirst();
			
			int cnt = cursor.getInt(0);
			Log.d("!!!", "sms cnt: "+cnt);
			cursor.close();
			
			cursor = mCr.query(Uri.parse("content://sms"), null, null, null, null);
			
			if (!cursor.moveToFirst())
				return;
	
			int n=0;
			do{
			   String msgData = "";
			   for(int idx=0;idx<cursor.getColumnCount();idx++)
			   {
			       msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
			   }
			   
			   Log.d("!!!", "sms: "+msgData);
			   publishProgress(++n/cnt);
			}while(cursor.moveToNext());
		} finally {
			cursor.close();
		}
		*/
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Boolean res = false;
		try {
			doImport();
			res = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mState = TImporterState.EIdle;
		return res;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		try {
			if (result)
				mObserver.importerFinish();
			else
				mObserver.importerError();
		} catch(Exception e) {}
		
		super.onPostExecute(result);
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		try {
			mObserver.importerProgress(values[0]);
		} catch(Exception e) {}
		super.onProgressUpdate(values);
	}
	
	public void setDbWriter(IMDbWriter dbwriter) {
		mDbWriter = dbwriter;
	}

	public void setContentResolver(ContentResolver cr) {
		mCr = cr;
	}

}
