package com.monster.pocketsafe;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class OptionsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v("!!!", "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

}
