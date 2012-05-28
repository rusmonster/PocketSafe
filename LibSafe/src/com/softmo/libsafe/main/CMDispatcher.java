package com.softmo.libsafe.main;

import java.util.ArrayList;

import android.util.Log;

public class CMDispatcher implements IMDispatcherSender {
	
	ArrayList<IMListener> mListeners = new ArrayList<IMListener>();

	public void addListener(IMListener listener) {
		if ( !mListeners.contains(listener) )
			mListeners.add(listener);
	}

	public void delListener(IMListener listener) {
		mListeners.remove(listener);

	}

	public void pushEvent(IMEvent event) {
		int cnt = mListeners.size();
		Log.v("!!!", "Listeners count: "+cnt);
		for (int i=0; i<cnt; i++) {
			IMListener l = mListeners.get(i);
			
			try {
				l.listenerEvent(event);
			} catch (Exception e) {
				Log.d("!!!", "Listener Exception");
				e.printStackTrace();
			}
		}

	}

}
