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

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SmsViewerActivity extends ListActivity implements IMListener {
	

	private IMMain mMain;
	private String mPhone;
	private String mName;
	ArrayList<IMSms> mSmsList = new ArrayList<IMSms>();
	private final Handler mHandler = new Handler();
	private View mEditorView;
	private EditText mEdText;
	private Button mBtSend;
	
	public static final String PHONE = "com.monster.pocketsafe.SmsViewerActivity.PHONE";
	private static final int IDD_SMS_SENDING = 1;
	

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
		
		
		getMain().DbReader().QuerySms().QueryByPhoneOrderByDat(mSmsList, mPhone, 0, 1000);
		
		boolean updated=false;
		for (int i=0; i<mSmsList.size(); i++) {
			IMSms sms = mSmsList.get(i);
			if (sms.getIsNew() >= TTypIsNew.ENew) {
				sms.setIsNew(TTypIsNew.EOld);
				getMain().DbWriter().SmsUpdate(sms);
				updated=true;
			}
		}

		if (updated)
			getMain().checkNewNotificator();
		
        SmsAdapter adapter = new SmsAdapter(this, mSmsList, mName, mEditorView);
        setListAdapter(adapter);
	}
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.smsviewer);
	    
	    mEditorView = getLayoutInflater().inflate(R.layout.smsviewereditor, null);
	    mEdText = (EditText) mEditorView.findViewById(R.id.edSms);
	    mBtSend = (Button)mEditorView.findViewById(R.id.btSendSms);
	    
	    mBtSend.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String text = mEdText.getText().toString();
				if (text.length()>0)
					try {
						getMain().SendSms(mPhone, text);
					} catch (MyException e) {
						ErrorDisplayer.displayError(e.getId().Value);
					}
			}
		});
	    
	    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    getListView().setStackFromBottom(true);
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
	
	public void listenerEvent(IMEvent event) throws MyException {
		switch (event.getTyp()) {
		case ESmsRecieved:
		case ESmsUpdated:
		case ESmsOutboxAdded:
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
	    bindService(new Intent(this, CMSafeService.class), serviceConncetion, BIND_AUTO_CREATE);
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
	
	

}
