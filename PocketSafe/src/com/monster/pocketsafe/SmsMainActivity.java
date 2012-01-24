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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SmsMainActivity extends ListActivity implements IMListener {
	

	protected static final int NEW_SMS_RESULT = 1002;
	
	private static final int IDD_DELTHREAD = 1001;
	private static final int IDD_DELALL = 1002;

	private IMMain mMain;
	private Button mBtNewSms;
	private ArrayList<IMSmsGroup> mGroups = new ArrayList<IMSmsGroup>();
	private final android.os.Handler mHandler = new android.os.Handler();
	
	@Override
	protected void onResume() {
        Log.d("!!!", "onResume "+this.toString());
        
        Intent stIntent = new Intent(this, CMSafeService.class);
        startService(stIntent);
        bindService(stIntent, serviceConncetion, BIND_AUTO_CREATE);
        
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d("!!!", "onPause "+this.toString());
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		
		mMain=null;
		unbindService(serviceConncetion);
		
		super.onPause();
	}
	
	private void GotoGroup(int idx) {
		
		String phone = mGroups.get(idx).getPhone();
		
        Intent intent = new Intent(this, SmsViewerActivity.class); 
        intent.putExtra(SmsViewerActivity.PHONE, phone); 
        startActivity(intent);	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.d("!!!", "List item clicked");
		
		GotoGroup(position);

	}
	
	private void createListAdapter() throws MyException {
		
		Log.v("!!!", "createListAdapter()");
		
		getMain().DbReader().QuerySms().QueryGroupByPhoneOrderByMaxDatDesc(mGroups, 0, TStruct.PAGE_SIZE);
		
		if (mGroups.size()==TStruct.PAGE_SIZE) {
			ArrayList<IMSmsGroup> gr_list = new ArrayList<IMSmsGroup>();
			int k = TStruct.PAGE_SIZE;

			do {
				getMain().DbReader().QuerySms().QueryGroupByPhoneOrderByMaxDatDesc(gr_list, k, TStruct.PAGE_SIZE);
				k+=TStruct.PAGE_SIZE;
				for (int i=0; i<gr_list.size(); i++)
					mGroups.add(gr_list.get(i));
			} while (gr_list.size()==TStruct.PAGE_SIZE);
		}
		
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
    		Log.d("!!!", "Service connected "+this.toString());
    		try {
    			getMain().Dispatcher().addListener(SmsMainActivity.this);
				createListAdapter();
			} catch (MyException e) {
				ErrorDisplayer.displayError(SmsMainActivity.this, e.getId().Value);
			}
    	}

	    public void onServiceDisconnected(ComponentName name) {
	        setMain( null );
	        Log.d("!!!", "Service disconnected "+this.toString());
	    }
    };
    
    void GotoNewSms() {
    	Intent intent = new Intent(this, SmsNewActivity.class); 
        startActivityForResult(intent, NEW_SMS_RESULT);
    }
	    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       // startService(new Intent(this, CMSafeService.class));
       
        mBtNewSms = (Button) findViewById(R.id.btNewSms);
        mBtNewSms.setOnClickListener( new View.OnClickListener() {
			
			public void onClick(View v) {
		        GotoNewSms();
			}
		});
        
        registerForContextMenu(getListView());
        
        Log.d("!!!", "onCreate "+this.toString());
    }
    
    
    
	@Override
	protected void onStart() {
		 Log.d("!!!", "onStart "+this.toString());
		super.onStart();
	}

	@Override
	protected void onStop() {
		 Log.d("!!!", "onStop "+this.toString());
		super.onStop();
	}

	private IMMain getMain() throws MyException {
		if (mMain == null) {
			Log.w("!!!", "mMain == null");
			throw new MyException(TTypMyException.EErrServiceNotBinded);
		}
		return mMain;
	}

	private void setMain(IMMain mMain) {
		this.mMain = mMain;
	}

	@Override
	protected void onDestroy() {
		Log.d("!!!", "onDestroy "+this.toString());
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
	private int mThreadForDelId;
	
	public void listenerEvent(IMEvent event) throws MyException {
		Log.v("!!!", "listenerEvent: "+event.getTyp());
		
		switch (event.getTyp()) {
		case ESmsRecieved:
		case ESmsUpdated:
		case ESmsOutboxAdded:
		case ESmsDelMany:
		case ESmsDeleted:
			mHandler.removeCallbacks(mRunReload);
			mHandler.postDelayed(mRunReload, TStruct.DEFAULT_DELAY_VIEW_RELOAD);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	       if (resultCode == RESULT_OK) {
	            switch (requestCode) {
	            case NEW_SMS_RESULT:
	            	
	            	String phone = data.getStringExtra(SmsViewerActivity.PHONE);
	            	
	            	if (phone!=null && phone.length()>0) {
		                Intent intent = new Intent(this, SmsViewerActivity.class); 
		                intent.putExtra(SmsViewerActivity.PHONE, phone); 
		                startActivity(intent);
	            	}

	                break;
	            }

	        } else {
	            Log.w("!!!", "Warning: activity result not ok");
	        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater i = getMenuInflater();
		i.inflate(R.menu.menu_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.mnuMainNewSms:
			GotoNewSms();
			break;
		case R.id.mnuMainDelAll:
			showDialog(IDD_DELALL);
			break;
		case R.id.mnuMainSettings:
			startActivity(new Intent(this, OptionsActivity.class));
			break;
		case R.id.mnuMainAbout:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		try {
			if (v.getId() == android.R.id.list) {
				
				MenuInflater i = getMenuInflater();
				i.inflate(R.menu.main_context, menu);
				
				AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
				
				String nam = mGroups.get(info.position).getPhone();
				IMContact c = getMain().DbReader().QueryContact().getByPhone(nam);
				if (c!=null) nam = c.getName();
				menu.setHeaderTitle(nam);
			}
		} catch (MyException e) {
			ErrorDisplayer.displayError(this, e.getId().Value);
		}
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int idx = info.position;
		switch(item.getItemId()) {
		case R.id.mnuMainView:
			GotoGroup(idx);
			break;
		case R.id.mnuMainDelThread:
			mThreadForDelId = idx;
			showDialog(IDD_DELTHREAD);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	
	AlertDialog ShowDelThreadDialog(int idx) throws MyException {
		final String phone = mGroups.get(idx).getPhone();
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
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsMainActivity.this, e.getId().Value);
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
	
	AlertDialog ShowDelAllDialog() throws MyException {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		
		String msg = getResources().getString(R.string.sms_delallconf);
		dlg.setMessage(msg);
		
		dlg.setPositiveButton(getResources().getString(R.string.yes), new OnClickListener() {
			
			public void onClick(DialogInterface arg0, int arg1) {
				try {
					getMain().DbWriter().SmsDelAll();
				} catch (MyException e) {
					ErrorDisplayer.displayError(SmsMainActivity.this, e.getId().Value);
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

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog dlg = null;
		try {
			switch(id) {
			case IDD_DELTHREAD:
				dlg = ShowDelThreadDialog(mThreadForDelId);
				break;
			case IDD_DELALL:
				dlg = ShowDelAllDialog();
				break;
			}
		} catch (MyException e) {
			ErrorDisplayer.displayError(this, e.getId().Value);
		}
		return dlg;
	}
	
	
}