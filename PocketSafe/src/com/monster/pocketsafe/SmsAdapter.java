package com.monster.pocketsafe;

import java.util.List;

import com.monster.pocketsafe.dbengine.IMSms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SmsAdapter extends BaseAdapter {

	private List<IMSms> mList;
	private final Context mContext;
	
	public SmsAdapter(Context context, List<IMSms> list) {
		mContext = context;
		mList = list;
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
	    tv.setText(sms.getPhone()+": "+sms.getText());
	    return tv;
	}

}
