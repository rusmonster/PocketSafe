package com.softmo.smssafe.dbengine;

import android.content.ContentResolver;

public interface IMDbTableContact extends IMDbQueryContact {
	void SetContentResolver(ContentResolver cr);
}
