package com.softmo.smssafe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.softmo.smssafe.R;
import com.softmo.smssafe.dbengine.IMContact;
import com.softmo.smssafe.dbengine.IMSms;
import com.softmo.smssafe.dbengine.TTypStatus;
import com.softmo.smssafe.main.IMEvent;
import com.softmo.smssafe.main.IMEventErr;
import com.softmo.smssafe.main.IMListener;
import com.softmo.smssafe.utils.MyException;
import com.softmo.smssafe.utils.MyException.TTypMyException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsViewerActivity extends CMBaseListActivity implements IMListener {
	

	private String mHash;
	private int mSmsId;
	private String mPhone;
	private String mName;
	private final Handler mHandler = new Handler();
	private View mEditorView;
	private EditText mEdText;
	private TextView mTvCounter;
	private Button mBtSend;
	private Map<Integer, String> mSavedMap;
	
	private CSmsCounter mSmsCou = new CSmsCounter();
	
	private SmsAdapter mAdapter;
	
	public static final String HASH = "com.softmo.smssafe.SmsViewerActivity.HASH";
	public static final String SMS_ID = "com.softmo.smssafe.SmsViewerActivity.SMS_ID";
	
	private static final int IDD_SMS_SENDING = 1;
	
	private static final int IDM_FORWARD = 104;
	private static final int IDM_DELMESSAGE = 105;
	private static final int IDM_COPYMESSAGE = 106;
	private static final int IDM_RESEND = 107;
	
	private static final int NEW_SMS_RESULT = 1002;
	
	private static final int IDD_DELTHREAD = 1001;
	private static final int IDD_DELMESSAGE = 1002;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.smsviewer);
	    
	    
	    
	    mEditorView = getLayoutInflater().inflate(R.layout.smsviewereditor, null);
	    getListView().addFooterView(mEditorView);

	    mTvCounter = (TextView)mEditorView.findViewById(R.id.tvCounter);
	    mBtSend = (Button)mEditorView.findViewById(R.id.btSendSms);
	    mEdText = (EditText) mEditorView.findViewById(R.id.edSms);
	    mEdText.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			public void afterTextChanged(Editable s) {
				mSmsCou.setSms(s.toString());
				mTvCounter.setText(" "+mSmsCou.toString());
			}
		});
	    
	    
	    mBtSend.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String text = mEdText.getText().toString();
				if (text.length()>0)
					try {
						getHelper().getMain().SendSms(mPhone, text);
					} catch (MyException e) {
						ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().getValue());
					} catch (Exception e) {
						ErrorDisplayer.displayError(SmsViewerActivity.this, TTypMyException.ESmsErrSendGeneral.getValue());
					}
			}
		});
	    
	    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    getListView().setStackFromBottom(true);
	    
	    registerForContextMenu(getListView());
	}

	private final Runnable mRunReload = new Runnable() {
		
		public void run() {
			mAdapter.notifyDataSetChanged();
		}
	};
	private ProgressDialog mDlg;
	private int mMessageForDel;
	
	public void listenerEvent(IMEvent event) throws MyException {
		switch (event.getTyp()) {
		case ESmsRecieved:
		//case ESmsUpdated:
		//case ESmsUpdatedMany:
		case ESmsOutboxAdded:
		case ESmsDelMany:
		case ESmsDeleted:
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		case ESmsSendStart:
			mEdText.getText().clear();			
			showDialog(IDD_SMS_SENDING); 
			break;
		case ESmsSent:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEdText.getWindowToken(), 0);
			
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		case ESmsSendError:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			assert(event instanceof IMEventErr);
			handleSmsSendError((IMEventErr) event);
			
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		case ESmsDelivered:
		case ESmsDeliverError:
			//TODO
			break;
		}
	}
	
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dlg = null;
		try {
			switch (id) {
			case IDD_SMS_SENDING:
				if (mDlg!=null) 
					dismissDialog(IDD_SMS_SENDING);
				mDlg = new ProgressDialog(this);
				mDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mDlg.setCancelable(false);
				mDlg.setMessage(getResources().getString(R.string.sms_sending));
				dlg = mDlg;
				break;
			case IDD_DELTHREAD:
				dlg = ShowDelThreadDlg();
				break;
			case IDD_DELMESSAGE:
				dlg = ShowDelMessageDlg();
				break;
			}
		} catch(MyException e) {
			ErrorDisplayer.displayError(this, e.getId().getValue());
		}
		return dlg;
	}

	private Dialog ShowDelMessageDlg() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		
		String msg = getResources().getString(R.string.sms_delmessageconf);
		dlg.setMessage(msg);
		
		dlg.setPositiveButton(getResources().getString(R.string.yes), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					getHelper().getMain().DbWriter().SmsDelete(mMessageForDel);
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().getValue());
				}
				
			}
		});
		
		dlg.setNegativeButton(getResources().getString(R.string.no), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		
		return dlg.create();
	}

	private Dialog ShowDelThreadDlg() throws MyException {
		final String phone = mPhone;
		String nam = new String( phone );
		IMContact c = getHelper().getMain().DbReader().QueryContact().getByPhone(nam);
		if (c!=null) nam = c.getName();
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		
		String msg = getResources().getString(R.string.sms_delthreadconf, nam);
		dlg.setMessage(msg);
		
		dlg.setPositiveButton(getResources().getString(R.string.yes), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					getHelper().getMain().DbWriter().SmsDeleteByHash(mHash);
					GotoMain();
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().getValue());
				}
				
			}
		});
		
		dlg.setNegativeButton(getResources().getString(R.string.no), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		
		return dlg.create();
	}

	private void handleSmsSendError(IMEventErr event) {
		String errstr = ErrorDisplayer.getErrStr(getBaseContext(), event.getErr());
		Toast.makeText(getBaseContext(), errstr, Toast.LENGTH_SHORT).show();		
	}

	@Override
	protected void onStart() {
	    mHash = getIntent().getStringExtra(HASH);
	    mSmsId = getIntent().getIntExtra(SMS_ID, -1);
	    
		super.onStart();
	}
	
	@Override
	protected void onPause() {	
		if (mDlg != null) dismissDialog(IDD_SMS_SENDING); mDlg = null;
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.menu_smsviewer, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.mnuSmsViewerShowAll:
				GotoMain();
				break;
			case R.id.mnuSmsViewerNewSms:
		        startActivityForResult(new Intent(this, SmsNewActivity.class), NEW_SMS_RESULT);
				break;
			case R.id.mnuSmsViewerCall:
				phoneCall();
				break;
			case R.id.mnuSmsViewerCopyPhone:
				copyPhone();
				break;
			case R.id.mnuSmsViewerDelThread:
				showDialog(IDD_DELTHREAD);
				break;
			case R.id.mnuSmsViewerLock:
				getHelper().lockNow();
				break;
			}
		} catch(Exception e) {
			ErrorDisplayer.displayError(this, TTypMyException.EErrUnknown.getValue());
		}
		return super.onOptionsItemSelected(item);
	}

	private void phoneCall() {
		try {
			Log.d("!!!", "phoneCall: "+mPhone);
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        callIntent.setData(Uri.parse("tel:"+mPhone));
	        startActivity(callIntent);
	    } catch (Exception e) {
	         Log.e("!!!","call failed", e);
	    }
	}
	
	private void copyPhone() {
		try{
	        Log.d("!!!","copy phone: "+mPhone);
			ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(mPhone);
			Toast.makeText(this, R.string.phone_copied, Toast.LENGTH_SHORT).show();
	
	    } catch (Exception e) {
	         Log.e("!!!","copy failed", e);
	    }
	}
	private void GotoMain() {
		Intent i = new Intent(this, SmsMainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case NEW_SMS_RESULT:
            	
            	String hash = data.getStringExtra(SmsViewerActivity.HASH);
            	if (hash!=null && hash.length()>0 && !hash.equalsIgnoreCase(mHash)) {
	                Intent intent = new Intent(this, SmsViewerActivity.class); 
	                intent.putExtra(SmsViewerActivity.HASH, hash); 
	                startActivity(intent);
	                finish();
            	}

                break;
            }
        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			int id = (int) mAdapter.getItemId(info.position);
			
			switch(item.getItemId()) {
			case IDM_RESEND:
				getHelper().getMain().ResendSms( id );
				break;
			case IDM_FORWARD:
				ForwardMessage(id);
				break;
			case IDM_COPYMESSAGE:
				IMSms sms = getHelper().getLocator().createSms(); 
				getHelper().getMain().DbReader().QuerySms().getById(sms, id);
				String txt = getHelper().getMain().decryptString(sms.getText());
				ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setText(txt);
				Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
				break;
			case IDM_DELMESSAGE:
				mMessageForDel = id;
				showDialog(IDD_DELMESSAGE);
				break;
			}
		} catch(Exception e) {
			ErrorDisplayer.displayError(this, TTypMyException.EErrUnknown.getValue());
		}
		return super.onContextItemSelected(item);
	}

	private void ForwardMessage(int id) throws MyException {
		IMSms sms = getHelper().getLocator().createSms(); 
		getHelper().getMain().DbReader().QuerySms().getById(sms, id);
		String text = getHelper().getMain().decryptString(sms.getText());
		
		Intent i = new Intent(this, SmsNewActivity.class);
		i.putExtra(SmsNewActivity.TEXT, text);
		startActivityForResult(i, NEW_SMS_RESULT);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		
		if (v.getId() == android.R.id.list && info.id>=0) {
			menu.setHeaderTitle(R.string.sms_message);
			
			try {
				IMSms sms = getHelper().getLocator().createSms();
				int id =  (int) mAdapter.getItemId(info.position);
				getHelper().getMain().DbReader().QuerySms().getById(sms,id);
				if (sms.getStatus() == TTypStatus.ESendError)
					menu.add(Menu.NONE, IDM_RESEND, Menu.NONE, R.string.sms_resend);
			}catch(Exception e)
			{}
			menu.add(Menu.NONE, IDM_FORWARD, Menu.NONE, R.string.sms_forward);
			menu.add(Menu.NONE, IDM_COPYMESSAGE, Menu.NONE, R.string.sms_copymessage);
			menu.add(Menu.NONE, IDM_DELMESSAGE, Menu.NONE, R.string.sms_delmessage);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public void onMainBind() throws MyException {     
		
		ArrayList<IMSms> dest = new ArrayList<IMSms>(1);
		getHelper().getMain().DbReader().QuerySms().QueryByHashOrderByDat(dest, mHash, 0, 1);
		
		if (dest.size()>0) {
			IMSms sms = dest.get(0);
			mPhone = getHelper().getMain().decryptString(sms.getPhone());
			IMContact cont = getHelper().getMain().DbReader().QueryContact().getByPhone(mPhone);
			if (cont != null)
				mName = cont.getName();
			else 
				mName = mPhone;
			
			setTitle(mName);
		}

        mAdapter = new SmsAdapter(this, getHelper().getMain(), mName, mHash);
       	mAdapter.setMap(mSavedMap);
        setListAdapter(mAdapter);	

        try {
			if (mSmsId != -1) {
				IMSms sms = getHelper().getLocator().createSms();
				getHelper().getMain().DbReader().QuerySms().getById(sms, mSmsId);
				
				if (sms.getStatus() == TTypStatus.ESending) {
					showDialog(IDD_SMS_SENDING); 
				}
			}
        } catch (Exception e) {
        	Log.e("!!!", "Error in SmsViewerActivity onMainBind: "+e.getMessage());
        }
        
	}



	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("!!!", "Sms: onSaveInstanceState");
		
		outState.putString("text", mEdText.getText().toString());
		if (mAdapter!=null) {
			Map<Integer, String> map = mAdapter.getMap(); 
			Iterator<Integer> i = map.keySet().iterator();
			
			int siz = map.size();
			ArrayList<Integer> keys = new ArrayList<Integer>(siz);
			ArrayList<String> vals = new ArrayList<String>(siz);
			while (i.hasNext()) {
				int key = i.next();
				keys.add(key);
				vals.add(map.get(key));
			}
			outState.putIntegerArrayList("keys", keys);
			outState.putStringArrayList("vals", vals);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState) {
		Log.d("!!!", "Sms: onRestoreInstanceState");
		
		mEdText.setText(outState.getString("text"));
		
		ArrayList<Integer> keys = outState.getIntegerArrayList("keys");
		ArrayList<String> vals = outState.getStringArrayList("vals");
		if (keys!=null && vals!=null) {
			mSavedMap = new HashMap<Integer, String>();
			for (int i=0; i<keys.size(); i++)
				mSavedMap.put(keys.get(i), vals.get(i));
		}
		super.onRestoreInstanceState(outState);
	}

	 
}
