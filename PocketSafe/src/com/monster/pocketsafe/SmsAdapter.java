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
		
		public SmsLoader(int position, SmsAdapterView sav, IMSms sms) {
			mPosition = position;
			mSav = sav;
			mSms = sms;
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
			
			if (result == null) return;
			
			if (mSav.mPos != mPosition) {
				Log.d("!!!", "onPostExecute: position changed");
				return;
			}
			
			mMap.put(mSms.getId(), result);
			FillView(mSav, mSms, result);

		}
	}
	
	private void FillView(SmsAdapterView sav, IMSms sms, String text) {
		if (sms.getIsNew() >= TTypIsNew.ENew) {
			sms.setIsNew(TTypIsNew.EOld);
			try {
				mMain.DbWriter().SmsUpdate(sms);
			} catch (MyException e) {
				e.printStackTrace();
				ErrorDisplayer.displayError(mActivity, e);
			}
		}
		
		
		String cap;
		if (sms.getDirection() == TTypDirection.EIncoming) { 
	    	cap = new String(mName);
	    	sav.mCap.setTextColor(mColorRed);
	    }
	    else {
	    	cap = new String(mMe);
	    	sav.mCap.setTextColor(mColorBlue);
	    }
	    
	    String strDat = mDateFormatDate.format(sms.getDate());
	    String strNow = mDateFormatDate.format(new Date());
	    
	    if (strDat.equals(strNow))
	    	strDat = mDateFormatSmall.format(sms.getDate());
	    else
	    	strDat = mDateFormatFull.format(sms.getDate());
	    
	    cap += " ("+strDat+")";
	    
		sav.mCap.setText(cap);
		sav.mText.setText(text);
	    
	    if (sms.getStatus() == TTypStatus.ESendError) 
	    	sav.mImgStatus.setVisibility(View.VISIBLE);
	    else
	    	sav.mImgStatus.setVisibility(View.INVISIBLE);
	    
	    sav.mItem.setVisibility(View.VISIBLE);
	    sav.mSmsLoading.setVisibility(View.INVISIBLE);		
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
		
		String err = mNotFound;
		ArrayList<IMSms> dest = new ArrayList<IMSms>(1);
		try {
			mDbReader.QuerySms().QueryByHashOrderByDat(dest, mHash, position, 1);
		} catch (MyException e) {
			err = ErrorDisplayer.getErrStr(mActivity, e.getId().getValue());
		}
		
		if (dest.size()==0) {
			sav.mSmsLoading.setText(err);
			sav.mItem.setVisibility(View.INVISIBLE);
			sav.mSmsLoading.setVisibility(View.VISIBLE);
			return null;
		}
		
		IMSms sms = dest.get(0);
		String text = mMap.get(sms.getId());
		
		if (text != null) {
			FillView(sav, sms, text);
		} else {
			sav.mItem.setVisibility(View.INVISIBLE);
			sav.mSmsLoading.setVisibility(View.VISIBLE);
			(new SmsLoader(position, sav, sms)).execute();
		}
		
		return v;		
	}
	
	
	@Override
	public void notifyDataSetChanged() {
		mCount=-1;
		super.notifyDataSetChanged();
	}

	
}
