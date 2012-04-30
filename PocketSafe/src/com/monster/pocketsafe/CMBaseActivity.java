package com.monster.pocketsafe;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMListener;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.utils.MyException;

public abstract class CMBaseActivity extends Activity implements IMBaseActivity, IMHelperBaseActivityObserver, IMListener {
	
	private CMHelperBaseActivity mHelper;
	
	public CMBaseActivity() {
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

	public IMMain getMain() throws MyException {
		return mHelper.getMain();
	}

	public void listenerEvent(IMEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void onMainBind() throws MyException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("!!!", "onCreateDialog: : "+this);
		return mHelper.onCreateDialog(id);
	}

}