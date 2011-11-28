package com.monster.pocketsafe;

import java.util.ArrayList;

import com.monster.pocketsafe.dbengine.IMContact;
import com.monster.pocketsafe.dbengine.IMSms;
import com.monster.pocketsafe.dbengine.TTypIsNew;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMListener;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class SmsViewerActivity extends ListActivity implements IMListener {

	private IMMain mMain;
	private String mPhone;
	private String mName;
	ArrayList<IMSms> mSmsList = new ArrayList<IMSms>();
	private final Handler mHandler = new Handler();
	private View mEditorView;
	
	public static final String PHONE = "com.monster.pocketsafe.SmsViewerActivity.PHONE";
	
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
		
		for (int i=0; i<mSmsList.size(); i++) {
			IMSms sms = mSmsList.get(i);
			if (sms.getIsNew() >= TTypIsNew.Enew) {
				sms.setIsNew(TTypIsNew.EReaded);
				getMain().DbWriter().SmsUpdate(sms);
			}
		}
        SmsAdapter adapter = new SmsAdapter(this, mSmsList, mName, mEditorView);
        setListAdapter(adapter);

	}
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.smsviewer);
	    
	    mEditorView = getLayoutInflater().inflate(R.layout.smsviewereditor, null);
	    
	    getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	    getListView().setStackFromBottom(true);
	    
	    bindService(new Intent(this, CMSafeService.class), serviceConncetion, BIND_AUTO_CREATE);
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
	
	public void listenerEvent(IMEvent event) throws MyException {
		switch (event.getTyp()) {
		case ESmsRecieved:
		case ESmsUpdated:
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		}
	}
	
	@Override
	protected void onStart() {
	    mPhone = getIntent().getStringExtra(PHONE);
	    Log.v("!!!", "PHONE: "+mPhone);

		super.onStart();
	}
	
	@Override
	protected void onResume() {
		try {
			getMain().Dispatcher().addListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		unbindService(serviceConncetion);
		super.onDestroy();
	}

}
