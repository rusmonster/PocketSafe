package com.softmo.smssafe.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.softmo.smssafe.R;
import com.softmo.smssafe.main.IMEvent;
import com.softmo.smssafe.utils.MyException;

public class AboutActivity extends CMBaseActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.about);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public void onMainBind() throws MyException {
		
	}

	public void listenerEvent(IMEvent event) throws Exception {
		
	}
	
	public void feedbackClick(View v) {
		try {
			String packageName = getPackageName();
			Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
			startActivity(browseIntent);
		}catch(Exception e) {
			Log.e("!!!", "Error starting browseIntent");
		}
	}

}
