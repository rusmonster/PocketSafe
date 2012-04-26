package com.monster.pocketsafe;

import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.utils.MyException;

import android.os.Bundle;

public class AboutActivity extends CMBaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.about);
	}

	public void onMainBind() throws MyException {
		
	}

	public void listenerEvent(IMEvent event) throws Exception {
		
	}

}
