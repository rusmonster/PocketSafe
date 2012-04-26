package com.monster.pocketsafe;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.monster.pocketsafe.dbengine.IMDbQuerySetting.TTypSetting;
import com.monster.pocketsafe.dbengine.IMSetting;
import com.monster.pocketsafe.main.IMEvent;
import com.monster.pocketsafe.main.IMListener;
import com.monster.pocketsafe.main.IMMain;
import com.monster.pocketsafe.safeservice.CMSafeService;
import com.monster.pocketsafe.utils.CMLocator;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMHelperBaseActivity implements IMListener {
	
	private static final int SET_PASS_RESULT = 1003;
	
	private Activity mOwner;
	private IMLocator mLocator;
	private IMMain mMain;
	
	private ServiceConnection serviceConncetion = new ServiceConnection() {

    	public void onServiceConnected(ComponentName name, IBinder service) {
    		CMHelperBaseActivity.this.mMain = ((CMSafeService.MyBinder)service).getMain();
    		try {
    			getMain().Dispatcher().addListener(CMHelperBaseActivity.this);
    			onMainBind();
			} catch (MyException e) {
				ErrorDisplayer.displayError(mOwner, e.getId().getValue());
			}
    	}

	    public void onServiceDisconnected(ComponentName name) {
	    	CMHelperBaseActivity.this.mMain = null;
	        Log.d("!!!", "Service disconnected "+this.toString());
	    }
    };
	
	public IMMain getMain() throws MyException {
		if (mMain == null) {
			Log.w("!!!", "mMain == null");
			throw new MyException(TTypMyException.EErrServiceNotBinded);
		}
		return mMain;
	}
    
	private void checkPassSet() throws MyException {
		IMSetting set = mLocator.createSetting();
		getMain().DbReader().QuerySetting().getById(set, TTypSetting.ERsaPub);
		
		if (set.getStrVal().length()>0)
			return;
		
		mOwner.startActivityForResult(new Intent(mOwner, SetPassActivity.class), SET_PASS_RESULT);
	}
	
	private void checkPassActual() {
		
	}
	
	private void internalMainBind() throws MyException {
		checkPassSet();
		checkPassActual();
	}
	
	protected void onMainBind() throws MyException {
		Log.d("!!!", "Service connected "+mOwner.toString());
		try {
			internalMainBind();
			if (mOwner instanceof IMHelperBaseActivityObserver) {
				IMHelperBaseActivityObserver ba = (IMHelperBaseActivityObserver)mOwner;
				ba.onMainBind();
			}
		} catch (MyException e) {
			ErrorDisplayer.displayError(mOwner, e.getId().getValue());
		}
	}
	
	public CMHelperBaseActivity(Activity owner) {
		mOwner = owner;
		mLocator = new CMLocator();
	}
	
    public void onCreate(Bundle savedInstanceState) {
    	Log.d("!!!", "onCreate "+mOwner.toString());
    }
	   
    public void onDestroy() {
		Log.d("!!!", "onDestroy "+mOwner.toString());
	}
	
    public void onStart() {
		Log.d("!!!", "onStart "+mOwner.toString());
	}

    public void onStop() {
		Log.d("!!!", "onStop "+mOwner.toString());
	}
	
    public void onResume() {
        Log.d("!!!", "onResume "+mOwner.toString());
        
        Intent stIntent = new Intent(mOwner, CMSafeService.class);
        mOwner.startService(stIntent);
        mOwner.bindService(stIntent, serviceConncetion, Activity.BIND_AUTO_CREATE);
	}
	

    public void onPause() {
		Log.d("!!!", "onPause "+mOwner.toString());
		try {
			getMain().Dispatcher().delListener(this);
		} catch (MyException e) {
			e.printStackTrace();
		}
		
		mMain=null;
		mOwner.unbindService(serviceConncetion);
	}
	
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            switch (requestCode) {
            case SET_PASS_RESULT:
            	Log.d("!!!", "SetPass cancel recved");
           		mOwner.finish();
                break;
            }
        }
    }
    
	public void listenerEvent(IMEvent event) throws Exception {
		Log.d("!!!", "listenerEvent: "+event.getTyp());
		
		if (mOwner instanceof IMListener) {
			((IMListener) mOwner).listenerEvent(event);
		}
	}
}
