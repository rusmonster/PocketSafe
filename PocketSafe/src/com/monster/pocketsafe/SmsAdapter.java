package com.monster.pocketsafe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;
import com.monster.pocketsafe.dbengine.TTypStatus;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsAdapter extends ArrayAdapter<IMSms> {

	private static final int LAYOUT = R.layout.smsadapter;
	
	private List<IMSms> mList;
	private final Activity mActivity;
	private String mName;
	
	private static final SimpleDateFormat mDateFormatFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final SimpleDateFormat mDateFormatSmall = new SimpleDateFormat("HH:mm");
	private static final SimpleDateFormat mDateFormatDate = new SimpleDateFormat("ddMMyyyy");
	
	private static class SmsAdapterView {
		//public ImageView mImgMsg;
		public TextView  mCap;
		public ImageView mImgStatus;
		public TextView  mText;
	}
	
	public SmsAdapter(Activity activity, List<IMSms> list, String nam) {
		super(activity, LAYOUT, list);
		
		mActivity = activity;
		mList = list;
		mName = nam;
	}
	
	public int getCount() {
		return mList.size();
	}

	public IMSms getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return mList.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		IMSms sms = mList.get(position);
	
		SmsAdapterView sav;
		View v = convertView;
		
		if(convertView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			v = inflater.inflate(LAYOUT, null, true);
			
			sav = new SmsAdapterView();
			//sav.mImgMsg = (ImageView)v.findViewById(R.id.msg_icon);
			sav.mCap	= (TextView)v.findViewById(R.id.smsCap);
			sav.mImgStatus 	= (ImageView)v.findViewById(R.id.msg_status);
			sav.mText		= (TextView)v.findViewById(R.id.smsText);
			
			v.setTag(sav);
		} else {
			sav = (SmsAdapterView) v.getTag();
		}
		
		String cap;
	    if (sms.getDirection() == TTypDirection.EIncoming) { 
	    	cap = new String(mName);
	    	sav.mCap.setTextColor(mActivity.getResources().getColor(R.color.red));
	    }
	    else {
	    	cap = mActivity.getResources().getString(R.string.Me);
	    	sav.mCap.setTextColor(mActivity.getResources().getColor(R.color.blue));
	    }
	    
	    String strDat = mDateFormatDate.format(sms.getDate());
	    String strNow = mDateFormatDate.format(new Date());
	    if (strDat.equals(strNow))
	    	strDat = mDateFormatSmall.format(sms.getDate());
	    else
	    	strDat = mDateFormatFull.format(sms.getDate());
	    
	    cap += " ("+strDat+")";
	    
	    sav.mCap.setText(cap);
	    sav.mText.setText(sms.getText());
	    
	    if (sms.getStatus() == TTypStatus.ESendError) 
	    	sav.mImgStatus.setVisibility(View.VISIBLE);
	    else
	    	sav.mImgStatus.setVisibility(View.INVISIBLE);
		
		return v;		
	}

}
