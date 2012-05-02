package com.monster.pocketsafe.main;

import java.math.BigInteger;

import android.util.Log;

import com.monster.pocketsafe.sec.IMAes;
import com.monster.pocketsafe.sec.IMBase64;
import com.monster.pocketsafe.utils.IMLocator;
import com.monster.pocketsafe.utils.IMTimer;
import com.monster.pocketsafe.utils.IMTimerObserver;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;

public class CMPassHolder implements IMPassHolder, IMTimerObserver {

	private IMLocator mLocator;
	private IMPassHolderObserver mObserver;
	private String mPass;
	private String mKey;
	private IMAes mAes;
	private IMBase64 mBase64;
	private long mInterval;
	private IMTimer mTimer;
	
	public CMPassHolder(IMLocator locator) {
		mLocator = locator;
		mAes = mLocator.createAes();
		mBase64 = mLocator.createBase64();
		mTimer = mLocator.createTimer();
		mTimer.SetObserver(this);
	}

	public void setPass(String pass) throws MyException {
		if (pass==null || pass.length()==0)
			throw new MyException(TTypMyException.EPassInvalid);
		
		
		mPass = pass;
		try {
			if (mKey!=null)
				getKey();
		} catch (MyException e) {
			Log.e("!!!", "Invalid pass: "+e.getId());
			mPass=null;
			throw e;
		}
		
		mTimer.cancelTimer();
		mTimer.startTimer(mInterval);
	}

	public String getPass() throws MyException {
		return mPass;
	}

	public void setKey(String _key) {
		mKey = _key;
	}

	public BigInteger getKey() throws MyException {
		if (mPass==null)
			throw new MyException(TTypMyException.EPassExpired);
		
		String key = mAes.decrypt(mPass, mKey);
		byte[] b = mBase64.decode(key.getBytes());
		BigInteger res = new BigInteger(b);
		
		return res;
	}

	public boolean isPassValid() {
		return (mPass!=null);
	}

	public void setInterval(long _ms) throws MyException {
		mInterval = _ms;
		
		if ( isPassValid() ) {
			mTimer.cancelTimer();
			mTimer.startTimer(mInterval);
		}
	}

	public void timerEvent(IMTimer sender) throws Exception {
		if (mPass!=null) {
			mPass = null;
			if (mObserver!=null) 
				mObserver.passExpired(this);
		}
	}

	public void setObserever(IMPassHolderObserver observer) {
		mObserver = observer;
	}

	public void clearPass() {
		if (mPass!=null) {
			mPass=null;
			mTimer.cancelTimer();
		}
	}

}
