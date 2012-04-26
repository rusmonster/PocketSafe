package com.monster.pocketsafe.testlong.smssender;

import android.test.AndroidTestCase;
import android.util.Log;

import com.monster.pocketsafe.sms.sender.CMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSenderObserver;
import com.monster.pocketsafe.testlong.utils.CMTestThread;
import com.monster.pocketsafe.utils.MyException;


public class CMSmsSenderTestLong extends AndroidTestCase implements IMSmsSenderObserver {
	
	private static String PHONE = "+7915"+"104"+"2215"; 
	//private static String PHONE = "5556";
	
	private IMSmsSender mSmsSender;
	private boolean mSentOk;
	private boolean mDelivOk;

	protected void setUp() throws Exception {
		super.setUp();
		
		mSentOk = false;
		mDelivOk = false;
		
		mSmsSender = new CMSmsSender();
		mSmsSender.SetObserver(this);
		mSmsSender.SetContext(getContext());
		mSmsSender.open();
	}

	protected void tearDown() throws Exception {
		
		mSmsSender.close();
		super.tearDown();
	}

	public void testSendSms() throws MyException, InterruptedException {
		
		Log.v("!!!", "sending sms1...");
		
		CMTestThread th = new CMTestThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					mSmsSender.sendSms(PHONE, "testSendSms1",78);
				} catch (MyException e) {
					throw new RuntimeException("Error in sendSms: "+e.getId().toString());
				}
			}
		});
		th.start();
		Thread.sleep(20000);
		th.stopThread();
		

		assertEquals(true, mSentOk);
		assertEquals(true, mDelivOk);
		

	}
	
	public void testSendBigSms() throws MyException, InterruptedException {
		
		Log.v("!!!", "sending sms2...");
		
		mSentOk = false;
		


		CMTestThread th = new CMTestThread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String text = new String();
					for (int i=0; i<300; i++)
						text += String.valueOf(i%10);
					Log.v("!!!", "text: "+text);
					mSmsSender.sendSms(PHONE, text, 81);
				} catch (MyException e) {
					throw new RuntimeException("Error in sendSms: "+e.getId().toString());
				}
			}
		});
		th.start();
		Thread.sleep(20000);
		th.stopThread();
		

		assertEquals(true, mSentOk);
		assertEquals(true, mDelivOk);
	}

	@Override
	public void SmsSenderSent(IMSmsSender sender, int tag) {
		mSentOk = true;
		Log.v("!!!", "SmsSenderSent");
	}

	@Override
	public void SmsSenderSentError(IMSmsSender sender,int tag,  int err) {
		Log.v("!!!", "SmsSenderSentError: "+err);
	}

	@Override
	public void SmsSenderDelivered(IMSmsSender sender, int tag) {
		mDelivOk = true;
		Log.v("!!!", "SmsSenderDelivered");
	}

	@Override
	public void SmsSenderDeliverError(IMSmsSender sender, int tag, int err) {
		mDelivOk = true;
		Log.v("!!!", "SmsSenderDeliverError: "+err);
	}

}
