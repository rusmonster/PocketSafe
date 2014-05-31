package com.softmo.smssafe2.testlong.utils;

import android.os.Looper;

public class CMTestThread extends Thread {

	private Runnable mRunnable;
	private Looper mLooper;
	private boolean mIsRun = false;
	
	private void setRun(boolean _run) {
		synchronized (this) {
			mIsRun = _run;
		}
	}
	
	private boolean getRun() {
		boolean run;
		synchronized (this) {
			run = mIsRun;
		}	
		return run;
	}
	
	public CMTestThread(Runnable _runnable) {
		mRunnable = _runnable;
	}
	@Override
	public void run() {
		
		Looper.prepare();
		mLooper = Looper.myLooper();
		
		setRun(true);
		
		mRunnable.run();
		Looper.loop();
		
		setRun(false);
	}
	
	public void stopThread() {
		if (!getRun()) throw new RuntimeException("Test Thread not running");
		
		mLooper.quit();
		try {
			join(1000);
		} catch (InterruptedException e) {
		}
		
		if (getRun()) throw new RuntimeException("Test Thread stop error");
	}	
}
