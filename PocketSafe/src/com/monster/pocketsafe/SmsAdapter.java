package com.monster.pocketsafe;

import java.text.SimpleDateFormat;
import java.util.List;

import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypDirection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter {

	private List<IMSms> mList;
	private final Context mContext;
	private String mName;
	private SimpleDateFormat mDateFormatFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private View mEditor;
	//private SimpleDateFormat mDateFormatTime = new SimpleDateFormat("HH:mm:ss");
	
	public SmsAdapter(Context context, List<IMSms> list, String nam) {
		mContext = context;
		mList = list;
		mName = nam;
	}
	
	public int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return mList.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		IMSms sms = mList.get(position);

	    TextView tv = new TextView(mContext);
	    String txt;
	    
	    if (sms.getDirection() == TTypDirection.EIncoming) 
	    	txt = new String(mName);
	    else
	    	txt = new String("Me");
	    
	    txt += ": "+sms.getText()+"\n"+mDateFormatFull.format(sms.getDate());
	    
	    tv.setText(txt);
	    return tv;		}

}
