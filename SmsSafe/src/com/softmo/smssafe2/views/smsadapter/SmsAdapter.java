package com.softmo.smssafe2.views.smsadapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.softmo.smssafe2.R;
import com.softmo.smssafe2.dbengine.IMDbReader;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.views.ErrorDisplayer;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

public abstract class SmsAdapter extends BaseAdapter {

	
	protected IMMain mMain;
	protected IMDbReader mDbReader;
	protected final Activity mActivity;
	protected String mName;
	protected String mHash;
	protected int mCount;
	
	protected Map<Integer, String> mMap = new HashMap<Integer, String>();
	
	protected String mMe;
	protected String mNotFound;
	protected int mColorRed;
	protected int mColorBlue;
	
	protected static final SimpleDateFormat mDateFormatFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	protected static final SimpleDateFormat mDateFormatSmall = new SimpleDateFormat("HH:mm");
	protected static final SimpleDateFormat mDateFormatDate = new SimpleDateFormat("ddMMyyyy");
	
	protected static class SmsAdapterView {
		public int mPos;
	}

	public static SmsAdapter Factory(TTypSmsList typ, ListActivity activity, IMMain main, String nam, String hash) {
		switch (typ) {
		case EFlat:
			return new SmsAdapterSimple(activity, main, nam, hash);
		case EChat:
			return new SmsAdapterBubble(activity, main, nam, hash);
			
		default:
			return new SmsAdapterSimple(activity, main, nam, hash);
		}
		
	}
	
	public SmsAdapter(ListActivity activity, IMMain main, String nam, String hash) {
		super();
		
		mActivity = activity;
		mMain = main;
		mDbReader = mMain.DbReader();
		mName = nam;
		mHash = hash;
		mCount = -1;
		
		mMe = mActivity.getResources().getString(R.string.Me);
		mNotFound = mActivity.getResources().getString(R.string.EDbIdNotFoundSms);
		mColorRed = mActivity.getResources().getColor(R.color.red);
		mColorBlue = mActivity.getResources().getColor(R.color.blue);
	}
	
	public Map<Integer, String> getMap() {
		return mMap;
	}
	
	public void setMap(Map<Integer,String> map) {
		if (map!=null)
			mMap=map;
	}
	
	public int getCount() {
		//Log.d("!!!", "SmsAdapter::getCount");
		if (mCount == -1) {
			try {
				mCount = mDbReader.QuerySms().getCountByHash(mHash);
			} catch (MyException e) {
				e.printStackTrace();
				return 0;
			}
		}
		return mCount;
	}

	public IMSms getItem(int position) {
		Log.d("!!!", "SmsAdapter::getItem");
		return null;
	}

	public long getItemId(int position) {
		Log.d("!!!", "SmsAdapter::getItemId");
		
		long res=-1;
		
		ArrayList<IMSms> dest = new ArrayList<IMSms>(1);
		try {
			mDbReader.QuerySms().QueryByHashOrderByDat(dest, mHash, position, 1);
		} catch (MyException e) {
			e.printStackTrace();
			ErrorDisplayer.displayError(mActivity, e);
		}
		
		if (dest.size()>0)
			res = dest.get(0).getId();
		
		return res;
	}

	protected class SmsLoader extends AsyncTask<Void, Void, String> {
		
		private int mPosition;
		private SmsAdapterView mSav;
		private IMSms mSms;
		
		public SmsLoader(int position, SmsAdapterView sav, IMSms sms) {
			mPosition = position;
			mSav = sav;
			mSms = sms;
		}

		public int getPosition() {
			return mPosition;
		}
		
		public SmsAdapterView getSav() {
			return mSav;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			
			String text = null;
			
			try {
				text = mMain.decryptString(mSms.getText());
			} catch (MyException e1) {
				e1.printStackTrace();
				text = ErrorDisplayer.getErrStr(mActivity, e1.getId().getValue());
			}
			
			return text;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			mLoadersCount--;
			doLoadQuery();
			
			if (result == null) return;
			
			mMap.put(mSms.getId(), result);
			
			if (mSav.mPos != mPosition) {
				Log.d("!!!", "onPostExecute: position changed");
				return;
			}
			
			FillView(mSav, mSms, result);

		}
	}
	
	protected LinkedList<SmsLoader> mLoadQuery = new LinkedList<SmsLoader>();
	private static final int MAX_LOADERS = 5;
	private int mLoadersCount = 0;
	
	void doLoadQuery() {
		try {
			while (mLoadersCount < MAX_LOADERS) {
				SmsLoader loader = mLoadQuery.poll();
				if (loader==null) break;
				
				if (loader.getPosition() == loader.getSav().mPos) {
					loader.execute();
					mLoadersCount++;
					Log.d("!!!", "doLoadQuery: mLoadersCount="+mLoadersCount);
				} else {
					Log.i("!!!", "doLoadQuery: skip loader");
				}
			}
		} catch(Exception e) {
			Log.e("!!!", "Error in (new SmsLoader(position, sav, sms)).execute(): "+e.getMessage());
			e.printStackTrace();
		}
	
	}
	
	
	
	
	@Override
	public void notifyDataSetChanged() {
		mCount=-1;
		super.notifyDataSetChanged();
	}

	protected abstract void FillView(SmsAdapterView view, IMSms sms, String text);
}
