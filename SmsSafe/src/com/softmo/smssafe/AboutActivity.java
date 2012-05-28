package com.softmo.smssafe;

import CMBaseActivity;

import com.softmo.libsafe.main.IMEvent;
import com.softmo.libsafe.utils.MyException;
import com.softmo.smssafe.R;

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
