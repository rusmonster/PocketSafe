package com.monster.pocketsafe;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetPassActivity extends Activity {
	
	public final static String PASS = "com.monster.pocketsafe.SetPassActivity.PASS";

	private EditText mPass1;
	private EditText mPass2;
	
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
}
