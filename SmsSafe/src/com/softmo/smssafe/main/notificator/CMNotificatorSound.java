package com.softmo.smssafe.main.notificator;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

public class CMNotificatorSound extends CMSmsNotificator implements
		IMNotificatorSound {

	private boolean mSoundOnly;
	private int mCntNewSms=0;
	
	public void setSoundOnly(boolean soundonly) {
		mSoundOnly = soundonly;
		if (mSoundOnly)
			Cancel();
		else
			Update(mCntNewSms);
	}

	public void Cancel() {
		super.Update(0);
	}

	private void playSound() {
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getCotntext(), notification);
        r.play();
    	} catch (Exception e) {}	
	}
	
	@Override
	public void Popup(int cnt_newsms) {
		mCntNewSms = cnt_newsms;
		
		if (mSoundOnly)
			playSound();
		else
			super.Popup(cnt_newsms);
	}

	@Override
	public void Update(int cnt_newsms) {
		mCntNewSms = cnt_newsms;
		
		if (!mSoundOnly)
			super.Update(cnt_newsms);
	}

	
}
