package com.monster.pocketsafe.utils;

import com.monster.pocketsafe.utils.MyException.TTypMyException;

import android.os.Handler;
import android.util.Log;

public class CMTimer implements IMTimer {

	private IMTimerObserver mObserver;
	private final Handler mHandler = new Handler();
	private enum TTimerState {
		EWait,
		EBusy
	}
	private TTimerState mState = TTimerState.EWait;
	
	private final Runnable mRunner = new Runnable() {
		
		public void run() {
			mState = TTimerState.EWait;
			try {
				mObserver.timerEvent(CMTimer.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void startTimer(long ms) throws MyException {
		if (mState != TTimerState.EWait)
			throw new MyException(TTypMyException.ETimerNotReady);
		
		mHandler.postDelayed(mRunner, ms);
		Log.d("!!!", "Timer setted: "+ms);
		
	}

	public void cancelTimer() {
		if (mState == TTimerState.EBusy) {
			mHandler.removeCallbacks(mRunner);
			mState = TTimerState.EWait;
		}

	}

	public void SetObserver(IMTimerObserver observer) {
		mObserver = observer;
	}

}
