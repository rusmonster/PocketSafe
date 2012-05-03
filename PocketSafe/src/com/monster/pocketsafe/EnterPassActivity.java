package com.monster.pocketsafe;

import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EnterPassActivity extends Activity {

public final static String PASS = "com.monster.pocketsafe.EnterPassActivity.PASS";
	
	private EditText mPass;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.enterpass);
	    
	    mPass = (EditText) findViewById(R.id.edPass);
	}

	public void onOkClick(View v) {
		try {
			String pass1 = mPass.getText().toString();
			
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

