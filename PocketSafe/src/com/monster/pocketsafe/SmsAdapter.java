package com.monster.pocketsafe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import com.monster.pocketsafe.dbengine.IMDbReader;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.dbengine.TTypStatus;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.utils.MyException;

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
	
	private String mMe;
	private int mColorRed;
	private int mColorBlue;
	
	private class TQItem {
		public int mPostion;
		public SmsAdapterView mSav;
		public TQItem(int pos, SmsAdapterView sav) {
			mPostion = pos;
			mSav=sav;
		}
	}
	private LinkedList<TQItem> mQueue = new LinkedList<TQItem>();
	
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
		private String mCap;
		private IMSms mSms;
		
		public SmsLoader(int position, SmsAdapterView sav) {
			mPosition = position;
			mSav = sav;
		}

		@Override
		protected String doInBackground(Void... params) {
			
			ArrayList<IMSms> dest = new ArrayList<IMSms>(1);
			try {
				mDbReader.QuerySms().QueryByHashOrderByDat(dest, mHash, mPosition, 1);
			} catch (MyException e) {
				String err = ErrorDisplayer.getErrStr(mActivity, e.getId().getValue());
			}
			
			if (dest.size()==0) {
				return null;
			}
			
			IMSms sms = dest.get(0);
			mSms = sms;
			
			String text=null;
			for (int i=0; i<10; i++)
				try {
					text = mMain.decryptString(sms.getText());
					break;
				} catch (MyException e) {
					text = ErrorDisplayer.getErrStr(mActivity, e.getId().getValue());
				}
			
			String cap;
			if (sms.getDirection() == TTypDirection.EIncoming) { 
		    	cap = new String(mName);
		    	mSav.mCap.setTextColor(mColorRed);
		    }
		    else {
		    	cap = new String(mMe);
		    	mSav.mCap.setTextColor(mColorBlue);
		    }
		    
		    String strDat = mDateFormatDate.format(sms.getDate());
		    String strNow = mDateFormatDate.format(new Date());
		    
		    if (strDat.equals(strNow))
		    	strDat = mDateFormatSmall.format(sms.getDate());
		    else
		    	strDat = mDateFormatFull.format(sms.getDate());
		    
		    cap += " ("+strDat+")";
		    
		    mCap = cap;
		    
			return text;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			mLoader=null;
			ProcessQueue();
			
			if (result == null) return;
			if (mSav.mPos != mPosition) return;
			
			if (mSms.getIsNew() >= TTypIsNew.ENew) {
				mSms.setIsNew(TTypIsNew.EOld);
				try {
					mMain.DbWriter().SmsUpdate(mSms);
				} catch (MyException e) {
					e.printStackTrace();
					ErrorDisplayer.displayError(mActivity, e);
				}
			}
		    mSav.mCap.setText(mCap);
		    mSav.mText.setText(result);
		    
		    if (mSms.getStatus() == TTypStatus.ESendError) 
		    	mSav.mImgStatus.setVisibility(View.VISIBLE);
		    else
		    	mSav.mImgStatus.setVisibility(View.INVISIBLE);
		    
		    mSav.mItem.setVisibility(View.VISIBLE);
		    mSav.mSmsLoading.setVisibility(View.INVISIBLE);
		}
	}
	
	private SmsLoader mLoader = null;
	
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
		
		sav.mItem.setVisibility(View.INVISIBLE);
		sav.mSmsLoading.setVisibility(View.VISIBLE);
		sav.mPos = position;
		
		mQueue.add(new TQItem(position, sav));
		ProcessQueue();
		
		return v;		
	}
	
	private void ProcessQueue() {
		if (mLoader != null) return;
		
		TQItem item = mQueue.poll();
		
		while (item!=null) {
			if (item.mPostion == item.mSav.mPos) {
				mLoader = new SmsLoader(item.mPostion, item.mSav);
				mLoader.execute();
				return;
			}
			Log.d("!!!","SKIP QUEUE");
			item = mQueue.poll();
		}
		
		
	}
	
	public void Close() {
		mQueue.clear();
	}

	@Override
	public void notifyDataSetChanged() {
		mCount=-1;
		super.notifyDataSetChanged();
	}

	
}
