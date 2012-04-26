package com.monster.pocketsafe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SetPassActivity extends Activity {

	private Button mBtCancel;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.setpass);
	    
	    mBtCancel = (Button) findViewById(R.id.btCancel);
	    mBtCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				onCancelClick();
				
			}
		});
	    
	}

	private void onCancelClick() {
		Log.d("!!!", "SetPass Canceled");
        setResult(RESULT_CANCELED);
        finish();	
	}
}
