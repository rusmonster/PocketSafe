package com.monster.pocketsafe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.dbengine.TTypStatus;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.smsadapter;
	
	private IMMain mMain;
	private IMDbReader mDbReader;
	private final Activity mActivity;
	private String mName;
	private String mHash;
	private int mCount;
	
	private Map<Integer, String> mMap = new HashMap<Integer, String>();
	
	private String mMe;
	private String mNotFound;
	private String mLoading;
	private int mColorRed;
	private int mColorBlue;
	
	private static final SimpleDateFormat mDateFormatFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat mDateFormatSmall = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat mDateFormatDate = new SimpleDateFormat("ddMMyyyy");
	
	private static class SmsAdapterView {
		public LinearLayout mItem;
		//public ImageView mImgMsg;
		public TextView  mCap;
		public ImageView mImgStatus;
		public TextView  mText;
		public TextView	 mSmsLoading;
		public int mPos;
	}
	
	public SmsAdapter(Activity activity, IMMain main, String nam, String hash) {
		super();
		
		mActivity = activity;
		mMain = main;
		mDbReader = mMain.DbReader();
		mName = nam;
		mHash = hash;
		mCount = -1;
		
		mMe = mActivity.getResources().getString(R.string.Me);
		mNotFound = mActivity.getResources().getString(R.string.EDbIdNotFoundSms);
		mLoading = mActivity.getResources().getString(R.string.smsLoading);
		mColorRed = mActivity.getResources().getColor(R.color.red);
		mColorBlue = mActivity.getResources().getColor(R.color.blue);
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

	private class SmsLoader extends AsyncTask<Void, Void, String> {
		
		private int mPosition;
		private SmsAdapterView mSav;
		private IMSms mSms;
		private String mCap;
		
		public SmsLoader(int position, SmsAdapterView sav) {
			mPosition = position;
			mSav = sav;
		}

		@Override
		protected String doInBackground(Void... params) {
			
			String text = null;
			
			try {
				ArrayList<IMSms> dest = new ArrayList<IMSms>(1);
				mDbReader.QuerySms().QueryByHashOrderByDat(dest, mHash, mPosition, 1);
				if (dest.size()==0)
					throw new MyException(TTypMyException.EDbIdNotFoundSms);
				
				mSms = dest.get(0);
				
				if (mSms.getDirection() == TTypDirection.EIncoming) { 
			    	mCap = new String(mName);
			    }
			    else {
			    	mCap = new String(mMe);
			    }
			    
			    String strDat = mDateFormatDate.format(mSms.getDate());
			    String strNow = mDateFormatDate.format(new Date());
			    
			    if (strDat.equals(strNow))
			    	strDat = mDateFormatSmall.format(mSms.getDate());
			    else
			    	strDat = mDateFormatFull.format(mSms.getDate());
			    
			    mCap += " ("+strDat+")";
			    
				text = mMap.get(mSms.getId());
				
				if (text!=null)
					return text;
				
				publishProgress();
				
				text = mMain.decryptString(mSms.getText());
				
				
			} catch (MyException e1) {
				e1.printStackTrace();
				text = ErrorDisplayer.getErrStr(mActivity, e1.getId().getValue());
			}
			
			return text;
		}
		
		
		
		@Override
		protected void onProgressUpdate(Void... values) {
			
			if (mSav.mPos != mPosition) {
				Log.d("!!!", "onProgressUpdate: position changed");
				return;
			}
			
			mSav.mSmsLoading.setText(mLoading);
		    mSav.mItem.setVisibility(View.INVISIBLE);
		    mSav.mSmsLoading.setVisibility(View.VISIBLE);	
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) result = new String();
			
			if (mSav.mPos != mPosition) {
				Log.d("!!!", "onPostExecute: position changed");
				return;
			}

			mMap.put(mSms.getId(), result);
			
			if (mSms == null) { //sms not found in db
				mSav.mSmsLoading.setText(result); //errDescription in result
				mSav.mItem.setVisibility(View.INVISIBLE);
				mSav.mSmsLoading.setVisibility(View.VISIBLE);	
			} else {
				
				if (mSms.getDirection() == TTypDirection.EIncoming)	
					mSav.mCap.setTextColor(mColorRed); 
				else 
					mSav.mCap.setTextColor(mColorBlue);
				
				mSav.mCap.setText(mCap);
				mSav.mText.setText(result);
			    
			    if (mSms.getStatus() == TTypStatus.ESendError) 
			    	mSav.mImgStatus.setVisibility(View.VISIBLE);
			    else
			    	mSav.mImgStatus.setVisibility(View.INVISIBLE);
			    
			    mSav.mItem.setVisibility(View.VISIBLE);
			    mSav.mSmsLoading.setVisibility(View.INVISIBLE);	
			    
				if (mSms.getIsNew() >= TTypIsNew.ENew) {
					mSms.setIsNew(TTypIsNew.EOld);
					try {
						mMain.DbWriter().SmsUpdate(mSms);
					} catch (MyException e) {
						e.printStackTrace();
						ErrorDisplayer.displayError(mActivity, e);
					}
				}
			}
		}
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("!!!", "SmsAdapter::getView: "+position);
		
		SmsAdapterView sav;
		View v = convertView;
		
		if(convertView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			v = inflater.inflate(LAYOUT, null, true);
			
			sav = new SmsAdapterView();
			sav.mSmsLoading = (TextView)v.findViewById(R.id.smsLoading);
			
			//sav.mImgMsg = (ImageView)v.findViewById(R.id.msg_icon);
			sav.mCap	= (TextView)v.findViewById(R.id.smsCap);
			sav.mImgStatus 	= (ImageView)v.findViewById(R.id.msg_status);
			sav.mText		= (TextView)v.findViewById(R.id.smsText);
			sav.mItem		= (LinearLayout)v.findViewById(R.id.smsItem);
			
			v.setTag(sav);
		} else {
			sav = (SmsAdapterView) v.getTag();
		}
		
		sav.mPos = position;
		
		(new SmsLoader(position, sav)).execute();
		
		return v;		
	}
	
	
	@Override
	public void notifyDataSetChanged() {
		mCount=-1;
		super.notifyDataSetChanged();
	}

	
}
