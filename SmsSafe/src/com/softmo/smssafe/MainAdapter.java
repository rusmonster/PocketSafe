package com.softmo.smssafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.softmo.smssafe.R;
import com.softmo.smssafe.dbengine.IMContact;
import com.softmo.smssafe.dbengine.IMDbReader;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.IMSmsGroup;
import com.softmo.smssafe.main.IMMain;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {

	private static final int LAYOUT = R.layout.mainitem;
	
	private Activity mActivity;
	private IMMain mMain;
	private IMDbReader mDbReader;
	private int mCount;
	
	private Map<Integer, String> mMap;

	private static class MainAdapterView {
		public LinearLayout mItem;
		public TextView  mText;
		public TextView	 mLoading;
		public int mPos;
	}
	
	public MainAdapter(IMMain main, Activity act) {
		super();
		
		mActivity = act;
		mMain = main;
		mDbReader = mMain.DbReader();
		mCount = -1;
		mMap = new HashMap<Integer, String>();
		Log.d("!!!", "MAP created");
	}
	
	public Map<Integer, String> getMap() {
		return mMap;
	}
	
	public void setMap(Map<Integer,String> map) {
		if (map!=null) {
			mMap=map;
			Log.d("!!!", "MAP setted");
		}
	}
	
	public int getCount() {
		if (mCount == -1) {
			try {
				mCount = mDbReader.QuerySms().getCountGroup();
			} catch (MyException e) {
				e.printStackTrace();
				return 0;
			}
		}
		return mCount;
	}

	public IMSms getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		Log.d("!!!", "MainAdapter::getItemId");
		
		long res=-1;
		
		ArrayList<IMSmsGroup> dest = new ArrayList<IMSmsGroup>(1);
		try {
			mDbReader.QuerySms().QueryGroupOrderByMaxDatDesc(dest, position, 1);
		} catch (MyException e) {
			e.printStackTrace();
		}
		
		if (dest.size()>0)
			res = dest.get(0).getId();
		
		return res;
	}

	private class MainLoader extends AsyncTask<Void, Void, String> {
		
		private int mPosition;
		private MainAdapterView mMav;
		private IMSmsGroup mGroup;
		
		public MainLoader(int position, MainAdapterView sav, IMSmsGroup group) {
			mPosition = position;
			mMav = sav;
			mGroup = group;
		}

		public int getPosition() {
			return mPosition;
		}
		
		public MainAdapterView getMav() {
			return mMav;
		}
		
		@Override
		protected String doInBackground(Void... params) {
			
			String name = null;
			try {
				try {
					name = mMain.decryptString(mGroup.getPhone());
				}catch (MyException e){
					name = ErrorDisplayer.getErrStr(mActivity, e.getId().getValue());
				}
				
				IMContact cont = mMain.DbReader().QueryContact().getByPhone(name);
				if (cont != null)
					name = cont.getName();
				
			} catch (Exception e1) {
				e1.printStackTrace();
				name = ErrorDisplayer.getErrStr(mActivity, TTypMyException.EErrUnknown.getValue());
			}
			
			return name;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			mLoadersCount--;
			doLoadQuery();
			
			if (result == null) return;
			
			mMap.put(mGroup.getId(), result);
			
			if (mMav.mPos != mPosition) {
				Log.d("!!!", "onPostExecute: position changed");
				return;
			}
			
			FillView(mMav, mGroup, result);

		}
	}
	
	private LinkedList<MainLoader> mLoadQuery = new LinkedList<MainLoader>();
	private static final int MAX_LOADERS = 5;
	private int mLoadersCount = 0;
	
	void doLoadQuery() {
		try {
			while (mLoadersCount < MAX_LOADERS) {
				MainLoader loader = mLoadQuery.poll();
				if (loader==null) break;
				
				if (loader.getPosition() == loader.getMav().mPos) {
					loader.execute();
					mLoadersCount++;
					Log.d("!!!", "doLoadQuery: mLoadersCount="+mLoadersCount);
				} else {
					Log.i("!!!", "doLoadQuery: skip loader");
				}
			}
		} catch(Exception e) {
			Log.e("!!!", "Error in (new MainLoader(position, sav, sms)).execute(): "+e.getMessage());
			e.printStackTrace();
		}
	
	}
	
	private void FillView(MainAdapterView mav, IMSmsGroup group, String text) {
		if (group.getCountNew()>0)
			text = new String(text+" ("+group.getCountNew()+"/"+group.getCount()+")");
		else
			text = new String(text+" ("+group.getCount()+")");
				
		mav.mText.setText(text);
		mav.mItem.setVisibility(View.VISIBLE);
		mav.mLoading.setVisibility(View.INVISIBLE);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("!!!", "MainAdapter::getView: "+position);
		
		MainAdapterView mav;
		View v = convertView;
		
		if(v == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			v = inflater.inflate(LAYOUT, null, true);
			
			mav = new MainAdapterView();
			mav.mLoading = (TextView)v.findViewById(R.id.Loading);
			mav.mText		= (TextView)v.findViewById(R.id.mainText);
			mav.mItem		= (LinearLayout)v.findViewById(R.id.mainItem);
			
			v.setTag(mav);
		} else {
			mav = (MainAdapterView) v.getTag();
		}
		
		mav.mPos = position;
		
		ArrayList<IMSmsGroup> dest = new ArrayList<IMSmsGroup>(1);
		try {
			mDbReader.QuerySms().QueryGroupOrderByMaxDatDesc(dest, position, 1);
		} catch (MyException e) {
			e.printStackTrace();
		}
		
		if (dest.size()==0) {
			mav.mLoading.setText(mActivity.getResources().getString(R.string.EDbIdNotFoundGroup));
			mav.mItem.setVisibility(View.INVISIBLE);
			mav.mLoading.setVisibility(View.VISIBLE);
			return null;
		}
		
		IMSmsGroup group = dest.get(0);
		String text = mMap.get(group.getId());
		
		if (text != null) {
			Log.w("!!!", "MainAdapter: text cached: "+position);
			FillView(mav, group, text);
		} else {
			Log.w("!!!", "MainAdapter: text NOT cached: "+position);
			mav.mItem.setVisibility(View.INVISIBLE);
			mav.mLoading.setVisibility(View.VISIBLE);
			mLoadQuery.add( new MainLoader(position, mav, group) );
			doLoadQuery();
		}
		
		return v;		
	}
	
	
	@Override
	public void notifyDataSetChanged() {
		mCount=-1;
		super.notifyDataSetChanged();
	}

	
}
