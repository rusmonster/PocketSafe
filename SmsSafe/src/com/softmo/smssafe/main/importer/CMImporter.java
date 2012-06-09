package com.softmo.smssafe.main.importer;

import java.util.Date;

import com.softmo.smssafe.dbengine.IMDbEngine;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.TTypDirection;
import com.softmo.smssafe.dbengine.TTypFolder;
import com.softmo.smssafe.dbengine.TTypIsNew;
import com.softmo.smssafe.dbengine.TTypStatus;
import com.softmo.smssafe.main.CMMain;
import com.softmo.smssafe.main.IMDbWriter;
import com.softmo.smssafe.sec.IMRsa;
import com.softmo.smssafe.sec.IMSha256;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CMImporter extends AsyncTask<Void, Integer, Boolean>implements IMImporter {
	
	private IMLocator mLocator;
	private IMImporterObserver mObserver;
	private IMDbEngine mDbEngine;
	private ContentResolver mCr;
	private IMRsa mRsa;
	
	private enum TImporterState {
		EIdle,
		EBusy
	}

	TImporterState mState = TImporterState.EIdle;
	
	public CMImporter(IMLocator locator) {
		mLocator = locator;
	}
	
	public void setObserver(IMImporterObserver observer) {
		mObserver = observer;
	}

	public void setDbEngine(IMDbEngine dbEngine) {
		mDbEngine = dbEngine;
	}

	public void setContentResolver(ContentResolver cr) {
		mCr = cr;
	}

	public void setRsa(IMRsa rsa) {
		mRsa = rsa;
	}
	
	public void startImport() throws MyException {
		if (mState != TImporterState.EIdle)
			throw new MyException(TTypMyException.EImporterErrBusy);
		
		if (mObserver==null || mDbEngine==null || mCr==null || mLocator==null || mRsa==null)
			throw new MyException(TTypMyException.EImporterNullParam); 
		
		execute();
		mState = TImporterState.EIdle;
		try {
			mObserver.importerStart();
		} catch(Exception e){};
	}

	
	private void doImport() {
		
		Cursor cursor = mCr.query(Uri.parse("content://sms"), new String[] {"count(*)"}, null, null, null);
		try {
			cursor.moveToFirst();
			
			int cnt = cursor.getInt(0);
			Log.d("!!!", "sms cnt: "+cnt);
			cursor.close();
			
			cursor = mCr.query(Uri.parse("content://sms"), null, null, null, null);
			
			if (!cursor.moveToFirst())
				return;
	
			IMSms sms = mLocator.createSms();
			
			IMSha256 sha = mLocator.createSha256();
			
			int n=0;
			do{

				try {
					int col = cursor.getColumnIndex("address");
					String phone = cursor.getString(col);
					
					col = cursor.getColumnIndex("body");
					String text  = cursor.getString(col);
	
					col = cursor.getColumnIndex("read");
					int read  = cursor.getInt(col);
					
					col = cursor.getColumnIndex("date");
					long dat  = cursor.getLong(col);

					col = cursor.getColumnIndex("type");
					int typ  = cursor.getInt(col);
					
					col = cursor.getColumnIndex("_id");
					int sms_id  = cursor.getInt(col);
					
					String hash = sha.getHash(phone);
					byte[] cPhone = mRsa.EncryptBuffer(phone.getBytes(IMDbEngine.ENCODING));
					byte[] cText = mRsa.EncryptBuffer(text.getBytes(IMDbEngine.ENCODING));
					
					sms.setHash(hash);
					sms.setPhone( new String(cPhone, IMDbEngine.ENCODING));
					sms.setText( new String(cText, IMDbEngine.ENCODING));
					sms.setDate( new Date(dat) );
					sms.setDirection((typ==1)?TTypDirection.EIncoming:TTypDirection.EOutgoing);
					
					int folder;
					/*
					 	MESSAGE_TYPE_ALL    = 0;
						MESSAGE_TYPE_INBOX  = 1;
						MESSAGE_TYPE_SENT   = 2;
						MESSAGE_TYPE_DRAFT  = 3;
						MESSAGE_TYPE_OUTBOX = 4;
						MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages
						MESSAGE_TYPE_QUEUED = 6; // for messages to send later
					 */
					switch(typ) {
						case 1: folder=TTypFolder.EInbox; break;
						case 2: folder=TTypFolder.ESent; break;
						default:folder=TTypFolder.EOutbox; break;
					}
					sms.setFolder(folder);
					
					sms.setIsNew( (read==0)?TTypIsNew.ENew:TTypIsNew.EOld );
					
					int status;
					switch(typ) {
						case 1: status=TTypStatus.ERecv; break;
						case 2: status=TTypStatus.ESent; break;
						default:status=TTypStatus.ESendError; break;
					}
					sms.setStatus( status );
					sms.setSmsId(sms_id);
					
					IMSms sms_byId = mDbEngine.QuerySms().getBySmsId(sms_id);
					if (sms_byId!=null) {
						int id = sms_byId.getId();
						sms.setId(id);
						mDbEngine.TableSms().Update(sms);
					} else {
						mDbEngine.TableSms().Insert(sms);
					}
					
				} catch(Exception e){ e.printStackTrace(); };
			   
			   publishProgress(++n*100/cnt);
			} while(cursor.moveToNext());
		} finally {
			cursor.close();
		}
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

}
