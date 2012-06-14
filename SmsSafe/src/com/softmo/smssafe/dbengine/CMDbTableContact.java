package com.softmo.smssafe.dbengine;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import com.softmo.smssafe.dbengine.provider.IMDbProvider;
import com.softmo.smssafe.utils.IMLocator;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

public class CMDbTableContact implements IMDbTableContact {
	
	private IMLocator mLocator;
	private IMDbProvider mDbp;
	
	public CMDbTableContact(IMLocator locator) {
		mLocator = locator;
	}
	
	public IMContact getByPhone(String phone) {
		
		if (phone == null) return null;
		if (phone.length()==0) return null;
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
		String[] proj = new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup.NUMBER};
		
		Cursor c = mDbp.query(uri, proj, null, null, null);
		try {
			if (c.moveToFirst()) {
				IMContact res = mLocator.createContact();
				res.setName(c.getString(0));
				res.setPhone(c.getString(1));
				return res;
			}
			
			return null;
		} finally {
			c.close();
		}
	}

	public void SetDbProvider(IMDbProvider dbp) {
		mDbp = dbp;
	}

	public int getCount() throws MyException {
		Cursor c = mDbp.query(ContactsContract.Contacts.CONTENT_URI, new String[] {"count(*) as count"}, null, null, null);
		try {
			if (c.moveToFirst()) 
				return c.getInt(0);
			
			throw new MyException(TTypMyException.EDbErrGetCountContact);
		} finally {
			c.close();
		}
	}

}
