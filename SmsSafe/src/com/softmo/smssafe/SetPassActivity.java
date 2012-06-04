package com.softmo.smssafe;

import java.util.HashSet;

import com.softmo.smssafe.R;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SetPassActivity extends Activity {

	public static final String MODE = "com.softmo.smssafe.SetPassActivity.MODE";
	public final static String PASS = "com.softmo.smssafe.SetPassActivity.PASS";
	
	public enum TMode {
		ESetPass,
		EChangePass
	};
	
	private EditText mPass1;
	private EditText mPass2;
	
	private static HashSet<String> mNumbers = new HashSet<String>();
	static {
		for (int i=0; i<10; i++)
			mNumbers.add(String.valueOf(i));
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setpass);
	    
	    mPass1 = (EditText) findViewById(R.id.edPass1);
	    mPass2 = (EditText) findViewById(R.id.edPass2);
	}

	public void onOkClick(View v) {
		try {
			String pass1 = mPass1.getText().toString();
			String pass2 = mPass2.getText().toString();
			
			if (pass1.length()!=pass2.length() || !pass1.equals(pass2))
				throw new MyException(TTypMyException.EPassNotMatch);
			
			if (pass1.length()==0)
				throw new MyException(TTypMyException.EPassEmpty);
			
			for (int i=0; i<pass1.length(); i++) {
				String ch = pass1.substring(i,i+1);
				if (!mNumbers.contains(ch))
					throw new MyException(TTypMyException.EPassNotDigital);
			}

				
			Intent intent = new Intent();
	        intent.putExtra(PASS, pass1); 
	        
	        setResult(RESULT_OK, intent);
	        finish();	
		} catch(MyException e) {
			ErrorDisplayer.displayError(this, e);
		}
	}
	
	public void onCancelClick(View v) {
		Log.d("!!!", "SetPass Canceled");
        setResult(RESULT_CANCELED);
        finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("pass1", mPass1.getText().toString());
		outState.putString("pass2", mPass2.getText().toString());
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		mPass1.setText(outState.getString("pass1"));
		mPass2.setText(outState.getString("pass2"));

		super.onRestoreInstanceState(outState);
	}	
}
