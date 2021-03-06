package com.softmo.smssafe2.views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.softmo.smssafe2.R;
import com.softmo.smssafe2.dbengine.IMContact;
import com.softmo.smssafe2.main.IMEvent;
import com.softmo.smssafe2.main.IMEventSimpleID;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import java.net.URLDecoder;

public class SmsNewActivity extends CMBaseActivity {

	private static final int IDD_SMS_SENDING = 1;
	protected static final int CONTACT_PICKER_RESULT = 1001;
	public static final String TEXT = "com.softmo.smssafe2.SMSNEW_TEXT";
	private EditText mEdPhone;
	private View mBtBook;
	private EditText mEdText;
	private View mBtSend;
	private ProgressDialog mDlg;
	private String mPhone;
	private String mSavedPhone;
	private String mIntentPhone;
	private String mLastEdPhoneVal;
	private TextView mTxtCounter;

	private void Phone2Name() {
		String phone = mEdPhone.getText().toString();

		if (phone.equalsIgnoreCase(mLastEdPhoneVal)) return;

		phone = phone.replaceAll("[^0-9\\+]*", "").trim();

		Log.d("!!!", "phone=" + phone);
		Log.d("!!!", "mLastEdPhoneVal=" + mLastEdPhoneVal);

		IMContact c = null;
		try {
			c = getHelper().getMain().DbReader().QueryContact().getByPhone(phone);
		} catch (MyException e) {
			//ErrorDisplayer.displayError(this, e.getId().getValue());
			return;
		}

		mPhone = null;
		if (c != null) {
			mPhone = c.getPhone().replaceAll("[^0-9\\+]*", "").trim();
			mEdPhone.setText(c.getName());
		}

		Log.d("!!!", "mPhone=" + mPhone);

		mLastEdPhoneVal = mEdPhone.getText().toString();

	}

	private void SendSms() throws MyException {
		String phone = mPhone;
		if (phone == null)
			mPhone = mEdPhone.getText().toString();

		getHelper().getMain().SendSms(mPhone, mEdText.getText().toString());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsnew);
		setTitle(R.string.sms_new);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mEdPhone = (EditText) findViewById(R.id.edSmsNewPhone);
		mEdPhone.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mLastEdPhoneVal = mEdPhone.getText().toString().trim();
					mEdPhone.selectAll();
				} else {
					Phone2Name();
				}
			}
		});

		mBtBook = findViewById(R.id.btSmsNewBook);

		mTxtCounter = (TextView) findViewById(R.id.tvCounter);
		mEdText = (EditText) findViewById(R.id.edSmsNewText);
		mEdText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//nothing
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				//nothing
			}

			public void afterTextChanged(Editable s) {
				CSmsCounter smscou = new CSmsCounter(s.toString());
				mTxtCounter.setText(" " + smscou.toString());
			}
		});


		mBtSend = findViewById(R.id.btSmsNewSend);
		mBtBook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SelectContactFromBook();
			}
		});

		mBtSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					Phone2Name();
					SendSms();
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsNewActivity.this, e.getId().getValue());
				} catch (Exception e) {
					ErrorDisplayer.displayError(SmsNewActivity.this, TTypMyException.ESmsErrSendGeneral.getValue());
				}


			}
		});

		// check if application was started with an intent
		try {
			Intent intent = getIntent();
			if (savedInstanceState == null && intent != null && intent.getAction() != null) {

				if (intent.getAction().equals(Intent.ACTION_SEND)
						|| intent.getAction().equals(Intent.ACTION_SENDTO)) {
					String message = intent.getStringExtra(Intent.EXTRA_TEXT);
					mEdText.setText(message);

					String data = intent.getDataString();
					if (data != null) {
						mIntentPhone = URLDecoder.decode(data, "UTF-8")
								.replaceAll("[^0-9\\+]*", "");
					}

					Log.d("!!!", "mIntentPhone: " + mIntentPhone + "; message: " + message);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		String text = getIntent().getStringExtra(TEXT);
		if (text != null)
			mEdText.setText(text);
		super.onStart();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case IDD_SMS_SENDING:
				if (mDlg != null)
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
			case ESmsSendError:
				dismissDialog(IDD_SMS_SENDING);
				mDlg = null;
				myFinish(event);
				break;
			/*
		case ESmsSendError:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			IMEventErr ev = (IMEventErr) event;
			ErrorDisplayer.displayError(this, ev.getErr());
			break;
			*/
		}

	}

	private void myFinish(IMEvent event) throws MyException {
		IMEventSimpleID evID = (IMEventSimpleID) event;
		int id = evID.getId();

		String hash = getHelper().getMain().DbReader().QuerySms().getHashById(id);

		Intent intent = new Intent(this, SmsViewerActivity.class);
		intent.putExtra(SmsViewerActivity.HASH, hash);
		intent.putExtra(SmsViewerActivity.SMS_ID, id);

		mEdPhone.getText().clear();
		mEdText.getText().clear();

		setResult(RESULT_OK, intent);
		finish();
	}

	private void SelectContactFromBook() {
		//Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		//startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

		Intent i = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
		startActivityForResult(i, CONTACT_PICKER_RESULT);

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

						// query for everything phone
						cursor = getContentResolver().query(Phone.CONTENT_URI,
								null, Phone._ID + "=?", new String[]{id},
								null);

						int phoneIdx = cursor.getColumnIndex(Phone.NUMBER);

						// let's just get the first phone
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
					/*
                    if (phone.length()>0) {
                    	Phone2Name();
                    	mEdText.requestFocus();
                    }
					*/
					}

					break;

            /*
            case CONTACT_PICKER_RESULT:
            	Cursor cursor = null;
                String phone = "";
                try {
                    Uri result = data.getData();
                    Log.v("!!!", "Got a contact result: " + result.toString());

                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();
 			      
                    // query for everything phone
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
                */
			}

		} else {
			Log.w("!!!", "Warning: activity result not ok");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void onMainBind() throws MyException {
		if (mIntentPhone != null) mEdPhone.setText(mIntentPhone);
		mIntentPhone = null;

		Phone2Name();

		if (mPhone == null && mSavedPhone != null) mPhone = mSavedPhone;
		Log.i("!!!", "onMainBind: " + mPhone);
		mEdText.requestFocus();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.menu_smsnew, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.mnuSmsNewLock:
				getHelper().lockNow();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		if (mDlg != null) dismissDialog(IDD_SMS_SENDING);
		mDlg = null;
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("!!!", "onSaveInstanceState: " + mPhone);
		outState.putString("phone", mPhone);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		mSavedPhone = outState.getString("phone");
		Log.i("!!!", "onSaveInstanceState: " + mSavedPhone);
		super.onRestoreInstanceState(outState);
	}

}
