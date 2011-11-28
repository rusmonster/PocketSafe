package com.monster.pocketsafe.testlong.dbengine;

import android.os.Looper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.monster.pocketsafe.sms.sender.CMSmsSender;
import com.monster.pocketsafe.sms.sender.IMSmsSenderObserver;
import com.monster.pocketsafe.utils.MyException;
import com.monster.pocketsafe.utils.MyException.TTypMyException;


public class CMSmsSenderTestLong extends AndroidTestCase implements IMSmsSenderObserver {
	
	private CMSmsSender mSmsSender;
	private boolean mSentOk;
	private Looper mLooper;

	protected void setUp() throws Exception {
		super.setUp();
		
		mLooper = Looper.myLooper();
		
		mSmsSender = new CMSmsSender();
		mSmsSender.SetObserver(this);
		mSmsSender.SetContext(getContext());
		mSmsSender.open();
	}

	protected void tearDown() throws Exception {
		
		mSmsSender.close();
		super.tearDown();
	}

	public void testOpenClose() throws MyException {
		Log.v("!!!", "testOpenClose");
		mSmsSender.close();
		mSmsSender.open();
		
		mSentOk = false;
		mSmsSender.sendSms("+79261361040", "testOpenClose");
		
		Looper.loop();
		checkLooper();
		
		assertEquals(true, mSentOk);
	}
	
	private void checkLooper() {
		mLooper = Looper.myLooper();
		if (mLooper == null) {
			Log.d("!!!", "Recreating looper");
			Looper.prepare();
			mLooper = Looper.myLooper();
			assertNotNull(mLooper);
		}
		
	}

	public void testSendSms() throws MyException {
		
		Log.v("!!!", "sending sms1...");
		
		mSentOk = false;
		mSmsSender.sendSms("+79261361040", "testSendSms1");
		
		Looper.loop();
		checkLooper();
		assertEquals(true, mSentOk);
		
		Log.v("!!!", "sending sms2...");
		mSentOk = false;
		mSmsSender.sendSms("+79261361040", "testSendSms2");
		
		Looper.loop();
		assertEquals(true, mSentOk);
	}

	@Override
	public void SmsSenderSent(CMSmsSender sender) {
		mSentOk = true;
		Log.v("!!!", "SmsSenderSent");
		mLooper.quit();
	}

	@Override
	public void SmsSenderSentError(CMSmsSender sender, TTypMyException err) {
		Log.v("!!!", "SmsSenderSentError: "+err);
		mLooper.quit();
	}

	@Override
	public void SmsSenderDelivered(CMSmsSender sender) {
		//mSentOk = true;
		Log.v("!!!", "SmsSenderDelivered");
		//mLooper.quit();
	}

	@Override
	public void SmsSenderDeliverError(CMSmsSender sender, TTypMyException err) {
		Log.v("!!!", "SmsSenderDeliverError: "+err);
		//mLooper.quit();
	}

}
