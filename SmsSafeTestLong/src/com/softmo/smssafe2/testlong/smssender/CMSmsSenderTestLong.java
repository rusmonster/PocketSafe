package com.softmo.smssafe2.testlong.smssender;

import android.test.AndroidTestCase;
import android.util.Log;

import com.softmo.smssafe2.smssender.CMSmsSender;
import com.softmo.smssafe2.smssender.IMSmsSender;
import com.softmo.smssafe2.smssender.IMSmsSenderObserver;
import com.softmo.smssafe2.testlong.utils.CMTestThread;
import com.softmo.smssafe2.utils.MyException;


public class CMSmsSenderTestLong extends AndroidTestCase implements IMSmsSenderObserver {
	
	private static final String PHONE;
	//private static String PHONE = "5556";
	
	private IMSmsSender mSmsSender;
	private boolean mSentOk;
	private boolean mDelivOk;

    static
    {
        byte[] p = new byte[] { '+', 0x37, 0x39, 0x32, 0x36, 0x31, 0x33, 0x36, 0x31, 0x30, 0x34, 0x30 };
        PHONE = new String(p);
    }

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
