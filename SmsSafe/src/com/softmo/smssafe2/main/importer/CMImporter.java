package com.softmo.smssafe2.main.importer;

import java.util.Date;

import com.softmo.smssafe2.dbengine.IMDbEngine;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.TTypDirection;
import com.softmo.smssafe2.dbengine.TTypFolder;
import com.softmo.smssafe2.dbengine.TTypIsNew;
import com.softmo.smssafe2.dbengine.TTypStatus;
import com.softmo.smssafe2.sec.IMRsa;
import com.softmo.smssafe2.sec.IMSha256;
import com.softmo.smssafe2.utils.IMLocator;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class CMImporter extends AsyncTask<Void, Integer, Boolean>implements IMImporter {
	
	private static final int PAGE_SIZE = 100;
	
	private IMLocator mLocator;
	private IMImporterObserver mObserver;
	private IMDbEngine mDbEngine;
	private ContentResolver mCr;
	private IMRsa mRsa;
	private int mErrCode = TTypMyException.EImporterErrGeneral.getValue();
	
	private enum TImporterState {
		EIdle,
		EBusy,
		EFinised,
		ECanceling,
		ECanceled
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
		mState = TImporterState.EBusy;
		try {
			mObserver.importerStart();
		} catch(Exception e){};
	}

	private void writeSms(IMSms sms) throws Exception {
		IMSms sms_byId = mDbEngine.QuerySms().getBySmsId(sms.getSmsId());
		
		if (sms_byId!=null) {
			int id = sms_byId.getId();
			sms.setId(id);
			mDbEngine.TableSms().Update(sms);
		} else {
			mDbEngine.TableSms().Insert(sms);
		}
	}
	
	private void doImport() throws Exception {
		
		Cursor cursor = null;
		try {
			cursor = mCr.query(Uri.parse("content://sms"), new String[] {"count(*)"}, null, null, null);
			cursor.moveToFirst();
			
			int cnt = cursor.getInt(0);
			Log.d("!!!", "sms cnt: "+cnt);
			
			IMSms sms = mLocator.createSms();
			IMSha256 sha = mLocator.createSha256();
			
			int n=0;	
			int perc=0;
			int cur = 0;
			
			
			do {
				cursor.close();
				cursor = mCr.query(Uri.parse("content://sms"), null, null, null, "1 LIMIT "+cur+","+PAGE_SIZE);
				cur += PAGE_SIZE;
				
				if (!cursor.moveToFirst())
					return;
		
				
				do{
	
					if ( isCancelled() )
						throw new MyException(TTypMyException.EImporterCancelled);
					
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
						byte[] cPhone = mRsa.EncryptBuffer(phone.getBytes());
						byte[] cText = mRsa.EncryptBuffer(text.getBytes());
						
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
						
						writeSms(sms);
	
						
					} catch (MyException e) {
						Log.e("!!!", "MyException: "+e.getId());
						e.printStackTrace();
					} /*catch(Exception e)
					{ e.printStackTrace(); };*/
				   
					int p = ++n*100/cnt;
					if (perc != p) {
						perc = p;
						publishProgress(perc);
					}
				} while(cursor.moveToNext());
			} while (true);
		} finally {
			cursor.close();
			mDbEngine.TableSms().updateGroups();
		}
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		Boolean res = false;
		try {
			doImport();
			res = true;
		}catch (MyException e) {
			mErrCode = e.getId().getValue();
			Log.e("!!!", "Error in doImport: "+e);
		}
		catch (Exception e) {
			Log.e("!!!", "Error in doImport: "+e);
			e.printStackTrace();
		}
		
		return res;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		Log.d("!!!", "onPostExecute ");
		
		mState = TImporterState.EFinised;
		
		try {
			if (result)
				mObserver.importerFinish();
			else
				mObserver.importerError(mErrCode);
		} catch(Exception e) {}
		
		super.onPostExecute(result);
	}
	
	
	@Override
	protected void onCancelled() {
		mState = TImporterState.ECanceled;
		
		try {
			mObserver.importerError(TTypMyException.EImporterCancelled.getValue());
		} catch(Exception e) {}
		super.onCancelled();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		try {
			mObserver.importerProgress(values[0]);
		} catch(Exception e) {}
		super.onProgressUpdate(values);
	}

	public void cancelImport() {
		if (mState!=TImporterState.EBusy) 
			return;
		
		cancel(false);
		mState = TImporterState.ECanceling;
		Log.d("!!!", "Import cancelled");
	}

}
