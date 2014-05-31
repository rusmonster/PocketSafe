package com.softmo.smssafe2.testlong.timers;

import com.softmo.smssafe2.testlong.utils.CMTestThread;
import com.softmo.smssafe2.utils.CMTimer2;
import com.softmo.smssafe2.utils.IMTimer;
import com.softmo.smssafe2.utils.IMTimerObserver;
import com.softmo.smssafe2.utils.MyException;

import junit.framework.TestCase;

public class CMTimer2TestLong extends TestCase {
	
	private CMTimer2 mTimer;
	private int mCntTicks;
	private IMTimerObserver mObserver = new IMTimerObserver() {
		
		@Override
		public void timerEvent(IMTimer sender) throws Exception {
			mCntTicks++;
		}
	};
	
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mCntTicks=0;
	}



	public void testTimer() throws Exception {
			CMTestThread th = new CMTestThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					mTimer = new CMTimer2();
					mTimer.SetObserver(mObserver);
					mTimer.startTimer(1000);
				} catch (MyException e) {
					throw new RuntimeException("Error in startGenerateKeyPair");
				}
			}
		});
		th.start();
		
		int k=0;
		while (k++<30 && mCntTicks==0)
			Thread.sleep(100);
		th.stopThread();
		
		assertEquals(1, mCntTicks);
	}
	
	public void testTimerCancel() throws Exception {
		CMTestThread th = new CMTestThread(new Runnable() {
		
			@Override
			public void run() {
				try {
					mTimer = new CMTimer2();
					mTimer.SetObserver(mObserver);
					mTimer.startTimer(1000);
					mTimer.cancelTimer();
				} catch (MyException e) {
					throw new RuntimeException("Error in startGenerateKeyPair");
				}
			}
		});
		th.start();
		
		Thread.sleep(3000);
		th.stopThread();
		
		assertEquals(0, mCntTicks);
	}
	
	public void testTimerCancelTimer() throws Exception {
		CMTestThread th = new CMTestThread(new Runnable() {
		
			@Override
			public void run() {
				try {
					mTimer = new CMTimer2();
					mTimer.SetObserver(mObserver);
					mTimer.startTimer(1000);
					mTimer.cancelTimer();
					mTimer.startTimer(2000);
				} catch (MyException e) {
					throw new RuntimeException("Error in startGenerateKeyPair");
				}
			}
		});
		th.start();
		
		int k=0;
		while (k++<30 && mCntTicks==0)
			Thread.sleep(100);
		
		th.stopThread();
		
		assertEquals(1, mCntTicks);
	}	
}
