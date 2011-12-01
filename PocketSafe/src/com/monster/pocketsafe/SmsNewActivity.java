package com.monster.pocketsafe;

import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMEventErr;
import com.monster.pocketsafe.main.IMListener;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsNewActivity extends Activity implements IMListener {
	
	private static final int IDD_SMS_SENDING = 1;
	protected static final int CONTACT_PICKER_RESULT = 1001;
	private IMMain mMain;
	private EditText mEdPhone;
	private Button mBtBook;
	private EditText mEdText;
	private Button mBtSend;
	private ProgressDialog mDlg;

	private IMMain getMain() throws MyException {
		if (mMain == null)
			throw new MyException(TTypMyException.EErrServiceNotBinded);
		return mMain;
	}

	private void setMain(IMMain mMain) {
		this.mMain = mMain;
	}
	
    private ServiceConnection serviceConncetion = new ServiceConnection() {

    	public void onServiceConnected(ComponentName name, IBinder service) {
    		setMain( ((CMSafeService.MyBinder)service).getMain() );
    		Log.d("!!!", "Service connected");
    		try {
    			getMain().Dispatcher().addListener(SmsNewActivity.this);
			} catch (MyException e) {
				e.printStackTrace();
			}
    	}

	    public void onServiceDisconnected(ComponentName name) {
	        setMain( null );
	        Log.d("!!!", "Service disconnected");
	    }
    };
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.smsnew);

	    mEdPhone = (EditText)findViewById(R.id.edSmsNewPhone);
	    mBtBook = (Button)findViewById(R.id.btSmsNewBook);
	    mEdText = (EditText)findViewById(R.id.edSmsNewText);
	    mBtSend = (Button)findViewById(R.id.btSmsNewSend);
	    
	    mBtBook.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				 Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,  
					        Contacts.CONTENT_URI);  
				startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);				
			}
		});
	    
	    mBtSend.setOnClickListener( new OnClickListener() {
			
			public void onClick(View v) {
				try {
					getMain().SendSms(mEdPhone.getText().toString(),mEdText.getText().toString());
				} catch (MyException e) {
					ErrorDisplayer.displayError(e.getId().Value);
				}
				
			}
		} );
	}
	
	@Override
	protected void onResume() {
	    bindService(new Intent(this, CMSafeService.class), serviceConncetion, BIND_AUTO_CREATE);
	    super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		try {
			getMain().Dispatcher().delListener(this);
			unbindService(serviceConncetion);
			if (mDlg!=null) dismissDialog(IDD_SMS_SENDING); mDlg = null;
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case IDD_SMS_SENDING:
			if (mDlg!=null) 
				dismissDialog(IDD_SMS_SENDING);
			mDlg = new ProgressDialog(this);
			mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mDlg.setCancelable(false);
			mDlg.setMessage(getResources().getString(R.string.sms_sending));
			return mDlg;
				
		}
		return super.onCreateDialog(id);
	}

	public void listenerEvent(IMEvent event) throws Exception {
		switch (event.getTyp()) {
		case ESmsSendStart:
			showDialog(IDD_SMS_SENDING); 
			break;
		case ESmsSent:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			
	        Intent intent = new Intent(this, SmsViewerActivity.class); 
	        intent.putExtra(SmsViewerActivity.PHONE, mEdPhone.getText().toString()); 
	        
			mEdPhone.getText().clear();
			mEdText.getText().clear();
			
	        setResult(RESULT_OK, intent);
	        finish();
			break;
		case ESmsSendError:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			IMEventErr ev = (IMEventErr) event;
			String errstr = ErrorDisplayer.getErrStr(ev.getErr());
			Toast.makeText(getBaseContext(), errstr, Toast.LENGTH_SHORT).show();
			break;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case CONTACT_PICKER_RESULT:
            	Cursor cursor = null;
                String phone = "";
                try {
                    Uri result = data.getData();
                    Log.v("!!!", "Got a contact result: " + result.toString());

                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();

                    // query for everything email
                    cursor = getContentResolver().query(Phone.CONTENT_URI,
                            null, Phone.CONTACT_ID + "=?", new String[] { id },
                            null);

                    int phoneIdx = cursor.getColumnIndex(Phone.DATA);

                    // let's just get the first email
                    if (cursor.moveToFirst()) {
                        phone = cursor.getString(phoneIdx);
                        Log.v("!!!", "Got phone: " + phone);
                    } else {
                        Log.w("!!!", "No results");
                    }
                } catch (Exception e) {
                    Log.e("!!!", "Failed to get phone data", e);
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                    
                    mEdPhone.setText(phone);
                    if (phone.length()>0)
                    	mEdText.requestFocus();

                }

                break;
            }

        } else {
            Log.w("!!!", "Warning: activity result not ok");
        }
		super.onActivityResult(requestCode, resultCode, data);
	}
	

}
