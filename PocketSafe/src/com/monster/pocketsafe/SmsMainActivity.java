package com.monster.pocketsafe;

import java.util.ArrayList;

import com.monster.pocketsafe.R;
import com.monster.pocketsafe.dbengine.IMContact;
import com.monster.pocketsafe.dbengine.IMSmsGroup;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SmsMainActivity extends ListActivity implements IMListener {

	private IMMain mMain;
	private Button mBtNewSms;
	private ArrayList<IMSmsGroup> mGroups = new ArrayList<IMSmsGroup>();
	private final android.os.Handler mHandler = new android.os.Handler();
	
	@Override
	protected void onResume() {
        Log.d("!!!", "onResume");
		try {
			getMain().Dispatcher().addListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("!!!", "onPause");
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d("!!!", "List item clicked");
		
		String phone = mGroups.get(position).getPhone();
		
        Intent intent = new Intent(this, SmsViewerActivity.class); 
        intent.putExtra(SmsViewerActivity.PHONE, phone); 
        startActivity(intent);
	}
	
	private void createListAdapter() throws MyException {
		
		Log.v("!!!", "createListAdapter()");
		
		getMain().DbReader().QuerySms().QueryGroupByPhoneOrderByMaxDatDesc(mGroups, 0, 1000);
		
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<mGroups.size(); i++) {
			IMSmsGroup gr = mGroups.get(i);
			
			String name = gr.getPhone();
			IMContact cont = getMain().DbReader().QueryContact().getByPhone(gr.getPhone());
			if (cont != null)
				name = cont.getName();
			
			if (gr.getCountNew()>0)
				list.add(name+" ("+gr.getCountNew()+"/"+gr.getCount()+")");
			else
				list.add(name+" ("+gr.getCount()+")");
		}
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        
        setListAdapter(adapter);
	}
	
    private ServiceConnection serviceConncetion = new ServiceConnection() {

    	public void onServiceConnected(ComponentName name, IBinder service) {
    		setMain( ((CMSafeService.MyBinder)service).getMain() );
    		Log.d("!!!", "Service connected");
    		try {
    			getMain().Dispatcher().addListener(SmsMainActivity.this);
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
	    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       // startService(new Intent(this, CMSafeService.class));
        bindService(new Intent(this, CMSafeService.class), serviceConncetion, BIND_AUTO_CREATE);

        
        mBtNewSms = (Button) findViewById(R.id.btNewSms);
        mBtNewSms.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
				Log.d("!!!", "button clicked");
				finish();
			}
		});
        
        Log.d("!!!", "onCreate");
    }
    
	private IMMain getMain() throws MyException {
		if (mMain == null)
			throw new MyException(TTypMyException.EErrServiceNotBinded);
		return mMain;
	}

	private void setMain(IMMain mMain) {
		this.mMain = mMain;
	}

	@Override
	protected void onDestroy() {
		Log.d("!!!", "onDestroy");
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		unbindService(serviceConncetion);
		super.onDestroy();
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
		Log.v("!!!", "listenerEvent: "+event.getTyp());
		
		switch (event.getTyp()) {
		case ESmsRecieved:
		case ESmsUpdated:
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		}
	}
}
