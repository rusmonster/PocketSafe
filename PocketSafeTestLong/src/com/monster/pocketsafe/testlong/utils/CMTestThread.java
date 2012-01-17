package com.monster.pocketsafe.testlong.utils;

import android.os.Looper;

public class CMTestThread extends Thread {

	private Runnable mRunnable;
	private Looper mLooper;
	private boolean mIsRun = false;
	
	public CMTestThread(Runnable _runnable) {
		mRunnable = _runnable;
	}
	@Override
	public void run() {
		
		Looper.prepare();
		mLooper = Looper.myLooper();
		
		mIsRun = true;
		
		mRunnable.run();
		Looper.loop();
		
		mIsRun = false;
	}
	
	public void stopThread() {
		if (!mIsRun) throw new RuntimeException("Test Thread not running");
		
		mLooper.quit();
		try {
			join(1000);
		} catch (InterruptedException e) {
		}
		
		if (mIsRun) throw new RuntimeException("Test Thread stop error");
	}

}
