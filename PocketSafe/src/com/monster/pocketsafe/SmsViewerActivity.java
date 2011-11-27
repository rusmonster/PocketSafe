package com.monster.pocketsafe;

import java.util.ArrayList;

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
import android.os.IBinder;
import android.util.Log;

public class SmsViewerActivity extends ListActivity implements IMListener {

	private IMMain mMain;
	private String mPhone;
	ArrayList<IMSms> mSmsList = new ArrayList<IMSms>();
	
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
		getMain().DbReader().QuerySms().QueryByPhoneOrderByDatDesc(mSmsList, mPhone, 0, 1000);
		
		for (int i=0; i<mSmsList.size(); i++) {
			IMSms sms = mSmsList.get(i);
			if (sms.getIsNew() >= TTypIsNew.Enew) {
				sms.setIsNew(TTypIsNew.EReaded);
				mMain.DbWriter().SmsUpdate(sms);
			}
		}
        SmsAdapter adapter = new SmsAdapter(this, mSmsList);
        
        setListAdapter(adapter);
	}
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.smsviewer);
	    
	    bindService(new Intent(this, CMSafeService.class), serviceConncetion, BIND_AUTO_CREATE);
	
	    // TODO Auto-generated method stub
	}

	public void listenerEvent(IMEvent event) throws MyException {
		switch (event.getTyp()) {
		case ESmsRecieved:
			createListAdapter();
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
