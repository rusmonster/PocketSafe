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
			try {
				if (mState == TTimerState.EBusy) {
					mState = TTimerState.EWait;
					mObserver.timerEvent(CMTimer.this);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void startTimer(long ms) throws MyException {
		if (mState != TTimerState.EWait)
			throw new MyException(TTypMyException.ETimerNotReady);
		
		mHandler.postDelayed(mRunner, ms);
		mState = TTimerState.EBusy;
		Log.d("!!!", "Timer setted: "+ms);
		
	}

	public void cancelTimer() {
		if (mState == TTimerState.EBusy) {
			mHandler.removeCallbacks(mRunner);
			mState = TTimerState.EWait;
			Log.d("!!!", "Timer canceled");
		}

	}

	public void SetObserver(IMTimerObserver observer) {
		mObserver = observer;
	}

}
