package com.softmo.smssafe2.main;

import android.content.Context;
import android.util.Log;
import com.softmo.smssafe2.sec.IMAes;
import com.softmo.smssafe2.sec.IMBase64;
import com.softmo.smssafe2.utils.IMLocator;
import com.softmo.smssafe2.utils.IMTimer;
import com.softmo.smssafe2.utils.IMTimerObserver;
import com.softmo.smssafe2.utils.IMTimerWakeup;
import com.softmo.smssafe2.utils.MyException;
import com.softmo.smssafe2.utils.MyException.TTypMyException;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

public class CMPassHolder implements IMPassHolder, IMTimerObserver {

	private IMLocator mLocator;
	private IMPassHolderObserver mObserver;
	private byte[] mPass;
	private String mKey;
	private byte[] mSecret;
	private IMAes mAes;
	private IMBase64 mBase64;
	private long mInterval;
	private IMTimerWakeup mTimer;
	private Date mTimExpire;
	private byte[] mSecretMask;
	private byte[] mPassMask;
	
	public CMPassHolder(IMLocator locator) {
		mLocator = locator;
		mAes = mLocator.createAes();
		mBase64 = mLocator.createBase64();
		mTimer = mLocator.createTimerWakeup();
		mTimer.SetObserver(this);
	}

	public void restartTimer() throws MyException {
		mTimExpire = null;
		mTimer.cancelTimer();
		
		if (isPassValid()) {
			mTimExpire = new Date(System.currentTimeMillis()+mInterval);
			mTimer.startTimer(mInterval);
		}
	}
	
	public void cancelTimer() {
		
		mTimer.cancelTimer();
		
		if (mTimExpire!=null) {
			Date dat = new Date();
			if (dat.after(mTimExpire))
				try {
					timerEvent(mTimer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			mTimExpire = null;
		}
	}
	
	public void setPass(String pass) throws MyException {
		if (pass==null || pass.length()==0)
			throw new MyException(TTypMyException.EPassInvalid);


		mPass = pass.getBytes();

		mPassMask = new byte[mPass.length];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(mPassMask);

		maskPass();

		mTimExpire = null; //for pass check in getKey();
		try {
			if (mKey!=null)
				getKey();
		} catch (MyException e) {
			Log.e("!!!", "Invalid pass: "+e.getId());
			clearPass();
			throw e;
		}
	}

	public String getPass() throws MyException {
		String res = null;
		if (isPassValid()) {
			maskPass();
			res = new String(mPass);
			maskPass();
		}

		return res;
	}

	public void setKey(String _key) {
		mKey = _key;
	}

	public BigInteger getKey() throws MyException {
		Date dat = new Date();
		if (mTimExpire!=null && dat.after(mTimExpire)) {
			clearPass();
		}
		
		if (!isPassValid())
			throw new MyException(TTypMyException.EPassExpired);

		BigInteger res;
		if (mSecret == null) {
			String key = mAes.decrypt(getPass(), mKey);
			mSecret = mBase64.decode(key.getBytes());

			mSecretMask = new byte[mSecret.length];
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(mSecretMask);

			res = new BigInteger(mSecret);

			maskSecret();
		} else {
			maskSecret();
			res = new BigInteger(mSecret);
			maskSecret();
		}

		return res;
	}

	public boolean isPassValid() {
		return (mPass!=null);
	}

	public void setInterval(long _ms) throws MyException {
		mInterval = _ms;
	}

	public void timerEvent(IMTimer sender) throws Exception {
		if (isPassValid()) {
			clearPass();
			if (mObserver!=null)
				mObserver.passExpired(this);
		}
	}

	public void setObserever(IMPassHolderObserver observer) {
		mObserver = observer;
	}

	public void clearPass() {

		if (mSecret != null)
			Arrays.fill(mSecret, (byte) 0);

		if (mSecretMask != null)
			Arrays.fill(mSecretMask, (byte) 0);

		if (mPass != null)
			Arrays.fill(mPass, (byte) 0);

		if (mPassMask != null)
			Arrays.fill(mPassMask, (byte) 0);

		mTimExpire=null;
		mSecret = null;
		mSecretMask = null;
		mPass = null;
		mPassMask = null;

		mTimer.cancelTimer();
	}

	public void setContext(Context context) {
		mTimer.setContext(context);
	}

	private void maskSecret() {
		for (int i = 0; i < mSecret.length; i++) {
			mSecret[i] = (byte) (mSecret[i] ^ mSecretMask[i]);
		}
	}

	private void maskPass() {
		for (int i = 0; i < mPass.length; i++) {
			mPass[i] = (byte) (mPass[i] ^ mPassMask[i]);
		}
	}
}
