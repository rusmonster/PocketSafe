package com.softmo.smssafe2.dbengine;

import android.content.ContentResolver;

public interface IMDbTableContact extends IMDbQueryContact {
	void SetContentResolver(ContentResolver cr);
}
