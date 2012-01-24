package com.monster.pocketsafe;

import java.util.ArrayList;
import com.monster.pocketsafe.dbengine.IMContact;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMEventErr;
import com.monster.pocketsafe.main.IMListener;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SmsViewerActivity extends ListActivity implements IMListener {
	

	private IMMain mMain;
	private String mPhone;
	private String mName;
	ArrayList<IMSms> mSmsList = new ArrayList<IMSms>();
	private final Handler mHandler = new Handler();
	private View mEditorView;
	private EditText mEdText;
	private TextView mTvCounter;
	private Button mBtSend;
	
	private CSmsCounter mSmsCou = new CSmsCounter();
	
	public static final String PHONE = "com.monster.pocketsafe.SmsViewerActivity.PHONE";
	private static final int IDD_SMS_SENDING = 1;
	
	private static final int IDM_NEW = 101;
	private static final int IDM_DEL = 102;
	private static final int IDM_SHOWALL = 103;
	private static final int IDM_FORWARD = 104;
	private static final int IDM_DELMESSAGE = 105;
	
	private static final int NEW_SMS_RESULT = 1002;
	
	private static final int IDD_DELTHREAD = 1001;
	private static final int IDD_DELMESSAGE = 1002;
	
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
    			getMain().Dispatcher().addListener(SmsViewerActivity.this);
				createListAdapter();
			} catch (MyException e) {
				e.printStackTrace();
			}
    	}

	    public void onServiceDisconnected(ComponentName name) {
	        setMain( null );
	        Log.d("!!!", "Service disconnected");
	    }
    };
    
	private void createListAdapter() throws MyException {
		if (mPhone == null || mPhone.length() == 0) return;
		
		IMContact cont = getMain().DbReader().QueryContact().getByPhone(mPhone);
		if (cont != null)
			mName = cont.getName();
		else 
			mName = mPhone;
		
		setTitle(mName);
		
		
		getMain().DbReader().QuerySms().QueryByPhoneOrderByDat(mSmsList, mPhone, 0, TStruct.PAGE_SIZE);
		if (mSmsList.size()==TStruct.PAGE_SIZE) {
			ArrayList<IMSms> sms_list = new ArrayList<IMSms>();
			int k = TStruct.PAGE_SIZE;

			do {
				getMain().DbReader().QuerySms().QueryByPhoneOrderByDat(sms_list, mPhone, k, TStruct.PAGE_SIZE);
				k+=TStruct.PAGE_SIZE;
				for (int i=0; i<sms_list.size(); i++)
					mSmsList.add(sms_list.get(i));
			} while (sms_list.size()==TStruct.PAGE_SIZE);
		}
		
		for (int i=0; i<mSmsList.size(); i++) {
			IMSms sms = mSmsList.get(i);
			if (sms.getIsNew() >= TTypIsNew.ENew) {
				sms.setIsNew(TTypIsNew.EOld);
				getMain().DbWriter().SmsUpdate(sms);
			}
		}
		
        SmsAdapter adapter = new SmsAdapter(this, mSmsList, mName);
        setListAdapter(adapter);
	}
    
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
						getMain().SendSms(mPhone, text);
					} catch (MyException e) {
						ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().Value);
					} catch (Exception e) {
						ErrorDisplayer.displayError(SmsViewerActivity.this, TTypMyException.ESmsErrSendGeneral.Value);
					}
			}
		});
	    
	    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    getListView().setStackFromBottom(true);
	    
	    registerForContextMenu(getListView());
	}

	private final Runnable mRunReload = new Runnable() {
		
		public void run() {
			try {
				createListAdapter();
			} catch (MyException e) {
				e.printStackTrace();
			}	
		}
	};
	private ProgressDialog mDlg;
	private int mMessageForDel;
	
	public void listenerEvent(IMEvent event) throws MyException {
		switch (event.getTyp()) {
		case ESmsRecieved:
		case ESmsUpdated:
		case ESmsOutboxAdded:
		case ESmsDelMany:
		case ESmsDeleted:
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		case ESmsSendStart:
			showDialog(IDD_SMS_SENDING); 
			break;
		case ESmsSent:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			mEdText.getText().clear();
			
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEdText.getWindowToken(), 0);
			
			Log.v("!!!", "Text cleared");
			break;
		case ESmsSendError:
			dismissDialog(IDD_SMS_SENDING); mDlg = null;
			assert(event instanceof IMEventErr);
			handleSmsSendError((IMEventErr) event);
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
			ErrorDisplayer.displayError(this, e.getId().Value);
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
					getMain().DbWriter().SmsDelete(mMessageForDel);
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().Value);
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
		IMContact c = getMain().DbReader().QueryContact().getByPhone(nam);
		if (c!=null) nam = c.getName();
		
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		
		String msg = getResources().getString(R.string.sms_delthreadconf, nam);
		dlg.setMessage(msg);
		
		dlg.setPositiveButton(getResources().getString(R.string.yes), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					getMain().DbWriter().SmsDeleteByPhone(phone);
					GotoMain();
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsViewerActivity.this, e.getId().Value);
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
		String errstr = ErrorDisplayer.getErrStr(event.getErr());
		Toast.makeText(getBaseContext(), errstr, Toast.LENGTH_SHORT).show();		
	}

	@Override
	protected void onStart() {
	    mPhone = getIntent().getStringExtra(PHONE);
	    Log.v("!!!", "PHONE: "+mPhone);
	    
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Intent stIntent = new Intent(this, CMSafeService.class);
        startService(stIntent);
	    bindService(stIntent, serviceConncetion, BIND_AUTO_CREATE);
	    
	    Log.v("!!!", "SmsViewer onResume()");
	    super.onResume();
	}
	
	@Override
	protected void onPause() {
		try {
			getMain().Dispatcher().delListener(this);
			unbindService(serviceConncetion);
			if (mDlg != null) dismissDialog(IDD_SMS_SENDING);
			mDlg = null;
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, IDM_SHOWALL, Menu.NONE, R.string.sms_allsms);
		menu.add(Menu.NONE, IDM_NEW, Menu.NONE, R.string.sms_newsms);
		menu.add(Menu.NONE, IDM_DEL, Menu.NONE, R.string.sms_del_thread);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case IDM_SHOWALL:
			GotoMain();
			break;
		case IDM_NEW:
	        startActivityForResult(new Intent(this, SmsNewActivity.class), NEW_SMS_RESULT);
			break;
		case IDM_DEL:
			showDialog(IDD_DELTHREAD);
			break;
		}
		return super.onOptionsItemSelected(item);
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
            	
            	String phone = data.getStringExtra(SmsViewerActivity.PHONE);
            	if (phone!=null && phone.length()>0 && !phone.equalsIgnoreCase(mPhone)) {
	                Intent intent = new Intent(this, SmsViewerActivity.class); 
	                intent.putExtra(SmsViewerActivity.PHONE, phone); 
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
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		switch(item.getItemId()) {
		case IDM_FORWARD:
			ForwardMessage(info.position);
			break;
		case IDM_DELMESSAGE:
			mMessageForDel = mSmsList.get(info.position).getId();
			showDialog(IDD_DELMESSAGE);
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void ForwardMessage(int position) {
		String text = mSmsList.get(position).getText();
		Intent i = new Intent(this, SmsNewActivity.class);
		i.putExtra(SmsNewActivity.TEXT, text);
		startActivityForResult(i, NEW_SMS_RESULT);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if (v.getId() == android.R.id.list) {
			menu.setHeaderTitle(R.string.sms_message);
			
			menu.add(Menu.NONE, IDM_FORWARD, Menu.NONE, R.string.sms_forward);
			menu.add(Menu.NONE, IDM_DELMESSAGE, Menu.NONE, R.string.sms_delmessage);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	

}
