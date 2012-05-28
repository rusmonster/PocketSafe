package com.softmo.libsafe.view;


import com.softmo.libsafe.main.IMListener;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public abstract class CMBaseListActivity extends ListActivity implements IMHelperBaseActivityObserver, IMListener {
	
	private CMHelperBaseActivity mHelper;
	
	public CMBaseListActivity() {
		super();
		mHelper = new CMHelperBaseActivity(this);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mHelper.onCreate(savedInstanceState);
    }
	   
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHelper.onDestroy();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mHelper.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mHelper.onStop();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHelper.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHelper.onPause();
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mHelper.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("!!!", "onCreateDialog: "+this);
		return mHelper.onCreateDialog(id);
	}

	public IMBaseActivity getHelper() {
		return mHelper;
	}
}
