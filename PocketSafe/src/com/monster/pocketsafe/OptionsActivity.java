package com.monster.pocketsafe;

import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.utils.MyException;

import android.os.Bundle;

public class OptionsActivity extends CMBasePreferenceActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);
	}

	public void onMainBind() throws MyException {
		
	}

	public void listenerEvent(IMEvent event) throws Exception {
		
	}


}
