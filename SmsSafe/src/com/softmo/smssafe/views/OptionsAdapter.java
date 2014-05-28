package com.softmo.smssafe.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.softmo.smssafe.R;
import com.softmo.smssafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.softmo.smssafe.dbengine.IMSetting;
import com.softmo.smssafe.utils.MyException;

public class OptionsAdapter extends BaseAdapter {

	public static enum ITEMS {
		EChangePass,
		EPassTimeout,
		ENotification,
		ESmsListType,
		EImport
	}

	private OptionsActivity mActivity;

	public OptionsAdapter(OptionsActivity activity) {
		mActivity = activity;
	}

	public int getCount() {
		return ITEMS.values().length;
	}

	public long getItemId(int position) {
		return position;
	}

	public Object getItem(int position) {
		return null;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = mActivity.getLayoutInflater();
		View v = null;

		switch (ITEMS.values()[position]) {
			case EChangePass:
				v = inflater.inflate(R.layout.listbaseitem, null, true);
				((TextView) v.findViewById(R.id.text1)).setText(mActivity.getResources().getString(R.string.opt_changepass));
				break;

			case EPassTimeout:
				v = inflater.inflate(R.layout.listbaseitem, null, true);
				((TextView) v.findViewById(R.id.text1)).setText(mActivity.getResources().getString(R.string.opt_passtimeout));

				try {
					IMSetting set = mActivity.getHelper().getLocator().createSetting();
					mActivity.getHelper().getMain().DbReader().QuerySetting().getById(set, TTypSetting.EPassTimout);
					String val = set.getStrVal();

					for (int i = 0; i < mActivity.mTimout_vals.length; i++)
						if (mActivity.mTimout_vals[i].equals(val)) {
							val = mActivity.mTimout_labels[i];
							break;
						}

					((TextView) v.findViewById(R.id.text2)).setText(val);
				} catch (MyException e) {
				}
				break;

			case ENotification: {
				v = inflater.inflate(R.layout.listbaseitem, null, true);
				((TextView) v.findViewById(R.id.text1)).setText(mActivity.getResources().getString(R.string.opt_notificator_title));
				int val = 0;
				try {
					IMSetting set = mActivity.getHelper().getLocator().createSetting();
					mActivity.getHelper().getMain().DbReader().QuerySetting().getById(set, TTypSetting.ENotification);
					val = set.getIntVal();

				} catch (MyException e) {
				}

				int k = 0;
				if (val >= 0 && val < mActivity.mNotification_labels.length) k = val;
				String text = mActivity.mNotification_labels[k];

				((TextView) v.findViewById(R.id.text2)).setText(text);
			}
			break;
			case ESmsListType: {
				v = inflater.inflate(R.layout.listbaseitem, null, true);
				((TextView) v.findViewById(R.id.text1)).setText(mActivity.getResources().getString(R.string.opt_sms_list));
				int val = 0;
				try {
					IMSetting set = mActivity.getHelper().getLocator().createSetting();
					mActivity.getHelper().getMain().DbReader().QuerySetting().getById(set, TTypSetting.ESmsListTyp);
					val = set.getIntVal();

				} catch (MyException e) {
				}

				int k = 0;
				if (val >= 0 && val < mActivity.mSmsListTyp_labels.length) k = val;
				String text = mActivity.mSmsListTyp_labels[k];

				((TextView) v.findViewById(R.id.text2)).setText(text);
			}
			break;

			case EImport:
				v = inflater.inflate(R.layout.listbaseitem, null, true);
				((TextView) v.findViewById(R.id.text1)).setText(mActivity.getResources().getString(R.string.opt_import));
				break;

			default: //should never happens
				v = inflater.inflate(R.layout.listbaseitem, null, true);
		}

		return v;
	}

}
