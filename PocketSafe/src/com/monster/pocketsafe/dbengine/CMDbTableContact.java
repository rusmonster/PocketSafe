package com.monster.pocketsafe.dbengine;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMDbTableContact implements IMDbTableContact {
	
	private IMLocator mLocator;
	private ContentResolver mCr;
	
	public CMDbTableContact(IMLocator locator) {
		mLocator = locator;
	}
	
	public IMContact getByPhone(String phone) {
		
		if (phone == null) return null;
		if (phone.length()==0) return null;
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
		String[] proj = new String[] {PhoneLookup.DISPLAY_NAME, PhoneLookup.NUMBER};
		
		Cursor c = mCr.query(uri, proj, null, null, null);
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

	public void SetContentResolver(ContentResolver cr) {
		mCr = cr;
	}

	public int getCount() throws MyException {
		Cursor c = mCr.query(ContactsContract.Contacts.CONTENT_URI, new String[] {"count(*) as count"}, null, null, null);
		try {
			if (c.moveToFirst()) 
				return c.getInt(0);
			
			throw new MyException(TTypMyException.EDbErrGetCountContact);
		} finally {
			c.close();
		}
	}

}
