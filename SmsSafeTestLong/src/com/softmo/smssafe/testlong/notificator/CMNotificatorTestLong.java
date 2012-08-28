package com.softmo.smssafe.testlong.notificator;

import android.test.AndroidTestCase;

import com.softmo.smssafe.main.notificator.CMNotificatorSound;
import com.softmo.smssafe.main.notificator.CMSmsNotificator;
import com.softmo.smssafe.main.notificator.IMSmsNotificator;

import junit.framework.TestCase;

public class CMNotificatorTestLong extends AndroidTestCase {

	private CMNotificatorSound mNotificator;
	
	protected void setUp() throws Exception {
		super.setUp();
		mNotificator = new CMNotificatorSound();
		mNotificator.Init(getContext());
	}

	protected void tearDown() throws Exception {
		mNotificator.Cancel();
		super.tearDown();
	}
	
	public void testAlarm() throws InterruptedException {
		mNotificator.Popup(1);
		Thread.sleep(5000);
	}

	public void testSoundOnly() throws InterruptedException {
		mNotificator.setSoundOnly(true);
		mNotificator.Popup(1);
		Thread.sleep(5000);
	}
}
