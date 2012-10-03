package com.softmo.smssafe.views.smsadapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.ListActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softmo.smssafe.R;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.TTypDirection;
import com.softmo.smssafe.dbengine.TTypIsNew;
import com.softmo.smssafe.dbengine.TTypStatus;
import com.softmo.smssafe.main.IMMain;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.views.ErrorDisplayer;

public class SmsAdapterBubble extends SmsAdapter {

	private static final int LAYOUT = R.layout.smsadapterbubble;

	private static class SmsAdapterBubbleView extends SmsAdapterView {
		public LinearLayout mWrapper;
		public RelativeLayout mBubble;
		public TextView	 mSmsStub;
		public LinearLayout mItem;
		public TextView  mCap;
		public ImageView mImgStatus;
		public TextView  mText;
		
	}	
	public SmsAdapterBubble(ListActivity activity, IMMain main, String nam,	String hash) {
		super(activity, main, nam, hash);
		activity.getListView().setDivider(null);
	}

	@Override
	protected void FillView(SmsAdapterView view, IMSms sms, String text) {
		SmsAdapterBubbleView sav = (SmsAdapterBubbleView)view;
		
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
	    sav.mSmsStub.setVisibility(View.INVISIBLE);		
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("!!!", "SmsAdapter::getView: "+position);
		
		SmsAdapterBubbleView sav;
		View v = convertView;
		
		if(convertView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			v = inflater.inflate(LAYOUT, null, true);
			
			sav = new SmsAdapterBubbleView();
			sav.mSmsStub = (TextView)v.findViewById(R.id.SmsStub);
			sav.mCap	= (TextView)v.findViewById(R.id.smsCap);
			sav.mImgStatus 	= (ImageView)v.findViewById(R.id.msg_status);
			sav.mText		= (TextView)v.findViewById(R.id.smsText);
			sav.mItem		= (LinearLayout)v.findViewById(R.id.smsItem);
			sav.mWrapper	= (LinearLayout)v.findViewById(R.id.smswrapper);
			sav.mBubble		= (RelativeLayout)v.findViewById(R.id.smsbubble);
			
			v.setTag(sav);
		} else {
			sav = (SmsAdapterBubbleView) v.getTag();
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
			sav.mSmsStub.setText(err);
			return null;
		}
		
		IMSms sms = dest.get(0);

		if (sms.getDirection() == TTypDirection.EIncoming) { 
	    	sav.mBubble.setBackgroundResource(R.drawable.bubble_yellow);
			sav.mWrapper.setGravity(Gravity.LEFT);	    	
	    }
	    else {
	    	sav.mBubble.setBackgroundResource(R.drawable.bubble_green);
			sav.mWrapper.setGravity(Gravity.RIGHT);	    	
	    }
		
		String text = mMap.get(sms.getId());
		
		if (text != null) {
			Log.d("!!!", "SmsAdapter: text cached: "+position);
			FillView(sav, sms, text);
		} else {
			sav.mSmsStub.setText(R.string.Loading);
			showStub(sav);
			mLoadQuery.add( new SmsLoader(position, sav, sms) );
			doLoadQuery();
		}
		
		return v;		
	}
	
	private void showStub(SmsAdapterBubbleView sav) {
		sav.mCap.setText(""); 
		sav.mText.setText("");
		sav.mSmsStub.setVisibility(View.VISIBLE);
		sav.mItem.setVisibility(View.INVISIBLE);
		
	}
}
