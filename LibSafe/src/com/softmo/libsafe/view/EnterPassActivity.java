package com.softmo.libsafe.view;

import com.softmo.libsafe.utils.MyException;
import com.softmo.libsafe.utils.MyException.TTypMyException;
import com.softmo.libsafe.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EnterPassActivity extends Activity {

public final static String PASS = "com.softmo.smssafe.EnterPassActivity.PASS";
	
	private EditText mPass;
	private Button mDelBt;
	private String mRes;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.enterpass);
	    
	    mPass = (EditText) findViewById(R.id.edPass);
	    mRes = new String();
	    
	    mDelBt = (Button)findViewById(R.id.btDel);
	    mDelBt.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				mRes = new String();
				updateEditor();
				return true;
			}
		});
	}

	public void onOkClick(View v) {
		try {
			if (mRes.length()==0)
				throw new MyException(TTypMyException.EPassEmpty);
				
			Intent intent = new Intent();
	        intent.putExtra(PASS, mRes); 
	        
	        setResult(RESULT_OK, intent);
	        finish();	
		} catch(MyException e) {
			ErrorDisplayer.displayError(this, e);
		}
	}
	
	private void updateEditor() {
		String txt = new String();
		for(int i=0; i<mRes.length(); i++)
			txt+="*";
		
		mPass.setText( txt );
		mPass.setSelection(txt.length());		
	}
	
	private void addChar(String ch) {
		mRes += ch;
		updateEditor();
	}
	
	public void onClick0(View v) {
		addChar("0");
	}

	public void onClick1(View v) {
		addChar("1");
	}
	
	public void onClick2(View v) {
		addChar("2");
	}
	
	public void onClick3(View v) {
		addChar("3");
	}
	
	public void onClick4(View v) {
		addChar("4");
	}
	
	public void onClick5(View v) {
		addChar("5");
	}
	
	public void onClick6(View v) {
		addChar("6");
	}
	
	public void onClick7(View v) {
		addChar("7");
	}
	
	public void onClick8(View v) {
		addChar("8");
	}
	
	public void onClick9(View v) {
		addChar("9");
	}
	
	public void onClickDel(View v) {
		if (mRes.length()>0) {
			mRes = mRes.substring(0, mRes.length()-1);
			updateEditor();
		}
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("pass",mRes);
		
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		mRes = outState.getString("pass");
		updateEditor();
		
		super.onRestoreInstanceState(outState);
	}	
}

