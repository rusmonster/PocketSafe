package com.softmo.smssafe2.views.smsadapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.softmo.smssafe2.R;
import com.softmo.smssafe2.dbengine.IMSms;
import com.softmo.smssafe2.dbengine.TTypDirection;
import com.softmo.smssafe2.dbengine.TTypIsNew;
import com.softmo.smssafe2.dbengine.TTypStatus;
import com.softmo.smssafe2.main.IMMain;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.views.ErrorDisplayer;

public class SmsAdapterSimple extends SmsAdapter {
	
	private static final int LAYOUT = R.layout.smsadaptersimple;

	private static class SmsAdapterSimpleView extends SmsAdapterView {
		public LinearLayout mItem;
		//public ImageView mImgMsg;
		public TextView  mCap;
		public ImageView mImgStatus;
		public TextView  mText;
		public TextView	 mSmsLoading;
		
	}
	
	public SmsAdapterSimple(ListActivity activity, IMMain main, String nam,	String hash) {
		super(activity, main, nam, hash);
	}

	@Override
	protected void FillView(SmsAdapterView view, IMSms sms, String text) {
		SmsAdapterSimpleView sav = (SmsAdapterSimpleView)view;
		
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
		
		SmsAdapterSimpleView sav;
		View v = convertView;
		
		if(convertView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			v = inflater.inflate(LAYOUT, null, true);
			
			sav = new SmsAdapterSimpleView();
			sav.mSmsLoading = (TextView)v.findViewById(R.id.Loading);
			
			//sav.mImgMsg = (ImageView)v.findViewById(R.id.msg_icon);
			sav.mCap	= (TextView)v.findViewById(R.id.smsCap);
			sav.mImgStatus 	= (ImageView)v.findViewById(R.id.msg_status);
			sav.mText		= (TextView)v.findViewById(R.id.smsText);
			sav.mItem		= (LinearLayout)v.findViewById(R.id.smsItem);
			
			v.setTag(sav);
		} else {
			sav = (SmsAdapterSimpleView) v.getTag();
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
			Log.d("!!!", "SmsAdapter: text cached: "+position);
			FillView(sav, sms, text);
		} else {
			sav.mItem.setVisibility(View.INVISIBLE);
			sav.mSmsLoading.setVisibility(View.VISIBLE);
			mLoadQuery.add( new SmsLoader(position, sav, sms) );
			doLoadQuery();
		}
		
		return v;		
	}
	
}
