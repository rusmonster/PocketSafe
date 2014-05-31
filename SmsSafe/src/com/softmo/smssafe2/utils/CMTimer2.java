package com.softmo.smssafe2.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

import com.softmo.smssafe2.utils.MyException.TTypMyException;

public class CMTimer2 implements IMTimer {

	private IMTimerObserver mObserver;
	private enum TTimer2State {
		EWait,
		EBusy
	}
	private TTimer2State mState = TTimer2State.EWait;
	
	private Timer mTimer;
	private Handler mHandler = new Handler();

	private Runnable mRunTick = new Runnable() {
		public void run() {
			if (mState == TTimer2State.EBusy) {
				mState = TTimer2State.EWait;
				if (mObserver!=null)
					try {
						Log.d("!!!", "Timer2 fires timerEvent");
						mObserver.timerEvent(CMTimer2.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	};
	
	public void SetObserver(IMTimerObserver observer) {
		mObserver = observer;

	}

	public void startTimer(long ms) throws MyException {
		if (mState != TTimer2State.EWait)
			throw new MyException(TTypMyException.ETimerNotReady);
		
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.d("!!!", "Timer2 post");
				mHandler.post(mRunTick);
			}

		}, ms);		
		mState = TTimer2State.EBusy;
		Log.d("!!!", "Timer2 setted: "+ms);
		
	}

	public void cancelTimer() {
		if (mState == TTimer2State.EBusy) {
			mState = TTimer2State.EWait;
			mTimer.cancel();
			mTimer.purge();
			Log.d("!!!", "Timer2 canceled");
		}

	}

}